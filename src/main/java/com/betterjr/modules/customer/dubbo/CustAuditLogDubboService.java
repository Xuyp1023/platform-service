package com.betterjr.modules.customer.dubbo;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.customer.ICustAuditLogService;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.service.CustAuditLogService;

@Service(interfaceClass = ICustAuditLogService.class)
public class CustAuditLogDubboService implements ICustAuditLogService {
    @Resource
    private CustAuditLogService auditLogService;

    @Override
    public String webQueryAuditLogOpenAccountList(Long anBusinId) {
        return AjaxObject.newOk("开户 审核日志-列表查询 成功", auditLogService.queryCustAuditLogByAuditType(anBusinId, CustomerConstants.AUDIT_TYPE_OPENACCOUNT))
                .toJson();
    }

    @Override
    public String webQueryAuditLogInsteadApplyList(Long anBusinId) {
        return AjaxObject
                .newOk("代录申请 审核日志-列表查询 成功", auditLogService.queryCustAuditLogByAuditType(anBusinId, CustomerConstants.AUDIT_TYPE_INSTEADAPPLY))
                .toJson();
    }

    @Override
    public String webQueryAuditLogInsteadRecordList(Long anBusinId) {
        return AjaxObject
                .newOk("代录记录 审核日志-列表查询 成功", auditLogService.queryCustAuditLogByAuditType(anBusinId, CustomerConstants.AUDIT_TYPE_INSTEADRECORD))
                .toJson();
    }

    @Override
    public String webQueryAuditLogChangeApplyList(Long anBusinId) {
        return AjaxObject
                .newOk("变更申请 审核日志-列表查询 成功", auditLogService.queryCustAuditLogByAuditType(anBusinId, CustomerConstants.AUDIT_TYPE_CHANGEAPPLY))
                .toJson();
    }

    @Override
    public String webFindAuditLog(Long anId) {
        return AjaxObject.newOk("审核日志-详情查询 成功", auditLogService.findCustAuditLog(anId)).toJson();
    }

}
