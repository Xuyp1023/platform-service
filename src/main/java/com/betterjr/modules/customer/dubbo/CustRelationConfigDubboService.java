package com.betterjr.modules.customer.dubbo;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.data.PlatformBaseRuleType;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.customer.ICustRelationConfigService;
import com.betterjr.modules.customer.service.CustRelationConfigService;

@Service(interfaceClass=ICustRelationConfigService.class)
public class CustRelationConfigDubboService implements ICustRelationConfigService {

    @Autowired
    private CustRelationConfigService relationConfigService;
    
    @Override
    public String webFindCustInfo(String anCustType, Long anCustNo,String anCustName) {
        return AjaxObject.newOk("查询客户关系信息", relationConfigService.findCustInfo(anCustType, anCustNo,anCustName)).toJson();
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
    public String webAddCustRelation(String anCustType,Long anCustNo,String anRelationCustStr){
        if(relationConfigService.addCustRelation(anCustType, anCustNo, anRelationCustStr)){
            return AjaxObject.newOk("客户关系添加成功").toJson();
        }else{
            return AjaxObject.newError("客户关系添加 失败").toJson();
        }
    }
    
    /****
     * 分页查询客户关系信息
     * @param anCustNo
     * @return
     */
    public String webQueryCustRelation(Long anCustNo,String anFlag,int anPageNum,int anPageSize,String anRelationType){
        return AjaxObject.newOkWithPage("分页查询客户关系信息", relationConfigService.queryCustRelationInfo(anCustNo,anRelationType,anFlag, anPageNum,anPageSize)).toJson();
    }
    
    /****
     * 查询当前客户的类型 
     * @param anCustNo 客户号
     * @return
     */
    public String webFindCustTypeByCustNo(){
        String type="";
        if(UserUtils.supplierUser()){
            type=String.valueOf(PlatformBaseRuleType.SUPPLIER_USER);
        }else if(UserUtils.coreUser()){
            type=String.valueOf(PlatformBaseRuleType.CORE_USER);
        }else if(UserUtils.sellerUser()){
            type= String.valueOf(PlatformBaseRuleType.SELLER_USER);
        }else if(UserUtils.factorUser()){
            type= String.valueOf(PlatformBaseRuleType.FACTOR_USER);
        }else if(UserUtils.platformUser()){
            type= String.valueOf(PlatformBaseRuleType.PLATFORM_USER);
        }
        return AjaxObject.newOk("获取当前客户类型",type).toJson();
    }
    
    /***
     * 查询电子合同服务商客户
     * @return
     */
    public String webFindElecAgreementServiceCust(){
        return AjaxObject.newOk("查询电子合同服务商客户",relationConfigService.findElecAgreementServiceCust()).toJson();
    }
    
    /***
     * 查询临时审核文件
     * @param anFactorNo
     * @param anCustNo
     * @return
     */
    public String webFindCustAduitTempFile(Long anRelateCustNo,Long anCustNo){
        return AjaxObject.newOk("获取审核文件",relationConfigService.findCustAduitTemp(anRelateCustNo, anCustNo)).toJson();
    }
    
    /***
     * 保存临时文件
     * @param anRelateCustNo
     * @param anCustNo
     * @param anFileTypeName
     * @param anFileMediaId
     * @return
     */
    public String webAddCustAduitTempFile(Long anRelateCustNo,Long anCustNo,String anFileTypeName, String anFileMediaId,String anCustType){
        return AjaxObject.newOk("文件保存成功",relationConfigService.addCustTempFile(anRelateCustNo, anCustNo, anFileTypeName, anFileMediaId,anCustType)).toJson();
    }
    
    /****
     * 删除附件
     * @param anId
     * @return
     */
    public String webSaveDeleteCustAduitTempFile(Long anId){
        if(relationConfigService.saveDeleteCustAduitTempFile(anId)){
            return AjaxObject.newOk("文件删除成功").toJson();
        }else{
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
    public String webAddFactorCustRelation(String anFactorCustType,String anWosCustType,Long anCustNo,String anFactorCustStr,String anWosCustStr){
        if(relationConfigService.addFactorCustRelation(anFactorCustType,anWosCustType, anCustNo,anFactorCustStr,anWosCustStr)){
            return AjaxObject.newOk("客户关系添加成功").toJson();
        }else{
            return AjaxObject.newError("客户关联关系已经存在").toJson();
        }
    }
}
