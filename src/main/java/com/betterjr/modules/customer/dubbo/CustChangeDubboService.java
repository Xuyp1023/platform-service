package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.customer.ICustChangeService;
import com.betterjr.modules.customer.service.CustChangeService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

@Service(interfaceClass = ICustChangeService.class)
public class CustChangeDubboService implements ICustChangeService {
    @Resource
    private CustChangeService changeService;

    @Override
    public String webQueryChangeApplyList(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        Map<String, Object> param = RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject
                .newOkWithPage("变更申请列表查询成功！", changeService.queryChangeApplyList(param, anFlag, anPageNum, anPageSize))
                .toJson();
    }

    @Override
    public String webAuditPassChangeApply(Long anId, String anReason) {
        return AjaxObject.newOk("审核通过成功！", changeService.saveAuditPassChangeApply(anId, anReason)).toJson();
    }

    @Override
    public String webAuditRejectChangeApply(Long anId, String anReason) {
        return AjaxObject.newOk("审核驳回成功！", changeService.saveAuditRejectChangeApply(anId, anReason)).toJson();
    }

    @Override
    public String webCancelChangeApply(Long anId, String anReason) {
        return AjaxObject.newOk("审核通过成功！", changeService.saveCancelChangeApply(anId, anReason)).toJson();
    }

}
