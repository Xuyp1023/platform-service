package com.betterjr.modules.customer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.customer.constant.CustomerConstants;
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

    /**
     * 添加变更申请
     * 
     * @param anCustNo
     * @param anChangeItem
     * @param anTmpIds
     * @return
     */
    public CustChangeApply addCustChangeApply(Long anCustNo, String anChangeItem, String anTmpIds) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        BTAssert.notNull(anChangeItem, "变更项目不允许为空！");
        BTAssert.notNull(anTmpIds, "变更流水项不允许为空！");

        if (checkExistChangeApply(anCustNo, anChangeItem) == true) {
            throw new BytterTradeException(40001, "不允许重复提交变更申请！");
        }

        final CustChangeApply custChangeApply = new CustChangeApply();
        custChangeApply.initAddValue(anCustNo, anChangeItem, anTmpIds);
        this.insert(custChangeApply);
        return custChangeApply;
    }

    /**
     * 检查是否有未处理的变更申请
     * 
     * @param anCustNo
     * @param anChangeItem
     * @param anTmpIds
     * @return
     */
    public Boolean checkExistChangeApply(Long anCustNo, String anChangeItem) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        BTAssert.notNull(anChangeItem, "变更项目不允许为空！");

        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("custNo", anCustNo);
        conditionMap.put("changeItem", anChangeItem);
        conditionMap.put("businStatus", CustomerConstants.NORMAL_STATUS);

        List<CustChangeApply> custChangeApplys = this.selectByProperty(conditionMap);

        return !Collections3.isEmpty(custChangeApplys);
    }

    /**
     * 保存变更申请-修改状态
     * 
     * @param anCustChangeApply
     * @return
     */
    public CustChangeApply saveCustChangeApplyStatus(Long anId, String anBusinStatus) {
        BTAssert.notNull(anId, "编号不允许为空！");
        BTAssert.notNull(anBusinStatus, "状态不允许为空！");

        final CustChangeApply tempCustChangeApply = this.selectByPrimaryKey(anId);
        tempCustChangeApply.initModifyValue(anBusinStatus);
        this.updateByPrimaryKeySelective(tempCustChangeApply);

        return tempCustChangeApply;
    }

    /**
     * 查询变更申请列表
     * 
     * @return
     */
    public Page<CustChangeApply> queryCustChangeApply(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {

        final Page<CustChangeApply> changeApplys = this.selectPropertyByPage(CustChangeApply.class, anParam, anPageNum, anPageSize, anFlag == 1);

        for (CustChangeApply changeApply : changeApplys) {
            CustAuditLog auditLog = auditLogService.findCustAuditLogByCustChangeApply(changeApply);
            if (auditLog != null) {
                changeApply.setAuditDate(auditLog.getAuditDate());
                changeApply.setAuditTime(auditLog.getAuditTime());
                changeApply.setAuditResult(auditLog.getResult());
            }
        }

        return changeApplys;
    }

}