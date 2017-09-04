package com.betterjr.modules.customer.dubbo;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.data.PlatformBaseRuleType;
import com.betterjr.common.data.SimpleDataEntity;
import com.betterjr.common.utils.UserUtils;
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
    public String webFindFactorStatus(final Long anCustNo) {

        return AjaxObject.newOk("开通保理业务状态查询成功", custRelationService.findFactorStatus(anCustNo)).toJson();
    }

    @Override
    public String webSaveCustRelation(final Long anCustNo, final String anProviderCustList, final String anFactorCustList, final String anPostscript) {

        return AjaxObject.newOk("开通保理融资业务申请成功", custRelationService.saveCustRelation(anCustNo, anProviderCustList, anFactorCustList, anPostscript))
                .toJson();
    }

    @Override
    public String webQueryAuditWorkflow(final Long anCustNo) {

        return AjaxObject.newOk("开通保理融资业务审批流程查询成功", custRelationAuditService.queryAuditWorkflow(anCustNo)).toJson();
    }

    @Override
    public String webQueryRelationAccept(final String anBusinStatus, final String anFlag, final int anPageNum, final int anPageSize) {

        return AjaxObject.newOkWithPage("客户白名单受理列表查询成功", custRelationService.queryRelationAccept(anBusinStatus, anFlag, anPageNum, anPageSize))
                .toJson();
    }

    @Override
    public String webQueryRelationAudit(final String anBusinStatus, final String anFlag, final int anPageNum, final int anPageSize) {

        return AjaxObject.newOkWithPage("客户白名单审批列表查询成功", custRelationService.queryRelationAudit(anBusinStatus, anFlag, anPageNum, anPageSize))
                .toJson();
    }

    @Override
    public String webSaveRelationAccept(final Long anId, final String anAuditOpinion) {

        return AjaxObject.newOk("客户白名单受理成功", custRelationService.saveRelationAccept(anId, anAuditOpinion)).toJson();
    }

    @Override
    public String webSaveRelationAudit(final Long anId, final String anAuditOpinion) {

        return AjaxObject.newOk("客户白名单审批成功", custRelationService.saveRelationAudit(anId, anAuditOpinion)).toJson();
    }

    @Override
    public String webSaveRefuseAcceptRelation(final Long anId, final String anAuditOpinion) {

        return AjaxObject.newOk("客户白名单受理驳回成功", custRelationService.saveRefuseAcceptRelation(anId, anAuditOpinion)).toJson();
    }

    @Override
    public String webSaveRefuseAuditRelation(final Long anId, final String anAuditOpinion) {

        return AjaxObject.newOk("客户白名单审批驳回成功", custRelationService.saveRefuseAuditRelation(anId, anAuditOpinion)).toJson();
    }

    @Override
    public String webQueryCoreKeyAndValue(final Long anCustNo) {

        return AjaxObject.newOk("核心企业下拉列表查询成功", custRelationService.queryCoreKeyAndValue(anCustNo)).toJson();
    }
    
    public List<CustRelation> queryCoreList(final Long anCustNo){
    	return custRelationService.queryCoreList(anCustNo);
    }

    @Override
    public String webQuerySupplierByCore(final Long anCoreCustNo) {

        return AjaxObject.newOk("供应商下拉列表查询成功", custRelationService.querySupplierByCore(anCoreCustNo)).toJson();
    }

    @Override
    public String webQuerySellerByCore(final Long anCoreCustNo) {

        return AjaxObject.newOk("经销商下拉列表查询成功", custRelationService.querySellerByCore(anCoreCustNo)).toJson();
    }

    @Override
    public String webQueryFactorKeyAndValue(final Long anCustNo) {
        final PlatformBaseRuleType role = UserUtils.getPrincipal().getInnerRules().iterator().next();
        return AjaxObject.newOk("保理机构下拉列表查询成功", custRelationService.queryFactorKeyAndValue(anCustNo, role)).toJson();
    }

    @Override
    public String webQueryProviderRelation(final Long anCustNo) {

        return AjaxObject.newOk("客户与电子合同服务商关系查询成功", custRelationService.queryProviderRelation(anCustNo)).toJson();
    }

    @Override
    public String webQueryCustRelation(final Long anCustNo) {
        final PlatformBaseRuleType role = UserUtils.getPrincipal().getInnerRules().iterator().next();
        return AjaxObject.newOk("客户关系查询成功", custRelationService.queryCustRelation(anCustNo, role)).toJson();
    }

    @Override
    public String webQueryFacotrCoreRelation(final Long anFactorNo) {

        return AjaxObject.newOk("保理机构与核心企业关系查询成功", custRelationService.queryFactorCoreRelation(anFactorNo)).toJson();
    }

    @Override
    public String webQueryFactorCustRelation(final Long anFactorNo, final String anCreditType) {

        return AjaxObject.newOk("保理机构关系客户查询成功", custRelationService.queryFactorCustRelation(anFactorNo, anCreditType)).toJson();
    }

    @Override
    public String webQueryFactorAllCust(final Long anFactorNo) {

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
    @Override
    public List<CustRelationData> webQueryCustRelationData(final Long anCustNo, final String anCreditType) {
        return custRelationService.queryCustRelationData(anCustNo, anCreditType);
    }

    @Override
    public String webQueryFactorRelation() {
        return AjaxObject.newOk("客户与保理机构关系查询成功", custRelationService.queryFactorRelation()).toJson();
    }

    @Override
    public CustRelation findOneRelation_OLD(final Long anCustNo, final Long anRelateCustno, final String anPartnerCustNo) {
        // TODO Auto-generated method stub
        return custRelationService.findOneRelation(anCustNo, anRelateCustno, anPartnerCustNo);
    }

    @Override
    public boolean saveFactorRelationStatus(final Long anCustNo, final String anScfId, final String anStatus, final String anFactorNo) {
        // TODO Auto-generated method stub
        return this.custRelationService.saveFactorRelationStatus(anCustNo, anScfId, anStatus, anFactorNo);
    }

    @Override
    public Long findCustNoByBankInfo(final String anBankAccountName, final String anBankAccount) {
        // TODO Auto-generated method stub
        return this.custRelationService.findCustNoByBankInfo(anBankAccountName, anBankAccount);
    }

    @Override
    public boolean saveAndCheckCust(final Map<String, Object> anValues, final String anCoreCustName, final Long anCoreCustNo) {

        return custRelationService.saveAndCheckCust(anValues, anCoreCustName, anCoreCustNo);
    }

    @Override

    public String webQueryFactorRelation(final Long anCustNo) {
        return AjaxObject.newOk("客户与保理机构关系查询成功", custRelationService.queryFactorRelation(anCustNo)).toJson();
    }

    @Override
    public String webQueryOpenedFactor(final Long anCustNo) {
        return AjaxObject.newOk("客户与保理机构关系查询成功", custRelationService.queryOpenedFactor(anCustNo)).toJson();
    }

    @Override
    public String webFindWechatCurrentCustInfo() {
        // TODO Auto-generated method stub
        return AjaxObject.newOk("微信客户信息查询", custRelationService.findWechatCurrentCustInfo()).toJson();
    }

    @Override
    public String webSaveCustRelation(final Long anCustNo, final String anFactorCustList) {
        // TODO Auto-generated method stub
        return AjaxObject.newOk("微信客户申请开通保理融资业务成功", custRelationService.saveCustRelation(anCustNo, anFactorCustList)).toJson();
    }

    @Override
    public Long findCustNoByScfId(final String anScfId, final String anAgencyNo) {

        return custRelationService.findCustNoByScfId(anScfId, anAgencyNo);
    }

    @Override
    public String findScfIdByCustNo(final Long anCustNo, final String anAgencyNo) {

        return this.custRelationService.findScfIdByCustNo(anCustNo, anAgencyNo);
    }

    @Override
    public List<CustRelation> findFactorRelaByCoreCustNo(final String anAgencyNo) {

        return custRelationService.findFactorRelaByCoreCustNo(anAgencyNo);
    }

    @Override
    public List<CustRelation> findFactorRelaByRough(final String anAgencyNo) {

        return custRelationService.findFactorRelaByRough(anAgencyNo);
    }

    @Override
    public void saveOrUpdateCustFactor(final CustRelation anRelation) {

        custRelationService.saveOrUpdateCustFactor(anRelation);
    }

    @Override
    public List<CustRelation> findAppAccountRequest() {

        return custRelationService.findAppAccountRequest();
    }

    @Override
    public CustRelation findByRelationId(final Long anRelationId) {

        return custRelationService.findByRelationId(anRelationId);
    }

    @Override
    public String checkCoreCustomer(final Long anCustNo, final String anAgencyNo) {

        return custRelationService.checkCoreCustomer(anCustNo, anAgencyNo);
    }

    @Override
    public void saveFactorRelationInfo(final Long anId, final String anScfId, final String anStatus) {

        custRelationService.saveFactorRelationInfo(anId, anScfId, anStatus);
    }

    @Override
    public String webQuerySimpleDataByFactorAndCore(final Long anCoreCustNo){

        return AjaxObject.newOk("查询开通保理业务对应核心企业成功", custRelationService.querySimpleDataByFactorAndCore(anCoreCustNo)).toJson();

    }

    @Override
    public String webQueryCoreCust(final Long anCoreCustNo) {
        return AjaxObject.newOk("查询核心企业下所有客户成功", custRelationService.queryCoreCust(anCoreCustNo)).toJson();
    }

    @Override
    public CustRelation findRelationWithCustCorp(final Long anCustNo, final String anPartnerCustNo, final String anCustCorp) {

        return custRelationService.findRelationWithCustCorp(anCustNo, anPartnerCustNo, anCustCorp);
    }

    @Override
    public String webQueryCustInfoByFactor(final String anRelateType, final String anFlag, final int anPageNum, final int anPageSize) {
        return AjaxObject.newOkWithPage("保理公司查询企业信息成功", custRelationService.queryCustInfoByFactor(anRelateType, anFlag, anPageNum, anPageSize)).toJson();
    }

    @Override
    public String webQueryAllCust() {
        
        return AjaxObject.newOk("查询所有客户成功", custRelationService.queryAllCust()).toJson();
    }

    @Override
    public List<SimpleDataEntity> queryFactoryByCore(Long anCoreCustNo) {
        
        return custRelationService.queryFactoryByCore(anCoreCustNo);
    }

    @Override
    public String webQueryBankInfoKeyAndValue(Long anCustNo) {
        // TODO Auto-generated method stub
        
        return AjaxObject.newOk("企业合作银行下拉列表查询成功", custRelationService.queryBankInfoKeyAndValue(anCustNo)).toJson();
    }
}
