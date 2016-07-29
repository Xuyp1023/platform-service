package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.customer.ICustInsteadService;
import com.betterjr.modules.customer.service.CustInsteadService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

@Service(interfaceClass = ICustInsteadService.class)
public class CustInsteadDubboService implements ICustInsteadService {
    @Resource
    private CustInsteadService insteadService;

    @Override
    public String webAddInsteadApply(Map<String, Object> anParam, String anFileList) {
        Map<String, Object> param = (Map<String, Object>) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("申请代录成功", insteadService.addInsteadApply(param, anFileList)).toJson();
    }

    @Override
    public String webSaveInsteadApply(Map<String, Object> anParam, Long anApplyId, String anFileList) {
        Map<String, Object> param = (Map<String, Object>) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("代录申请 修改申请 成功", insteadService.saveInsteadApply(param, anApplyId, anFileList)).toJson();
    }
    
    @Override
    public String webQueryInsteadApplyList(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        Map<String, Object> param = (Map<String, Object>) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOkWithPage("代录申请 代录列表 查询成功", insteadService.queryInsteadApplyList(param, anFlag, anPageNum, anPageSize)).toJson();
    }
    
    @Override
    public String webQueryInsteadApplyAuditList(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        Map<String, Object> param = (Map<String, Object>) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOkWithPage("代录申请 审核列表 查询成功", insteadService.queryInsteadApplyAuditList(param, anFlag, anPageNum, anPageSize)).toJson();
    }
    
    @Override
    public String webQueryInsteadApplyReviewList(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        Map<String, Object> param = (Map<String, Object>) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOkWithPage("代录申请 复核列表 查询成功", insteadService.queryInsteadApplyReviewList(param, anFlag, anPageNum, anPageSize)).toJson();
    }

    @Override
    public String webQueryInsteadApplyConfirmList(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        Map<String, Object> param = (Map<String, Object>) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOkWithPage("代录申请 确认列表 查询成功", insteadService.queryInsteadApplyConfirmList(param, anFlag, anPageNum, anPageSize)).toJson();
    }
    
    @Override
    public String webQueryInsteadRecordByApply(Long anApplyId) {
        return AjaxObject.newOk("代录申请 代录记录列表 查询成功", insteadService.queryInsteadRecordByApply(anApplyId)).toJson();
    }
    
    @Override
    public String webAuditPassInsteadApply(Long anId, String anReason) {
        return AjaxObject.newOk("代录申请 审核通过", insteadService.saveAuditPassInsteadApply(anId, anReason)).toJson();
    }

    @Override
    public String webAuditRejectInsteadApply(Long anId, String anReason) {
        return AjaxObject.newOk("代录申请 审核驳回", insteadService.saveAuditRejectInsteadApply(anId, anReason)).toJson();
    }
    
    @Override
    public String webSubmitTypeInInsteadApply(Long anId) {
        return AjaxObject.newOk("代录申请 提交复核", insteadService.saveSubmitTypeInInsteadApply(anId)).toJson();
    }
    
    @Override
    public String webReviewPassInsteadApply(Long anId, String anReason) {
        return AjaxObject.newOk("代录申请 复核通过", insteadService.saveReviewPassInsteadApply(anId, anReason)).toJson();
    }

    @Override
    public String webReviewRejectInsteadApply(Long anId, String anReason) {
        return AjaxObject.newOk("代录申请 复核驳回", insteadService.saveReviewRejectInsteadApply(anId, anReason)).toJson();
    }

    @Override
    public String webConfirmPassInsteadApply(Long anId, String anReason) {
        return AjaxObject.newOk("代录申请 确认通过", insteadService.saveConfirmPassInsteadApply(anId, anReason)).toJson();
    }

    @Override
    public String webConfirmRejectInsteadApply(Long anId, String anReason) {
        return AjaxObject.newOk("代录申请 确认驳回", insteadService.saveConfirmRejectInsteadApply(anId, anReason)).toJson();
    }

    @Override
    public String webCancelInsteadApply(Long anId, String anReason) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webReviewPassInsteadRecord(Long anId, String anReason) {
        return AjaxObject.newOk("代录记录 复核通过", insteadService.saveReviewPassInsteadRecord(anId, anReason)).toJson();
    }

    @Override
    public String webReviewRejectInsteadRecord(Long anId, String anReason) {
        return AjaxObject.newOk("代录记录 复核驳回", insteadService.saveReviewRejectInsteadRecord(anId, anReason)).toJson();
    }

    @Override
    public String webConfirmPassInsteadRecord(Long anId, String anReason) {
        return AjaxObject.newOk("代录记录 确认通过", insteadService.saveConfirmPassInsteadRecord(anId, anReason)).toJson();
    }

    @Override
    public String webConfirmRejectInsteadRecord(Long anId, String anReason) {
        return AjaxObject.newOk("代录记录 确认驳回", insteadService.saveConfirmRejectInsteadRecord(anId, anReason)).toJson();
    }

    @Override
    public String webCancelInsteadRecord(Long anId, String anReason) {
        // TODO Auto-generated method stub
        return null;
    }


}
