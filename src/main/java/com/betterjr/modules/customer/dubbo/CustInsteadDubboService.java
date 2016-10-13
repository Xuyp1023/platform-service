package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.customer.ICustInsteadService;
import com.betterjr.modules.customer.service.CustInsteadApplyService;
import com.betterjr.modules.customer.service.CustInsteadService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

@Service(interfaceClass = ICustInsteadService.class)
public class CustInsteadDubboService implements ICustInsteadService {
    @Resource
    private CustInsteadService insteadService;

    @Resource
    private CustInsteadApplyService insteadApplyService;

    @Override
    public String webAddInsteadApply(final Map<String, Object> anParam, final String anFileList) {
        final Map<String, Object> param = RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("申请代录成功", insteadService.addInsteadApply(param, anFileList)).toJson();
    }

    @Override
    public String webSaveInsteadApply(final Map<String, Object> anParam, final Long anApplyId, final String anFileList) {
        final Map<String, Object> param = RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("代录申请 修改申请 成功", insteadService.saveInsteadApply(param, anApplyId, anFileList)).toJson();
    }

    @Override
    public String webFindInsteadApply(final Long anApplyId) {
        return AjaxObject.newOk("代录申请 查询详情 成功", insteadApplyService.findCustInsteadApply(anApplyId)).toJson();
    }

    @Override
    public String webQueryInsteadApplyList(final Map<String, Object> anParam, final int anFlag, final int anPageNum, final int anPageSize) {
        final Map<String, Object> param = RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOkWithPage("代录申请 代录列表 查询成功", insteadService.queryInsteadApplyList(param, anFlag, anPageNum, anPageSize)).toJson();
    }

    @Override
    public String webQueryInsteadApplyOwnList(final Map<String, Object> anParam, final int anFlag, final int anPageNum, final int anPageSize) {
        final Map<String, Object> param = RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOkWithPage("代录申请 拥有列表 查询成功", insteadService.queryInsteadApplyOwnList(param, anFlag, anPageNum, anPageSize)).toJson();
    }

    @Override
    public String webQueryInsteadApplyAuditList(final Map<String, Object> anParam, final int anFlag, final int anPageNum, final int anPageSize) {
        final Map<String, Object> param = RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOkWithPage("代录申请 审核列表 查询成功", insteadService.queryInsteadApplyAuditList(param, anFlag, anPageNum, anPageSize)).toJson();
    }

    @Override
    public String webQueryInsteadApplyReviewList(final Map<String, Object> anParam, final int anFlag, final int anPageNum, final int anPageSize) {
        final Map<String, Object> param = RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOkWithPage("代录申请 复核列表 查询成功", insteadService.queryInsteadApplyReviewList(param, anFlag, anPageNum, anPageSize)).toJson();
    }

    @Override
    public String webQueryInsteadApplyConfirmList(final Map<String, Object> anParam, final int anFlag, final int anPageNum, final int anPageSize) {
        final Map<String, Object> param = RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOkWithPage("代录申请 确认列表 查询成功", insteadService.queryInsteadApplyConfirmList(param, anFlag, anPageNum, anPageSize)).toJson();
    }

    @Override
    public String webQueryInsteadRecordByApply(final Long anApplyId) {
        return AjaxObject.newOk("代录申请 代录记录列表 查询成功", insteadService.queryInsteadRecordByApply(anApplyId)).toJson();
    }

    @Override
    public String webAuditPassInsteadApply(final Long anId, final String anReason) {
        return AjaxObject.newOk("代录申请 审核通过", insteadService.saveAuditPassInsteadApply(anId, anReason)).toJson();
    }

    @Override
    public String webAuditRejectInsteadApply(final Long anId, final String anReason) {
        return AjaxObject.newOk("代录申请 审核驳回", insteadService.saveAuditRejectInsteadApply(anId, anReason)).toJson();
    }

    @Override
    public String webSubmitReviewInsteadApply(final Long anId) {
        return AjaxObject.newOk("代录申请 复核提交", insteadService.saveSubmitReviewInsteadApply(anId)).toJson();
    }

    @Override
    public String webSubmitConfirmInsteadApply(final Long anId) {
        return AjaxObject.newOk("代录申请 确认提交", insteadService.saveSubmitConfirmInsteadApply(anId)).toJson();
    }

    @Override
    public String webSubmitTypeInInsteadApply(final Long anId) {
        return AjaxObject.newOk("代录申请 录入提交", insteadService.saveSubmitTypeInInsteadApply(anId)).toJson();
    }

    @Override
    public String webReviewPassInsteadApply(final Long anId, final String anReason) {
        return AjaxObject.newOk("代录申请 复核通过", insteadService.saveReviewPassInsteadApply(anId, anReason)).toJson();
    }

    @Override
    public String webReviewRejectInsteadApply(final Long anId, final String anReason) {
        return AjaxObject.newOk("代录申请 复核驳回", insteadService.saveReviewRejectInsteadApply(anId, anReason)).toJson();
    }

    @Override
    public String webConfirmPassInsteadApply(final Long anId, final String anReason) {
        return AjaxObject.newOk("代录申请 确认通过", insteadService.saveConfirmPassInsteadApply(anId, anReason)).toJson();
    }

    @Override
    public String webConfirmRejectInsteadApply(final Long anId, final String anReason) {
        return AjaxObject.newOk("代录申请 确认驳回", insteadService.saveConfirmRejectInsteadApply(anId, anReason)).toJson();
    }

    @Override
    public String webCancelInsteadApply(final Long anId, final String anReason) {
        return AjaxObject.newOk("代录申请 作废成功", insteadService.saveCancelInsteadApply(anId, anReason)).toJson();
    }

    @Override
    public String webReviewPassInsteadRecord(final Long anId, final String anReason) {
        return AjaxObject.newOk("代录记录 复核通过", insteadService.saveReviewPassInsteadRecord(anId, anReason)).toJson();
    }

    @Override
    public String webReviewRejectInsteadRecord(final Long anId, final String anReason) {
        return AjaxObject.newOk("代录记录 复核驳回", insteadService.saveReviewRejectInsteadRecord(anId, anReason)).toJson();
    }

    @Override
    public String webConfirmPassInsteadRecord(final Long anId, final String anReason) {
        return AjaxObject.newOk("代录记录 确认通过", insteadService.saveConfirmPassInsteadRecord(anId, anReason)).toJson();
    }

    @Override
    public String webConfirmRejectInsteadRecord(final Long anId, final String anReason) {
        return AjaxObject.newOk("代录记录 确认驳回", insteadService.saveConfirmRejectInsteadRecord(anId, anReason)).toJson();
    }

    @Override
    public String webCancelInsteadRecord(final Long anId, final String anReason) {
        // TODO @@@@@@
        return null;
    }


}
