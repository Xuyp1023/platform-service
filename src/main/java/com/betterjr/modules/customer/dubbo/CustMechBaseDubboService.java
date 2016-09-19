package com.betterjr.modules.customer.dubbo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.data.SimpleDataEntity;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.customer.ICustMechBaseService;
import com.betterjr.modules.customer.constants.CustomerConstants;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustMechBase;
import com.betterjr.modules.customer.entity.CustMechBaseTmp;
import com.betterjr.modules.customer.helper.ChangeDetailBean;
import com.betterjr.modules.customer.service.CustChangeService;
import com.betterjr.modules.customer.service.CustMechBaseService;
import com.betterjr.modules.customer.service.CustMechBaseTmpService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

/**
 * 公司基本信息
 *
 * @author liuwl
 *
 */
@Service(interfaceClass = ICustMechBaseService.class)
public class CustMechBaseDubboService implements ICustMechBaseService {
    private static Logger logger = LoggerFactory.getLogger(CustMechBaseDubboService.class);

    @Resource
    private CustMechBaseService baseService;

    @Resource
    private CustMechBaseTmpService baseTmpService;

    @Resource
    private CustChangeService changeService;

    @Override
    public String webCheckOrgType(final String anRole) {
        boolean flag = false;
        switch (anRole.trim()) {
        case "FACTOR_USER":
            flag = UserUtils.factorUser();
            break;
        case "SELLER_USER":
            flag = UserUtils.sellerUser();
            break;
        case "CORE_USER":
            flag = UserUtils.coreUser();
            break;
        case "SUPPLIER_USER":
            flag = UserUtils.supplierUser();
            break;
        case "PLATFORM_USER":
            flag = UserUtils.platformUser();
            break;
        default:
            return AjaxObject.newError("检查机构类型错误").toJson();
        }

        return AjaxObject.newOk("检查机构类型成功", flag).toJson();
    }

    @Override
    public String webQueryCustInfo() {
        final Collection<CustInfo> custInfos = baseService.queryCustInfo();
        return AjaxObject.newOk("查询操作员所有的公司列表成功", custInfos).toJson();
    }

    @Override
    public String webQueryCustInfoSelect() {
        final Collection<SimpleDataEntity> custInfos = baseService.queryCustInfoSelect();
        return AjaxObject.newOk("查询操作员所有的公司列表成功", custInfos).toJson();
    }

    @Override
    public String webFindBaseInfo(final Long anCustNo) {
        final CustMechBase custMechBase = baseService.findCustMechBaseByCustNo(anCustNo);
        return AjaxObject.newOk("公司基本信息-详情查询 成功", custMechBase).toJson();
    }

    @Override
    public String webFindChangeApply(final Long anId) {
        final CustChangeApply changeApply = changeService.findChangeApply(anId, CustomerConstants.ITEM_BASE);
        // 通过changeApply找到
        final Long tmpId = Long.valueOf(changeApply.getTmpIds());

        final CustMechBaseTmp nowData = baseTmpService.findCustMechBaseTmp(tmpId);
        final CustMechBaseTmp befData = baseTmpService.findCustMechBaseTmpPrevVersion(nowData);

        final ChangeDetailBean<CustMechBaseTmp> changeDetailBean = new ChangeDetailBean<>();
        changeDetailBean.setChangeApply(changeApply);
        changeDetailBean.setNowData(nowData);
        changeDetailBean.setBefData(befData);

        return AjaxObject.newOk("公司基本信息-变更详情查询 成功", changeDetailBean).toJson();
    }

    @Override
    public String webQueryChangeApply(final Long anCustNo, final int anFlag, final int anPageNum, final int anPageSize) {
        final Page<CustChangeApply> changeApplys = changeService.queryChangeApply(anCustNo, CustomerConstants.ITEM_BASE, anFlag, anPageNum,
                anPageSize);
        return AjaxObject.newOkWithPage("公司基本信息-变更列表 成功", changeApplys).toJson();
    }

    @Override
    public String webAddChangeApply(final Map<String, Object> anParam, final String anFileList) {
        final CustMechBaseTmp custMechBaseTmp = (CustMechBaseTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("公司基本信息-变更申请 成功", baseTmpService.addChangeApply(custMechBaseTmp, anFileList)).toJson();
    }

    @Override
    public String webSaveChangeApply(final Map<String, Object> anParam, final Long anApplyId, final String anFileList) {
        final CustMechBaseTmp custMechBaseTmp = (CustMechBaseTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("公司基本信息-变更修改 成功", baseTmpService.saveChangeApply(custMechBaseTmp, anApplyId, anFileList)).toJson();
    }

    @Override
    public String webFindInsteadRecord(final Long anInsteadRecordId) {
        return AjaxObject.newOk("公司基本信息-代录详情 成功", baseTmpService.findCustMechBaseTmpByInsteadRecord(anInsteadRecordId)).toJson();
    }

    @Override
    public String webAddInsteadRecord(final Map<String, Object> anParam, final Long anInsteadRecordId, final String anFileList) {
        final CustMechBaseTmp custMechBaseTmp = (CustMechBaseTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("公司基本信息-添加代录 成功", baseTmpService.addInsteadRecord(custMechBaseTmp, anInsteadRecordId, anFileList)).toJson();
    }

    @Override
    public String webSaveInsteadRecord(final Map<String, Object> anParam, final Long anInsteadRecordId, final String anFileList) {
        final CustMechBaseTmp custMechBaseTmp = (CustMechBaseTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("公司基本信息-代录修改 成功", baseTmpService.saveInsteadRecord(custMechBaseTmp, anInsteadRecordId, anFileList)).toJson();
    }

    /* (non-Javadoc)
     * @see com.betterjr.modules.customer.ICustMechBaseService#webFindWechatLoginInfo()
     */
    @Override
    public String webFindWechatLoginInfo() {
        final CustOperatorInfo operator = UserUtils.getOperatorInfo();
        final CustInfo custInfo = Collections3.getFirst(UserUtils.getOperatorContextInfo().findCustInfoList());
        final Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("operId", operator.getId());
        resultMap.put("operName", operator.getName());
        resultMap.put("custNo", custInfo.getCustNo());
        resultMap.put("custName", custInfo.getCustName());

        return AjaxObject.newOk("微信用户信息获取 成功", resultMap).toJson();
    }
}
