package com.betterjr.modules.customer.dubbo;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.data.SimpleDataEntity;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.customer.ICustRelationService;

import com.betterjr.modules.customer.data.CustRelationData;

import com.betterjr.modules.customer.entity.CustRelation;

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
    public String webQuerySupplierByCore(Long anCoreCustNo) {

        return AjaxObject.newOk("供应商下拉列表查询成功", custRelationService.querySupplierByCore(anCoreCustNo)).toJson();
    }

    @Override
    public String webQuerySellerByCore(Long anCoreCustNo) {

        return AjaxObject.newOk("经销商下拉列表查询成功", custRelationService.querySellerByCore(anCoreCustNo)).toJson();
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

    @Override
    public String webQueryFactorAllCust(Long anFactorNo) {

        return AjaxObject.newOk("保理机构关系客户查询成功", custRelationService.webQueryFactorAllCust(anFactorNo)).toJson();
    }

    /****
     * 查询客户号根据类型返回关联关系信息
     * 
     * @param anCustNo
     *            关系客户号
     * @param anCreditType
     *            关系类型
     * @return 关系列表
     */
    public List<CustRelationData> webQueryCustRelationData(Long anCustNo, String anCreditType) {
        return custRelationService.queryCustRelationData(anCustNo, anCreditType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.betterjr.modules.customer.ICustRelationService#webQueryFactorList()
     */
    @Override
    public String webQueryFactorRelation(Long anCustNo) {
        return AjaxObject.newOk("客户与保理机构关系查询成功", custRelationService.queryFactorRelation(anCustNo)).toJson();
    }

    @Override
    public String webQueryFactorRelation() {
        return AjaxObject.newOk("客户与保理机构关系查询成功", custRelationService.queryFactorRelation()).toJson();
    }

    @Override
    public CustRelation findOneRelation(Long anCustNo, Long anRelateCustno, String anPartnerCustNo) {
        // TODO Auto-generated method stub
        return custRelationService.findOneRelation(anCustNo, anRelateCustno, anPartnerCustNo);
    }

    @Override
    public boolean saveFactorRelationStatus(Long anCustNo, String anScfId, String anStatus, String anFactorNo) {
        // TODO Auto-generated method stub
        return this.custRelationService.saveFactorRelationStatus(anCustNo, anScfId, anStatus, anFactorNo);
    }

    @Override
    public Long findCustNoByBankInfo(String anBankAccountName, String anBankAccount) {
        // TODO Auto-generated method stub
        return this.custRelationService.findCustNoByBankInfo(anBankAccountName, anBankAccount);
    }

    @Override
    public boolean saveAndCheckCust(Map<String, Object> anValues, String anCoreCustName, Long anCoreCustNo) {

        return custRelationService.saveAndCheckCust(anValues, anCoreCustName, anCoreCustNo);
    }

    @Override
    public Long findCustNoByScfId(String anScfId, String anAgencyNo) {

        return custRelationService.findCustNoByScfId(anScfId, anAgencyNo);
    }

    @Override
    public String findScfIdByCustNo(Long anCustNo, String anAgencyNo) {

        return this.custRelationService.findScfIdByCustNo(anCustNo, anAgencyNo);
    }

    @Override
    public List<CustRelation> findFactorRelaByCoreCustNo(String anAgencyNo) {

        return custRelationService.findFactorRelaByCoreCustNo(anAgencyNo);
    }

    @Override
    public List<CustRelation> findFactorRelaByRough(String anAgencyNo) {

        return custRelationService.findFactorRelaByRough(anAgencyNo);
    }

    @Override
    public void saveOrUpdateCustFactor(CustRelation anRelation) {

        custRelationService.saveOrUpdateCustFactor(anRelation);
    }

    @Override
    public List<CustRelation> findAppAccountRequest() {

        return custRelationService.findAppAccountRequest();
    }

    @Override
    public CustRelation findByRelationId(Long anRelationId) {

        return custRelationService.findByRelationId(anRelationId);
    }

    @Override
    public String checkCoreCustomer(Long anCustNo, String anAgencyNo) {

        return custRelationService.checkCoreCustomer(anCustNo, anAgencyNo);
    }

    @Override
    public void saveFactorRelationInfo(Long anId, String anScfId, String anStatus) {

        custRelationService.saveFactorRelationInfo(anId, anScfId, anStatus);
    }

    @Override
    public String webQuerySimpleDataByFactorAndCore(Long anCoreCustNo){

        return AjaxObject.newOk("查询开通保理业务对应核心企业成功", custRelationService.querySimpleDataByFactorAndCore(anCoreCustNo)).toJson();
    }

}
