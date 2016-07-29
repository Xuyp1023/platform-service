package com.betterjr.modules.customer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.entity.CustAuditLog;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustMechBankAccountTmp;
import com.betterjr.modules.customer.entity.CustMechBaseTmp;
import com.betterjr.modules.customer.entity.CustMechBusinLicenceTmp;
import com.betterjr.modules.customer.entity.CustMechContacterTmp;
import com.betterjr.modules.customer.entity.CustMechLawTmp;
import com.betterjr.modules.customer.entity.CustMechManagerTmp;
import com.betterjr.modules.customer.entity.CustMechShareholderTmp;
import com.betterjr.modules.customer.helper.FormalDataServiceHelper;
import com.betterjr.modules.customer.helper.IFormalDataService;

/**
 * 变更服务
 * 
 * @author liuwl
 *
 */
@Service
public class CustChangeService {

    @Resource
    private CustChangeApplyService changeApplyService;

    @Resource
    private CustAuditLogService auditLogService;

    /**
     * 查询变更申请详情
     * 
     * @param anCustNo
     * @param anId
     * @return
     */
    public CustChangeApply findChangeApply(Long anId, String anChangeItem) {
        BTAssert.notNull(anId, "申请编号不允许为空！");

        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put(CustomerConstants.ID, anId);
        conditionMap.put("changeItem", anChangeItem);
        List<CustChangeApply> changeApplys = changeApplyService.selectByProperty(conditionMap);

        if (Collections3.isEmpty(changeApplys) == true) {
            throw new BytterTradeException(20008, "变更申请详情没有找到！");
        }
        return Collections3.getFirst(changeApplys);
    }

    /**
     * 查询变更申请列表 分类查询
     * 
     * @param anCustNo
     * @return
     */
    public Page<CustChangeApply> queryChangeApply(Long anCustNo, String anChangeItem, int anFlag, int anPageNum, int anPageSize) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空!");
        BTAssert.notNull(anChangeItem, "变更项不允许为空!");

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put(CustomerConstants.CUST_NO, anCustNo);
        conditionMap.put("changeItem", anChangeItem);

        return changeApplyService.selectPropertyByPage(CustChangeApply.class, conditionMap, anPageNum, anPageSize, anFlag == 1);
    }

    /**
     * 查询变更申请列表 审核使用
     * 
     * @param anCustNo
     * @return
     */
    public Page<CustChangeApply> queryChangeApplyList(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        BTAssert.notNull(anParam, "查询条件不允许为空!");

        return changeApplyService.queryCustChangeApply(anParam, anFlag, anPageNum, anPageSize);
    }

    /**
     * 保存审核通过 将数据转入正式表
     * 
     * @param anId
     * @return
     */
    public CustChangeApply saveAuditPassChangeApply(Long anId, String anReason) {
        BTAssert.notNull(anId, "变更审核编号不允许为空!");

        final CustChangeApply tempChangeApply = changeApplyService.selectByPrimaryKey(anId);
        BTAssert.notNull(tempChangeApply, "没有找到相应的变更申请！");

        if (BetterStringUtils.equals(tempChangeApply.getBusinStatus(), CustomerConstants.CHANGE_APPLY_STATUS_NEW) == false) {
            throw new BytterTradeException(20010, "变更申请状态不正确，审核失败！");
        }

        // 修改审核状态为通过
        final CustChangeApply changeApply = changeApplyService.saveChangeApplyStatus(anId, CustomerConstants.CHANGE_APPLY_STATUS_AUDIT_PASS);
        BTAssert.notNull(changeApply, "修改变更申请审核状态失败！");

        // 保存相应数据至正式表
        IFormalDataService formalDataService = FormalDataServiceHelper.getFormalDataService(changeApply);
        BTAssert.notNull(formalDataService, "变更项目不正确！");

        String[] tmpIds = BetterStringUtils.split(changeApply.getTmpIds(), ",");
        formalDataService.saveFormalData(tmpIds);

        // 添加审核
        CustAuditLog auditLog = auditLogService.addCustAuditLog(CustomerConstants.AUDIT_TYPE_CHANGEAPPLY, anId, CustomerConstants.AUDIT_RESULT_PASS,
                anReason, changeApply.getChangeItem(), changeApply.getCustNo());
        BTAssert.notNull(auditLog, "审核记录添加失败!");

        return changeApply;
    }

    /**
     * 保存审核驳回 修改状态
     * 
     * @param anId
     * @return
     */
    public CustChangeApply saveAuditRejectChangeApply(Long anId, String anReason) {
        BTAssert.notNull(anId, "变更审核编号不允许为空!");

        final CustChangeApply tempChangeApply = changeApplyService.selectByPrimaryKey(anId);
        BTAssert.notNull(tempChangeApply, "没有找到相应的变更申请！");

        if (BetterStringUtils.equals(tempChangeApply.getBusinStatus(), CustomerConstants.CHANGE_APPLY_STATUS_NEW) == false) {
            throw new BytterTradeException(20010, "变更申请状态不正确，审核失败！");
        }

        // 修改审核状态为 驳回
        final CustChangeApply changeApply = changeApplyService.saveChangeApplyStatus(anId, CustomerConstants.CHANGE_APPLY_STATUS_AUDIT_REJECT);

        // 添加审核记录
        CustAuditLog auditLog = auditLogService.addCustAuditLog(CustomerConstants.AUDIT_TYPE_CHANGEAPPLY, anId, CustomerConstants.AUDIT_RESULT_REJECT,
                anReason, changeApply.getChangeItem(), changeApply.getCustNo());
        BTAssert.notNull(auditLog, "审核记录添加失败!");

        return changeApply;
    }

    /**
     * 作废变更申请
     * 
     * @param anCustNo
     * @param anId
     * @return
     */
    public CustChangeApply saveCancelChangeApply(Long anCustNo, Long anId) {
        return null;
    }

}
