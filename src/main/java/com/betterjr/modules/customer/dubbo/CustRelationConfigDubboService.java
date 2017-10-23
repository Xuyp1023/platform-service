package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.data.PlatformBaseRuleType;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.customer.ICustRelationConfigService;
import com.betterjr.modules.customer.data.FactorBusinessRequestData;
import com.betterjr.modules.customer.service.CustRelationConfigService;

@Service(interfaceClass = ICustRelationConfigService.class)
public class CustRelationConfigDubboService implements ICustRelationConfigService {

    Logger logger = LoggerFactory.getLogger(CustRelationConfigDubboService.class);

    @Autowired
    private CustRelationConfigService relationConfigService;

    @Override
    public String webFindCustByPlatform(final String anCustType) {
        return AjaxObject.newOk("查询客户信息", relationConfigService.findCustByPlatform(anCustType)).toJson();
    }

    @Override
    public String webFindCustInfo(final String anCustType, final Long anCustNo, final String anCustName) {
        // 如果选择的是供应商或经销商则调用之前的方法
        if (StringUtils.equalsIgnoreCase(anCustType, PlatformBaseRuleType.SUPPLIER_USER.toString())
                || StringUtils.equalsIgnoreCase(anCustType, PlatformBaseRuleType.SELLER_USER.toString())) {
            return AjaxObject
                    .newOk("查询客户关系信息", relationConfigService.findCustInfoOld(anCustType, anCustNo, anCustName))
                    .toJson();
        } else {
            return AjaxObject.newOk("查询客户关系信息", relationConfigService.findCustInfo(anCustType, anCustNo, anCustName))
                    .toJson();
        }
    }

    @Override
    public String webFindCustType() {
        return AjaxObject.newOk("查询客户需要关联的客户类型", relationConfigService.findCustType()).toJson();
    }

    /*****
     * 添加关联关系
     * @param anCustType 客户所属类型
     * @param anCustNo 客户号
     * @param anRelationCustNo 关联客户号
     * @return
     */
    @Override
    public String webAddCustRelation(final String anCustType, final Long anCustNo, final String anRelationCustStr) {
        if (relationConfigService.addCustRelation(anCustType, anCustNo, anRelationCustStr)) {
            return AjaxObject.newOk("客户关系添加成功").toJson();
        } else {
            return AjaxObject.newError("客户关系添加 失败").toJson();
        }
    }

    /****
     * 分页查询客户关系信息
     * @param anCustNo
     * @return
     */
    @Override
    public String webQueryCustRelation(final Long anCustNo, final String anFlag, final int anPageNum,
            final int anPageSize, final String anRelationType) {
        final PlatformBaseRuleType role = UserUtils.getUserRole();
        return AjaxObject.newOkWithPage(
                "分页查询客户关系信息",
                relationConfigService.queryCustRelationInfo(anCustNo, role, anRelationType, anFlag, anPageNum,
                        anPageSize)).toJson();
    }

    /****
     * 查询当前客户的类型
     * @param anCustNo 客户号
     * @return
     */
    @Override
    public String webFindCustTypeByCustNo() {
        String type = "";
        if (UserUtils.supplierUser()) {
            type = String.valueOf(PlatformBaseRuleType.SUPPLIER_USER);
        } else if (UserUtils.coreUser()) {
            type = String.valueOf(PlatformBaseRuleType.CORE_USER);
        } else if (UserUtils.sellerUser()) {
            type = String.valueOf(PlatformBaseRuleType.SELLER_USER);
        } else if (UserUtils.factorUser()) {
            type = String.valueOf(PlatformBaseRuleType.FACTOR_USER);
        } else if (UserUtils.platformUser()) {
            type = String.valueOf(PlatformBaseRuleType.PLATFORM_USER);
        }
        logger.info("type:" + type);
        return AjaxObject.newOk("获取当前客户类型", type).toJson();
    }

    /***
     * 查询电子合同服务商客户
     * @return
     */
    @Override
    public String webFindElecAgreementServiceCust() {
        return AjaxObject.newOk("查询电子合同服务商客户", relationConfigService.findElecAgreementServiceCust()).toJson();
    }

