// ============================================================================
// Copyright (c) 1998-2016 BYTTER Limited. All rights reserved.
// ============================================================================
// CURRENT VERSION V2.0
// ============================================================================
// CHANGE LOG
// V2.0 : 2016-07-21, liuwl, TASK-002
// V2.0 : 2016-07-20, liuwl, TASK-001
// ============================================================================
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
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.entity.CustAuditLog;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustInsteadApply;
import com.betterjr.modules.customer.entity.CustInsteadRecord;
import com.betterjr.modules.customer.entity.CustMechBaseTmp;
import com.betterjr.modules.customer.helper.FormalDataServiceHelper;
import com.betterjr.modules.customer.helper.IFormalDataService;

/**
 * 代录服务
 * 
 * @author liuwl
 *
 */
@Service
public class CustInsteadService {
    @Resource
    private CustInsteadApplyService insteadApplyService;

    @Resource
    private CustInsteadRecordService insteadRecordService;

    @Resource
    private CustAuditLogService auditLogService;


    /**
     * 发起代录申请
     * 
     * @param anCustInsteadApply
     * @return
     */
    public CustInsteadApply addInsteadApply(Map<String, Object> anParam) {
        BTAssert.notNull(anParam, "代录信息不允许为空！");

        String insteadType = (String) anParam.get("insteadType");
        if ((insteadType.equals(CustomerConstants.INSTEAD_APPLY_TYPE_OPENACCOUNT)
                || insteadType.equals(CustomerConstants.INSTEAD_APPLY_TYPE_CHANGE)) == false) {
            throw new BytterTradeException(20040, "代录类型不正确");
        }

        String tempCustNo = (String) anParam.get("custNo");
        Long custNo = Long.valueOf(tempCustNo);
        final CustInsteadApply custInsteadApply = insteadApplyService.addCustInsteadApply(insteadType, custNo);

        Long applyId = custInsteadApply.getId();
        String insteadItems = (String) anParam.get("insteadItems");
        insteadRecordService.addCustInsteadRecord(applyId, insteadType, insteadItems);

        return custInsteadApply;
    }

    /**
     * 代录申请列表  所有
     * @param anParam
     * @param anFlag
     * @param anPageNum
     * @param anPageSize
     * @return
     */
    public Page<CustInsteadApply> queryInsteadApplyList(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        BTAssert.notNull(anParam, "查询参数不允许为空！");
        
        return insteadApplyService.queryCustInsteadApply(anParam, anFlag, anPageNum, anPageSize);
    }
    
    /**
     * 代录申请列表  待审核
     * @param anParam
     * @param anFlag
     * @param anPageNum
     * @param anPageSize
     * @return
     */
    public Page<CustInsteadApply> queryInsteadApplyAuditList(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        BTAssert.notNull(anParam, "查询参数不允许为空！");

        anParam.put("businStatus", CustomerConstants.INSTEAD_APPLY_STATUS_NEW);
        return insteadApplyService.queryCustInsteadApply(anParam, anFlag, anPageNum, anPageSize);
    }
    
    /**
     * 代录申请列表  待复核
     * @param anParam
     * @param anFlag
     * @param anPageNum
     * @param anPageSize
     * @return
     */
    public Page<CustInsteadApply> queryInsteadApplyReviewList(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        BTAssert.notNull(anParam, "查询参数不允许为空！");

        anParam.put("businStatus", CustomerConstants.INSTEAD_APPLY_STATUS_TYPE_IN);
        return insteadApplyService.queryCustInsteadApply(anParam, anFlag, anPageNum, anPageSize);
    }
    
    /**
     * 代录申请列表  待确认
     * @param anParam
     * @param anFlag
     * @param anPageNum
     * @param anPageSize
     * @return
     */
    public Page<CustInsteadApply> queryInsteadApplyConfirmList(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        BTAssert.notNull(anParam, "查询参数不允许为空！");
        
        anParam.put("operOrg", UserUtils.getOperatorInfo().getOperOrg());
        anParam.put("businStatus", CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_PASS);
        
        return insteadApplyService.queryCustInsteadApply(anParam, anFlag, anPageNum, anPageSize);
    }
    
