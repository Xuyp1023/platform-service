package com.betterjr.modules.customer.dubbo;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.customer.ICustRelationService;
import com.betterjr.modules.customer.service.CustRelationAuditService;
import com.betterjr.modules.customer.service.CustRelationService;

@Service(interfaceClass = ICustRelationService.class)
public class CustRelationDubboService implements ICustRelationService {

    @Autowired
    private CustRelationService custRelationService;

    @Autowired
    private CustRelationAuditService custRelationAuditService;

    @Override
    public String webFindFactorStatus(Long anCustNo) {

        return AjaxObject.newOk("开通保理业务状态查询成功", custRelationService.findFactorStatus(anCustNo)).toJson();
    }

    @Override
    public String webSaveCustRelation(Long anCustNo, String anProviderCustList, String anFactorCustList, String anPostscript) {

        return AjaxObject.newOk("开通保理融资业务申请成功", custRelationService.saveCustRelation(anCustNo, anProviderCustList, anFactorCustList, anPostscript))
                .toJson();
    }

    @Override
    public String webQueryAuditWorkflow(Long anCustNo) {

        return AjaxObject.newOk("开通保理融资业务审批流程查询成功", custRelationAuditService.queryAuditWorkflow(anCustNo)).toJson();
    }

    @Override
    public String webQueryRelationAccept(String anBusinStatus, String anFlag, int anPageNum, int anPageSize) {

        return AjaxObject.newOkWithPage("客户白名单受理列表查询成功", custRelationService.queryRelationAccept(anBusinStatus, anFlag, anPageNum, anPageSize))
                .toJson();
    }

    @Override
    public String webQueryRelationAudit(String anBusinStatus, String anFlag, int anPageNum, int anPageSize) {

        return AjaxObject.newOkWithPage("客户白名单审批列表查询成功", custRelationService.queryRelationAudit(anBusinStatus, anFlag, anPageNum, anPageSize))
                .toJson();
    }

    @Override
    public String webSaveRelationAccept(Long anId, String anAuditOpinion) {

        return AjaxObject.newOk("客户白名单受理成功", custRelationService.saveRelationAccept(anId, anAuditOpinion)).toJson();
    }

    @Override
    public String webSaveRelationAudit(Long anId, String anAuditOpinion) {

        return AjaxObject.newOk("客户白名单审批成功", custRelationService.saveRelationAudit(anId, anAuditOpinion)).toJson();
    }

    @Override
    public String webSaveRefuseAcceptRelation(Long anId, String anAuditOpinion) {

        return AjaxObject.newOk("客户白名单受理驳回成功", custRelationService.saveRefuseAcceptRelation(anId, anAuditOpinion)).toJson();
    }

    @Override
    public String webSaveRefuseAuditRelation(Long anId, String anAuditOpinion) {

        return AjaxObject.newOk("客户白名单审批驳回成功", custRelationService.saveRefuseAuditRelation(anId, anAuditOpinion)).toJson();
    }

    @Override
    public String webQueryCoreKeyAndValue(Long anCustNo) {

        return AjaxObject.newOk("核心企业下拉列表查询成功", custRelationService.queryCoreKeyAndValue(anCustNo)).toJson();
    }

    @Override
    public String webQueryFactorKeyAndValue(Long anCustNo) {

        return AjaxObject.newOk("保理机构下拉列表查询成功", custRelationService.queryFactorKeyAndValue(anCustNo)).toJson();
    }

    @Override
    public String webQueryProviderRelation(Long anCustNo) {

        return AjaxObject.newOk("客户与电子合同服务商关系查询成功", custRelationService.queryProviderRelation(anCustNo)).toJson();
    }

    @Override
    public String webQueryCustRelation(Long anCustNo) {

        return AjaxObject.newOk("客户关系查询成功", custRelationService.queryCustRelation(anCustNo)).toJson();
    }

    @Override
    public String webQueryFacotrCoreRelation(Long anFactorNo) {

        return AjaxObject.newOk("保理机构与核心企业关系查询成功", custRelationService.queryFactorCoreRelation(anFactorNo)).toJson();
    }

    @Override
    public String webQueryFactorCustRelation(Long anFactorNo, String anCreditType) {

        return AjaxObject.newOk("保理机构关系客户查询成功", custRelationService.queryFactorCustRelation(anFactorNo, anCreditType)).toJson();
    }

}
