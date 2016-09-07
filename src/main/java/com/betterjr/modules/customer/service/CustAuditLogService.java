package com.betterjr.modules.customer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.customer.constants.CustomerConstants;
import com.betterjr.modules.customer.dao.CustAuditLogMapper;
import com.betterjr.modules.customer.entity.CustAuditLog;
import com.betterjr.modules.customer.entity.CustChangeApply;

/**
 * 审核记录服务
 * 
 * @author liuwl
 *
 */
@Service
public class CustAuditLogService extends BaseService<CustAuditLogMapper, CustAuditLog> {

    /**
     * 添加一条审核记录
     */
    public CustAuditLog addCustAuditLog(String anAuditType, String anStepNode, Long anBusinId, String anAuditResult, String anReason, String anAuditItem,
            Long anCustNo) {
        if (BetterStringUtils.isBlank(anAuditType) == true) {
            throw new BytterTradeException(20030, "审核类型不允许为空！");
        }
        if (BetterStringUtils.isBlank(anAuditResult) == true) {
            throw new BytterTradeException(20030, "审核结果不允许为空！");
        }

        BTAssert.notNull(anBusinId, "业务编号不允许为空！");

        CustAuditLog custAuditLog = new CustAuditLog();
        custAuditLog.initAddValue(anAuditType, anStepNode, anBusinId, anAuditResult, anReason, anAuditItem, anCustNo);
        this.insert(custAuditLog);
        return custAuditLog;
    }

    /**
     * 根据条件找回最新的审核记录
     */
    public CustAuditLog findCustAuditLogByCustChangeApply(CustChangeApply anChangeApply) {
        BTAssert.notNull(anChangeApply, "变更申请不允许为空！");

        Map<String, Object> conditionMap = new HashMap<>();

        conditionMap.put("auditType", CustomerConstants.AUDIT_TYPE_CHANGEAPPLY);
        conditionMap.put("businId", anChangeApply.getId());

        List<CustAuditLog> auditLogs = this.selectByProperty(conditionMap);
        return Collections3.getFirst(auditLogs);
    }
    
    /**
     * 
     */
    public List<CustAuditLog> queryCustAuditLogByAuditType(Long anBusinId, String anAuditType) {
        BTAssert.notNull(anBusinId, "业务编号不允许为空!");

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("auditType", anAuditType);
        conditionMap.put("businId", anBusinId);

        return this.selectByProperty(conditionMap);
    }

    /**
     * 
     */
    public CustAuditLog findCustAuditLog(Long anId) {
        BTAssert.notNull(anId, "审核日志编号不允许为空!");
        
        final CustAuditLog custAuditLog = this.selectByPrimaryKey(anId);
        BTAssert.notNull(custAuditLog, "没有找到对应的审核日志!");
        
        return custAuditLog;
    }
}