    /**
     * 
     * @param anId
     * @param anReason
     * @return
     */
    public CustInsteadApply saveAuditPassInsteadApply(Long anId, String anReason) {
        BTAssert.notNull(anId, "代录申请编号不允许为空！");

        // 根据条件查找
        final CustInsteadApply tempInsteadApply = insteadApplyService.selectByPrimaryKey(anId);
        BTAssert.notNull(tempInsteadApply, "没有找到相应的变更申请！");

        // 修改状态为审核通过
        final CustInsteadApply insteadApply = insteadApplyService.saveCustInsteadApply(anId, CustomerConstants.INSTEAD_APPLY_STATUS_AUDIT_PASS);
        BTAssert.notNull(insteadApply, "修改代录申请审核状态失败！");

        CustAuditLog auditLog = auditLogService.addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADAPPLY, anId, CustomerConstants.AUDIT_RESULT_PASS,
                anReason, null, insteadApply.getCustNo());
        BTAssert.notNull(auditLog, "审核记录添加失败!");

        return insteadApply;
    }

    /**
     * 
     * @param anId
     * @param anReason
     * @return
     */
    public CustInsteadApply saveAuditRejectInsteadApply(Long anId, String anReason) {
        BTAssert.notNull(anId, "代录申请编号不允许为空！");

        // 根据条件查找
        final CustInsteadApply tempInsteadApply = insteadApplyService.selectByPrimaryKey(anId);
        BTAssert.notNull(tempInsteadApply, "没有找到相应的变更申请！");

        // 修改状态为审核通过
        final CustInsteadApply insteadApply = insteadApplyService.saveCustInsteadApply(anId, CustomerConstants.INSTEAD_APPLY_STATUS_AUDIT_REJECT);
        BTAssert.notNull(insteadApply, "修改代录申请审核状态失败！");

        CustAuditLog auditLog = auditLogService.addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADAPPLY, anId,
                CustomerConstants.AUDIT_RESULT_REJECT, anReason, null, insteadApply.getCustNo());
        BTAssert.notNull(auditLog, "审核记录添加失败!");

        return insteadApply;
    }

    /**
     * 
     * @param anId
     * @param anReason
     * @return
     */
    public CustInsteadApply saveReviewPassInsteadApply(Long anId, String anReason) {
        BTAssert.notNull(anId, "代录申请编号不允许为空！");

        // 根据条件查找 查找 状态 INSTEAD_APPLY_STATUS_AUDIT_PASS
        final CustInsteadApply tempInsteadApply = insteadApplyService.selectByPrimaryKey(anId);
        BTAssert.notNull(tempInsteadApply, "没有找到相应的变更申请！");

        // 修改状态为审核通过
        final CustInsteadApply insteadApply = insteadApplyService.saveCustInsteadApply(anId, CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_PASS);
        BTAssert.notNull(insteadApply, "修改代录申请审核状态失败！");

        // 当前申请下所有代录记录 均复核，才可以通过
        List<CustInsteadRecord> insteadRecords = insteadRecordService.selectByProperty("applyId", anId);
        for (CustInsteadRecord insteadRecord : insteadRecords) {
            String businStatus = insteadRecord.getBusinStatus();
            if (BetterStringUtils.equals(businStatus, CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_PASS) == false) {
                throw new BytterTradeException(20008, "还有代录项目未复核！");
            }
        }

        CustAuditLog auditLog = auditLogService.addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADAPPLY, anId, CustomerConstants.AUDIT_RESULT_PASS,
                anReason, null, insteadApply.getCustNo());
        BTAssert.notNull(auditLog, "审核记录添加失败!");

        return insteadApply;
    }

    /**
     * 
     * @param anId
     * @param anReason
     * @return
     */
    public CustInsteadApply saveReviewRejectInsteadApply(Long anId, String anReason) {
        BTAssert.notNull(anId, "代录申请编号不允许为空！");

        // 根据条件查找
        final CustInsteadApply tempInsteadApply = insteadApplyService.selectByPrimaryKey(anId);
        BTAssert.notNull(tempInsteadApply, "没有找到相应的变更申请！");

        // 修改状态为审核通过
        final CustInsteadApply insteadApply = insteadApplyService.saveCustInsteadApply(anId, CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_REJECT);
        BTAssert.notNull(insteadApply, "修改代录申请审核状态失败！");

        CustAuditLog auditLog = auditLogService.addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADAPPLY, anId,
                CustomerConstants.AUDIT_RESULT_REJECT, anReason, null, insteadApply.getCustNo());
        BTAssert.notNull(auditLog, "审核记录添加失败!");

        return insteadApply;
    }

    /**
     * 
     * @param anId
     * @param anReason
     * @return
     */
    public CustInsteadApply saveConfirmPassInsteadApply(Long anId, String anReason) {
        BTAssert.notNull(anId, "代录申请编号不允许为空！");

        // 根据条件查找
        final CustInsteadApply tempInsteadApply = insteadApplyService.selectByPrimaryKey(anId);
        BTAssert.notNull(tempInsteadApply, "没有找到相应的变更申请！");

        // 将数据存入正式表
        String insteadType = tempInsteadApply.getInsteadType();
        if (BetterStringUtils.equals(insteadType, CustomerConstants.INSTEAD_APPLY_TYPE_OPENACCOUNT)) {
            // 这里处理开户代录，开户流程正式表  调用开户服务，并且修改新增的用户为已审核状态
            // TODO
        }
        else {
            // 当前申请下所有代录记录 均确认，才可以通过
            List<CustInsteadRecord> insteadRecords = insteadRecordService.selectByProperty("applyId", anId);
            for (CustInsteadRecord insteadRecord : insteadRecords) {
                String businStatus = insteadRecord.getBusinStatus();
                if (BetterStringUtils.equals(businStatus, CustomerConstants.INSTEAD_APPLY_STATUS_CONFIRM_PASS) == false) {
                    throw new BytterTradeException(20007, "还有代录项目未确认！");
                }
            }
            for (CustInsteadRecord insteadRecord : insteadRecords) {
                IFormalDataService formalDataService = FormalDataServiceHelper.getFormalDataService(insteadRecord);
                String[] tmpIds = BetterStringUtils.split(insteadRecord.getTmpIds(), ",");
                formalDataService.saveFormalData(tmpIds);
            }
        }
        // 修改状态为审核通过
        final CustInsteadApply insteadApply = insteadApplyService.saveCustInsteadApply(anId, CustomerConstants.INSTEAD_APPLY_STATUS_CONFIRM_PASS);
        BTAssert.notNull(insteadApply, "修改代录申请审核状态失败！");

        CustAuditLog auditLog = auditLogService.addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADAPPLY, anId, CustomerConstants.AUDIT_RESULT_PASS,
                anReason, null, insteadApply.getCustNo());
        BTAssert.notNull(auditLog, "审核记录添加失败!");

        return insteadApply;
    }

    /**
     * 
     * @param anId
     * @param anReason
     * @return
     */
    public CustInsteadApply saveConfirmRejectInsteadApply(Long anId, String anReason) {
        BTAssert.notNull(anId, "代录申请编号不允许为空！");

        // 根据条件查找 TODO
        final CustInsteadApply tempInsteadApply = insteadApplyService.selectByPrimaryKey(anId);
        BTAssert.notNull(tempInsteadApply, "没有找到相应的变更申请！");

        // 修改状态为审核通过
        final CustInsteadApply insteadApply = insteadApplyService.saveCustInsteadApply(anId, CustomerConstants.INSTEAD_APPLY_STATUS_CONFIRM_REJECT);
        BTAssert.notNull(insteadApply, "修改代录申请审核状态失败！");

        CustAuditLog auditLog = auditLogService.addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADAPPLY, anId,
                CustomerConstants.AUDIT_RESULT_REJECT, anReason, null, insteadApply.getCustNo());
        BTAssert.notNull(auditLog, "审核记录添加失败!");

        return insteadApply;
    }

    /**
     * 
     * @param anId
     * @return
     */
    public CustInsteadRecord saveReviewPassInsteadRecord(Long anId, String anReason) {
        BTAssert.notNull(anId, "代录记录编号不允许为空！");

        // 根据条件查找
        final CustInsteadRecord tempInsteadRecord = insteadRecordService.selectByPrimaryKey(anId);
        BTAssert.notNull(tempInsteadRecord, "没有找到相应的变更记录！");

        final CustInsteadRecord insteadRecord = insteadRecordService.saveCustInsteadRecordStatus(anId,
                CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_PASS);
        BTAssert.notNull(insteadRecord, "修改代录记录审核状态失败！");

        CustAuditLog auditLog = auditLogService.addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADRECORD, anId, CustomerConstants.AUDIT_RESULT_PASS,
                anReason, insteadRecord.getInsteadItem(), insteadRecord.getCustNo());
        BTAssert.notNull(auditLog, "审核记录添加失败!");

        return insteadRecord;
    }

    /**
     * 
     * @param anId
     * @param anReason
     * @return
     */
    public CustInsteadRecord saveReviewRejectInsteadRecord(Long anId, String anReason) {
        BTAssert.notNull(anId, "代录记录编号不允许为空！");

        // 根据条件查找
        final CustInsteadRecord tempInsteadRecord = insteadRecordService.selectByPrimaryKey(anId);
        BTAssert.notNull(tempInsteadRecord, "没有找到相应的变更记录！");

        final CustInsteadRecord insteadRecord = insteadRecordService.saveCustInsteadRecordStatus(anId,
                CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT);
        BTAssert.notNull(insteadRecord, "修改代录记录审核状态失败！");

        CustAuditLog auditLog = auditLogService.addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADRECORD, anId,
                CustomerConstants.AUDIT_RESULT_REJECT, anReason, insteadRecord.getInsteadItem(), insteadRecord.getCustNo());
        BTAssert.notNull(auditLog, "审核记录添加失败!");

        return insteadRecord;
    }

    /**
     * 
     * @param anId
     * @return
     */
    public CustInsteadRecord saveConfirmPassInsteadRecord(Long anId, String anReason) {
        BTAssert.notNull(anId, "代录记录编号不允许为空！");

        // 根据条件查找
        final CustInsteadRecord tempInsteadRecord = insteadRecordService.selectByPrimaryKey(anId);
        BTAssert.notNull(tempInsteadRecord, "没有找到相应的变更记录！");

        final CustInsteadRecord insteadRecord = insteadRecordService.saveCustInsteadRecordStatus(anId,
                CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_PASS);
        BTAssert.notNull(insteadRecord, "修改代录记录审核状态失败！");

        CustAuditLog auditLog = auditLogService.addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADRECORD, anId, CustomerConstants.AUDIT_RESULT_PASS,
                anReason, insteadRecord.getInsteadItem(), insteadRecord.getCustNo());
        BTAssert.notNull(auditLog, "审核记录添加失败!");

        return insteadRecord;
    }

    /**
     * 
     * @param anId
     * @param anReason
     * @return
     */
    public CustInsteadRecord saveConfirmRejectInsteadRecord(Long anId, String anReason) {
        BTAssert.notNull(anId, "代录记录编号不允许为空！");

        // 根据条件查找
        final CustInsteadRecord tempInsteadRecord = insteadRecordService.selectByPrimaryKey(anId);
        BTAssert.notNull(tempInsteadRecord, "没有找到相应的变更记录！");

        final CustInsteadRecord insteadRecord = insteadRecordService.saveCustInsteadRecordStatus(anId,
                CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);
        BTAssert.notNull(insteadRecord, "修改代录记录审核状态失败！");

        CustAuditLog auditLog = auditLogService.addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADRECORD, anId,
                CustomerConstants.AUDIT_RESULT_REJECT, anReason, insteadRecord.getInsteadItem(), insteadRecord.getCustNo());
        BTAssert.notNull(auditLog, "审核记录添加失败!");

        return insteadRecord;
    }

}