    /***
     * 查询临时审核文件
     * @param anFactorNo
     * @param anCustNo
     * @return
     */
    @Override
    public String webFindCustAduitTempFile(final Long anRelateCustNo, final Long anSelectCustNo) {
        return AjaxObject.newOk("获取审核文件", relationConfigService.findCustAduitTemp(anRelateCustNo, anSelectCustNo))
                .toJson();
    }

    /***
     * 保存临时文件
     * @param anRelateCustNo
     * @param anCustNo
     * @param anFileTypeName
     * @param anFileMediaId
     * @return
     */
    @Override
    public String webAddCustAduitTempFile(final Long anRelateCustNo, final String anFileTypeName,
            final String anFileMediaId, final String anCustType) {
        return AjaxObject.newOk("文件保存成功",
                relationConfigService.addCustTempFile(anRelateCustNo, anFileTypeName, anFileMediaId, anCustType))
                .toJson();
    }

    /****
     * 删除附件
     * @param anId
     * @return
     */
    @Override
    public String webSaveDeleteCustAduitTempFile(final Long anId) {
        if (relationConfigService.saveDeleteCustAduitTempFile(anId)) {
            return AjaxObject.newOk("文件删除成功").toJson();
        } else {
            return AjaxObject.newError("文件删除失败").toJson();
        }
    }

    /*****
     * 添加保理关联关系
     * @param anFactorCustType 保理客户所属类型
     * @param anWosCustType 电子服务客户所属类型
     * @param anCustNo 客户号
     * @param anFactorCustNo 关联保理公司的客户号
     * @param anRelationCustNo 关联电子合同服务的客户号
     * @return
     */
    @Override
    public String webAddFactorCustRelation(final String anFactorCustType, final String anWosCustType,
            final String anFactorCustStr, final String anWosCustStr, final Long anCustNo) {
        if (relationConfigService.addFactorCustRelation(anFactorCustType, anWosCustType, anFactorCustStr, anWosCustStr,
                anCustNo)) {
            return AjaxObject.newOk("客户关系添加成功").toJson();
        } else {
            return AjaxObject.newError("客户关联关系已经存在").toJson();
        }
    }

    /***
     * 查询保理业务申请基础数据
     * @param anCustNo 申请客户号
     * @return
     */
    @Override
    public String webFindFactorBusinessRequestData(final Long anCustNo) {
        return AjaxObject.newOk("保理业务申请基础数据", relationConfigService.findFactorRequestInfo(anCustNo)).toJson();
    }

    /***
     * 添加客户文件关系
     * @param anRelationCustNo 关联的客户号
     * @param anFileIds 上传的文件列表(以,分隔)
     * @param anCustType 客户类型
     */
    @Override
    public String webSaveCustAduitTempFile(final Long anRelateCustNo, final String anFileIds, final String anCustType,
            final Long anCustNo) {
        relationConfigService.saveCustFileAduitTemp(anRelateCustNo, anFileIds, anCustType, anCustNo);
        return AjaxObject.newOk("添加客户文件关系").toJson();
    }

    /***
     * 查询关联临时文件
     * @param anCustNo 关联客户号
     * @return
     */
    @Override
    public String webFindRelateAduitTempFile(final Long anCustNo) {
        return AjaxObject.newOk("查询附件", relationConfigService.findRelateAduitTempFile(anCustNo)).toJson();
    }

    /***
     * 受理审批
     */
    @Override
    public String webSaveAcceptAduit(final Map<String, Object> anMap) {
        relationConfigService.saveAcceptAduit(anMap);
        return AjaxObject.newOk("成功").toJson();
    }

    /***
     * 查询审核/受理记录
     * @param anCustNo
     * @return
     */
    @Override
    public String webFindCustRelateAduitRecord(final Long anCustNo, final Long anSelectCustNo, final String anRelateType) {
        return AjaxObject.newOk("查询审批记录",
                relationConfigService.findCustRelateAduitRecord(anCustNo, anSelectCustNo, anRelateType)).toJson();
    }

    @Override
    public FactorBusinessRequestData findBusinessCustInfo(final Long anCustNo) {
        return relationConfigService.findFactorRequestInfo(anCustNo);
    }

    @Override
    public String webFindAgencyFileType(final String anAgencyNo) {
        return AjaxObject.newOk("查询机构文件类型", relationConfigService.findAgencyFileType(anAgencyNo)).toJson();
    }

}
