package com.betterjr.modules.customer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.customer.constants.CustomerConstants;
import com.betterjr.modules.customer.dao.CustChangeApplyMapper;
import com.betterjr.modules.customer.entity.CustAuditLog;
import com.betterjr.modules.customer.entity.CustChangeApply;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustChangeApplyService extends BaseService<CustChangeApplyMapper, CustChangeApply> {
    @Resource
    private CustAuditLogService auditLogService;

    @Resource
    private CustAccountService custAccountService;

    /**
     * 添加变更申请
     */
    public CustChangeApply addChangeApply(final Long anCustNo, final String anChangeItem, final String anTmpIds) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        BTAssert.notNull(anChangeItem, "变更项目不允许为空！");
        BTAssert.notNull(anTmpIds, "变更流水项不允许为空！");

        if (checkExistChangeApply(anCustNo, anChangeItem, CustomerConstants.CHANGE_APPLY_STATUS_NEW) == true) {
            throw new BytterTradeException(40001, "不允许重复提交变更申请！");
        }

        // TODO @@@@@@@@ 检查是否有正在进行的 代录

        final CustChangeApply custChangeApply = new CustChangeApply();
        final String custName = custAccountService.queryCustName(anCustNo);
        custChangeApply.initAddValue(anCustNo, custName, anChangeItem, anTmpIds);
        this.insert(custChangeApply);
        return custChangeApply;
    }

    /**
     * 变更申请 查询
     */
    public CustChangeApply findChangeApply(final Long anId) {
        BTAssert.notNull(anId, "变更申请 编号不允许为空！");
        return this.selectByPrimaryKey(anId);
    }

    /**
     * 检查是否有未处理的变更申请
     */
    public Boolean checkExistChangeApply(final Long anCustNo, final String anChangeItem, final String anBusinStatus) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        BTAssert.notNull(anChangeItem, "变更项目不允许为空！");

        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("custNo", anCustNo);
        conditionMap.put("changeItem", anChangeItem);
        conditionMap.put("businStatus", anBusinStatus);

        final List<CustChangeApply> custChangeApplys = this.selectByProperty(conditionMap);

        return !Collections3.isEmpty(custChangeApplys);
    }

    /**
     * 
     * @param anCustNo
     * @return
     */
    public Boolean checkExistActiveChangeApply(final Long anCustNo, final String anChangeItem) {
        final Map<String, Object> conditionMap = new HashMap<>();

        conditionMap.put(CustomerConstants.CUST_NO, anCustNo);
        conditionMap.put("changeItem", anChangeItem);
        final String[] businStatues = { CustomerConstants.CHANGE_APPLY_STATUS_NEW };
        conditionMap.put("businStatus", businStatues);
        return Collections3.isEmpty(this.selectByProperty(conditionMap)) == false;
    }

    /**
     * 保存变更申请-修改状态
     */
    public CustChangeApply saveChangeApply(final Long anApplyId, final String anTmpIds) {
        BTAssert.notNull(anApplyId, "编号不允许为空！");
        BTAssert.notNull(anTmpIds, "流水记录不允许为空！");

        final CustChangeApply tempCustChangeApply = this.selectByPrimaryKey(anApplyId);
        tempCustChangeApply.setTmpIds(anTmpIds);
        tempCustChangeApply.setBusinStatus(CustomerConstants.CHANGE_APPLY_STATUS_NEW);

        this.updateByPrimaryKeySelective(tempCustChangeApply);

        return tempCustChangeApply;
    }

    /**
     * 保存变更申请-修改状态
     */
    public CustChangeApply saveChangeApplyStatus(final Long anId, final String anBusinStatus) {
        BTAssert.notNull(anId, "编号不允许为空！");
        BTAssert.notNull(anBusinStatus, "状态不允许为空！");

        final CustChangeApply tempCustChangeApply = this.selectByPrimaryKey(anId);
        tempCustChangeApply.initModifyValue(anBusinStatus);
        final int i = this.updateByPrimaryKeySelective(tempCustChangeApply);
        logger.debug("result:" + i);

        return tempCustChangeApply;
    }

    /**
     * 查询变更申请列表
     */
    public Page<CustChangeApply> queryCustChangeApply(final Map<String, Object> anParam, final int anFlag,
            final int anPageNum, final int anPageSize) {
        final Object custName = anParam.get("LIKEcustName");
        final Object businStatus = anParam.get("businStatus");
        if (custName == null || StringUtils.isBlank((String) custName)) {
            anParam.remove("LIKEcustName");
        } else {
            anParam.put("LIKEcustName", "%" + custName + "%");
        }
        if (businStatus == null || businStatus instanceof String && StringUtils.isBlank((String) businStatus)) {
            anParam.remove("businStatus");
        }

        final Page<CustChangeApply> changeApplys = this.selectPropertyByPage(anParam, anPageNum, anPageSize,
                anFlag == 1);

        changeApplys.forEach(changeApply -> {
            final CustAuditLog auditLog = auditLogService.findCustAuditLogByCustChangeApply(changeApply);
            if (auditLog != null) {
                changeApply.setAuditDate(auditLog.getAuditDate());
                changeApply.setAuditTime(auditLog.getAuditTime());
                changeApply.setAuditResult(auditLog.getResult());
                changeApply.setAuditReason(auditLog.getReason());
            }
        });

        return changeApplys;
    }

}