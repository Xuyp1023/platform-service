package com.betterjr.modules.customer.dubbo;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.customer.ICustRelationConfigService;
import com.betterjr.modules.customer.service.CustRelationConfigService;

@Service(interfaceClass=ICustRelationConfigService.class)
public class CustRelationConfigDubboService implements ICustRelationConfigService {

    @Autowired
    private CustRelationConfigService relationConfigService;
    
    @Override
    public String webFindCustInfo(String anCustType, Long anCustNo) {
        return AjaxObject.newOk("查询客户关系信息", relationConfigService.findCustInfo(anCustType, anCustNo)).toJson();
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
    public String webAddCustRelation(String anCustType,Long anCustNo,Long anRelationCustNo){
        if(relationConfigService.addCustRelation(anCustType, anCustNo, anRelationCustNo)){
            return AjaxObject.newOk("客户关系添加成功").toJson();
        }else{
            return AjaxObject.newOk("客户关系添加 失败").toJson();
        }
    }
    
    /****
     * 分页查询客户关系信息
     * @param anCustNo
     * @return
     */
    public String webQueryCustRelation(Long anCustNo,String anFlag,int anPageNum,int anPageSize){
        return AjaxObject.newOkWithPage("分页查询客户关系信息", relationConfigService.queryCustRelationInfo(anCustNo, anFlag, anPageNum,anPageSize)).toJson();
    }

}
