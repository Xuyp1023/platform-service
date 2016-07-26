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
    public String webAddInsteadApply(Map<String, Object> anParam) {
        Map<String, Object> param = (Map<String, Object>) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("申请代录成功", insteadService.addInsteadApply(param)).toJson();
    }

    @Override
    public String webQueryInsteadApplyList(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        Map<String, Object> param = (Map<String, Object>) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOkWithPage("查询代录列表成功", insteadService.queryInsteadApplyList(param, anFlag, anPageNum, anPageSize)).toJson();
    }
    
    @Override
    public String webQueryInsteadApplyAuditList(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        Map<String, Object> param = (Map<String, Object>) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOkWithPage("查询代录列表成功", insteadService.queryInsteadApplyAuditList(param, anFlag, anPageNum, anPageSize)).toJson();
    }
    
    @Override
    public String webQueryInsteadApplyReviewList(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        Map<String, Object> param = (Map<String, Object>) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOkWithPage("查询代录列表成功", insteadService.queryInsteadApplyReviewList(param, anFlag, anPageNum, anPageSize)).toJson();
    }

    @Override
    public String webQueryInsteadApplyConfirmList(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        Map<String, Object> param = (Map<String, Object>) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOkWithPage("查询代录列表成功", insteadService.queryInsteadApplyConfirmList(param, anFlag, anPageNum, anPageSize)).toJson();
    }
    
    @Override
    public String webAuditPassInsteadApply(Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webAuditRejectInsteadApply(Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webReviewPassInsteadApply(Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webReviewRejectInsteadReject(Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webConfirmPassInsteadApply(Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webConfirmRejectInsteadApply(Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webCancelInsteadApply(Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webReviewPassInsteadRecord(Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webReviewRejectInsteadRecord(Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webConfirmPassInsteadRecord(Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webConfirmRejectInsteadRecord(Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webCancelInsteadRecord(Long anId) {
        // TODO Auto-generated method stub
        return null;
    }


}
