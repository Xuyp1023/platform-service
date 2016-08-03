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

import java.util.ArrayList;
import java.util.Arrays;
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
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.entity.CustAuditLog;
import com.betterjr.modules.customer.entity.CustInsteadApply;
import com.betterjr.modules.customer.entity.CustInsteadRecord;
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
    public CustInsteadApply addInsteadApply(Map<String, Object> anParam, String anFileList) {
        BTAssert.notNull(anParam, "代录信息不允许为空！");

        String insteadType = (String) anParam.get("insteadType");
        if ((insteadType.equals(CustomerConstants.INSTEAD_APPLY_TYPE_OPENACCOUNT)
                || insteadType.equals(CustomerConstants.INSTEAD_APPLY_TYPE_CHANGE)) == false) {
            throw new BytterTradeException(20040, "代录类型不正确");
        }
        
        // TODO 处理上传  代录申请附件

        String tempCustNo = (String) anParam.get("custNo");
        Long custNo = Long.valueOf(tempCustNo);
        final CustInsteadApply custInsteadApply = insteadApplyService.addCustInsteadApply(insteadType, custNo);

        Long applyId = custInsteadApply.getId();
        String insteadItems = (String) anParam.get("insteadItems");
        insteadRecordService.addCustInsteadRecord(custInsteadApply, insteadType, insteadItems);

        return custInsteadApply;
    }
    
    /**
     * 代录申请 - 修改申请
     * 
     * @return
     */
    public CustInsteadApply saveInsteadApply(Map<String, Object> anParam, Long anApplyId, String anFileList) {
        
        // TODO 处理上传  代录申请附件
        
        return null;
    }

    /**
     * 代录申请列表 所有
     * 
     * @param anParam
     * @param anFlag
     * @param anPageNum
     * @param anPageSize
     * @return
     */
    public Page<CustInsteadApply> queryInsteadApplyList(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        BTAssert.notEmpty(anParam, "查询参数不允许为空！");

        return insteadApplyService.queryCustInsteadApply(anParam, anFlag, anPageNum, anPageSize);
    }

    /**
     * 代录申请列表 待审核 待录入 复核驳回
     * 
     * @param anParam
     * @param anFlag
     * @param anPageNum
     * @param anPageSize
     * @return
     */
    public Page<CustInsteadApply> queryInsteadApplyAuditList(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        BTAssert.notEmpty(anParam, "查询参数不允许为空！");

        String[] businStatues = { CustomerConstants.INSTEAD_APPLY_STATUS_NEW, CustomerConstants.INSTEAD_APPLY_STATUS_AUDIT_PASS,
                CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_REJECT };
        anParam.put("businStatus", businStatues);
        return insteadApplyService.queryCustInsteadApply(anParam, anFlag, anPageNum, anPageSize);
    }

    /**
     * 代录申请列表 待复核
     * 
     * @param anParam
     * @param anFlag
     * @param anPageNum
     * @param anPageSize
     * @return
     */
    public Page<CustInsteadApply> queryInsteadApplyReviewList(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        BTAssert.notEmpty(anParam, "查询参数不允许为空！");

        anParam.put("businStatus", CustomerConstants.INSTEAD_APPLY_STATUS_TYPE_IN);
        return insteadApplyService.queryCustInsteadApply(anParam, anFlag, anPageNum, anPageSize);
    }

    /**
     * 代录申请列表 待确认
     * 
     * @param anParam
     * @param anFlag
     * @param anPageNum
     * @param anPageSize
     * @return
     */
    public Page<CustInsteadApply> queryInsteadApplyConfirmList(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        BTAssert.notEmpty(anParam, "查询参数不允许为空！");

        anParam.put("operOrg", UserUtils.getOperatorInfo().getOperOrg());
        anParam.put("businStatus", CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_PASS);

        return insteadApplyService.queryCustInsteadApply(anParam, anFlag, anPageNum, anPageSize);
    }

    /**
     * 查询代录项
     * @param anApplyId
     * @return
     */
    public List<CustInsteadRecord> queryInsteadRecordByApply(Long anApplyId) {
        BTAssert.notNull(anApplyId, "代录申请编号不允许为空");
        
        return insteadRecordService.findCustInsteadRecordByApplyId(anApplyId);
    }
    
    /**
     * 修改代录申请状态： 审核通过
     * 
     * @param anId
     * @param anReason
     * @return
     */
    public CustInsteadApply saveAuditPassInsteadApply(Long anId, String anReason) {
        // 检查代录申请
        checkInsteadApply(anId, CustomerConstants.INSTEAD_APPLY_STATUS_NEW);

        // 修改代录申请状态： 审核通过
        final CustInsteadApply insteadApply = saveInsteadApplyStatus(anId, CustomerConstants.INSTEAD_APPLY_STATUS_AUDIT_PASS);

        // 添加审核日志
        addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADAPPLY, anId, CustomerConstants.AUDIT_RESULT_PASS, anReason, null,
                insteadApply.getCustNo());

        return insteadApply;
    }

    /**
     * 修改代录申请状态： 审核驳回
     * 
     * @param anId
     * @param anReason
     * @return
     */
    public CustInsteadApply saveAuditRejectInsteadApply(Long anId, String anReason) {
        // 检查代录申请
        checkInsteadApply(anId, CustomerConstants.INSTEAD_APPLY_STATUS_NEW);

        // 修改代录申请状态： 审核驳回
        final CustInsteadApply insteadApply = saveInsteadApplyStatus(anId, CustomerConstants.INSTEAD_APPLY_STATUS_AUDIT_REJECT);

        // 添加审核日志
        addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADAPPLY, anId, CustomerConstants.AUDIT_RESULT_REJECT, anReason, null,
                insteadApply.getCustNo());

        return insteadApply;
    }

    /**
     * 
     * @param anId
     * @return
     */
    public CustInsteadApply saveSubmitTypeInInsteadApply(Long anId) {
        // 检查代录申请
        checkInsteadApply(anId, CustomerConstants.INSTEAD_APPLY_STATUS_AUDIT_PASS, CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_REJECT,
                CustomerConstants.INSTEAD_APPLY_STATUS_CONFIRM_REJECT);
        
        // 检查所有子项状态 符合已录入，审核通过，确认通过
        checkInsteadRecordByApplyId(anId, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN, 
                CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_PASS, CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_PASS);
        
        // 修改代录申请状态：已录入待审核
        final CustInsteadApply insteadApply = saveInsteadApplyStatus(anId, CustomerConstants.INSTEAD_APPLY_STATUS_TYPE_IN);

        return insteadApply;
    }

    /**
     * 修改代录申请状态： 复核通过
     * 
     * @param anId
     * @param anReason
     * @return
     */
    public CustInsteadApply saveReviewPassInsteadApply(Long anId, String anReason) {
        // 检查代录申请
        checkInsteadApply(anId, CustomerConstants.INSTEAD_APPLY_STATUS_TYPE_IN);

        // 当前申请下所有代录记录 均复核，才可以通过
        List<CustInsteadRecord> insteadRecords = insteadRecordService.selectByProperty("applyId", anId);
        List<String> businStatus = Arrays.asList(CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_PASS, CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_PASS);
        for (CustInsteadRecord insteadRecord : insteadRecords) {
            if (businStatus.contains(insteadRecord.getBusinStatus()) == false) {
                throw new BytterTradeException(20008, "还有未复核代录项目！");
            }
        }

        // 修改代录申请状态： 复核通过
        final CustInsteadApply insteadApply = saveInsteadApplyStatus(anId, CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_PASS);

        // 添加审核日志
        addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADAPPLY, anId, CustomerConstants.AUDIT_RESULT_PASS, anReason, null,
                insteadApply.getCustNo());

        return insteadApply;
    }

    /**
     * 修改代录申请状态： 复核驳回
     * 
     * @param anId
     * @param anReason
     * @return
     */
    public CustInsteadApply saveReviewRejectInsteadApply(Long anId, String anReason) {
        // 检查代录申请
        checkInsteadApply(anId, CustomerConstants.INSTEAD_APPLY_STATUS_TYPE_IN);

        // 修改代录申请状态： 复核驳回
        final CustInsteadApply insteadApply = saveInsteadApplyStatus(anId, CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_REJECT);

        // 添加审核日志
        addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADAPPLY, anId, CustomerConstants.AUDIT_RESULT_REJECT, anReason, null,
                insteadApply.getCustNo());

        return insteadApply;
    }

    /**
     * 修改代录申请状态： 确认通过
     * 
     * @param anId
     * @param anReason
     * @return
     */
    public CustInsteadApply saveConfirmPassInsteadApply(Long anId, String anReason) {
        // 检查并取得代录申请
        final CustInsteadApply tempInsteadApply = checkInsteadApply(anId, CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_PASS);

        // 将数据存入正式表
        String insteadType = tempInsteadApply.getInsteadType();
        if (BetterStringUtils.equals(insteadType, CustomerConstants.INSTEAD_APPLY_TYPE_OPENACCOUNT)) {
            // 这里处理开户代录，开户流程正式表 调用开户服务，并且修改新增的用户为已审核状态
            // TODO
        }
        else {
            // 当前申请下所有代录记录 均确认，才可以通过
            List<CustInsteadRecord> insteadRecords = insteadRecordService.selectByProperty("applyId", anId);
            for (CustInsteadRecord insteadRecord : insteadRecords) {
                String businStatus = insteadRecord.getBusinStatus();
                
                if (BetterStringUtils.equals(businStatus, CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_PASS) == false) {
                    throw new BytterTradeException(20007, "还有代录项目未确认！");
                }
            }
            for (CustInsteadRecord insteadRecord : insteadRecords) {
                IFormalDataService formalDataService = FormalDataServiceHelper.getFormalDataService(insteadRecord);
                String[] tmpIds = BetterStringUtils.split(insteadRecord.getTmpIds(), ",");
                formalDataService.saveFormalData(tmpIds);
            }
        }

        // 修改代录申请状态： 确认通过
        final CustInsteadApply insteadApply = saveInsteadApplyStatus(anId, CustomerConstants.INSTEAD_APPLY_STATUS_CONFIRM_PASS);

        // 添加审核日志
        addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADAPPLY, anId, CustomerConstants.AUDIT_RESULT_PASS, anReason, null,
                insteadApply.getCustNo());

        return insteadApply;
    }

    /**
     * 修改代录申请状态： 确认驳回
     * 
     * @param anId
     * @param anReason
     * @return
     */
    public CustInsteadApply saveConfirmRejectInsteadApply(Long anId, String anReason) {
        // 检查代录申请
        checkInsteadApply(anId, CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_PASS);

        // 修改代录申请状态： 确认驳回
        final CustInsteadApply insteadApply = saveInsteadApplyStatus(anId, CustomerConstants.INSTEAD_APPLY_STATUS_CONFIRM_REJECT);

        // 添加审核日志
        addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADAPPLY, anId, CustomerConstants.AUDIT_RESULT_REJECT, anReason, null,
                insteadApply.getCustNo());

        return insteadApply;
    }

    /**
     * 修改代录记录状态： 复核通过
     * 
     * @param anId
     * @return
     */
    public CustInsteadRecord saveReviewPassInsteadRecord(Long anId, String anReason) {
        // 检查代录记录
        checkInsteadRecord(anId, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN);

        // 修改代录记录状态： 复核通过
        final CustInsteadRecord insteadRecord = saveInsteadRecordStatus(anId, CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_PASS);

        // 添加审核日志
        addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADRECORD, anId, CustomerConstants.AUDIT_RESULT_PASS, anReason,
                insteadRecord.getInsteadItem(), insteadRecord.getCustNo());

        return insteadRecord;
    }

    /**
     * 修改代录记录状态： 复核驳回
     * 
     * @param anId
     * @param anReason
     * @return
     */
    public CustInsteadRecord saveReviewRejectInsteadRecord(Long anId, String anReason) {
        // 检查代录记录
        checkInsteadRecord(anId, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN);

        // 修改代录记录状态： 复核驳回
        final CustInsteadRecord insteadRecord = saveInsteadRecordStatus(anId, CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT);

        // 添加审核日志
        addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADRECORD, anId, CustomerConstants.AUDIT_RESULT_REJECT, anReason,
                insteadRecord.getInsteadItem(), insteadRecord.getCustNo());

        return insteadRecord;
    }

    /**
     * 修改代录记录状态： 确认通过
     * 
     * @param anId
     * @return
     */
    public CustInsteadRecord saveConfirmPassInsteadRecord(Long anId, String anReason) {
        // 检查代录记录
        checkInsteadRecord(anId, CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_PASS);

        // 修改代录记录状态： 确认通过
        final CustInsteadRecord insteadRecord = saveInsteadRecordStatus(anId, CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_PASS);

        // 添加审核日志
        addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADRECORD, anId, CustomerConstants.AUDIT_RESULT_PASS, anReason,
                insteadRecord.getInsteadItem(), insteadRecord.getCustNo());

        return insteadRecord;
    }

    /**
     * 修改代录记录状态： 确认驳回
     * 
     * @param anId
     * @param anReason
     * @return
     */
    public CustInsteadRecord saveConfirmRejectInsteadRecord(Long anId, String anReason) {
        // 检查代录记录
        checkInsteadRecord(anId, CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_PASS);

        // 修改代录记录状态： 确认驳回
        final CustInsteadRecord insteadRecord = saveInsteadRecordStatus(anId, CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        // 添加审核日志
        addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADRECORD, anId, CustomerConstants.AUDIT_RESULT_REJECT, anReason,
                insteadRecord.getInsteadItem(), insteadRecord.getCustNo());

        return insteadRecord;
    }

    /**
     * 检查代录申请是否有效
     * 
     * @param anId
     */
    public CustInsteadApply checkInsteadApply(Long anId, String... anBusinStatues) {
        BTAssert.notNull(anId, "代录申请编号不允许为空！");
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put(CustomerConstants.ID, anId);
        if (Collections3.isEmpty(anBusinStatues) == false) {
            conditionMap.put("businStatus", anBusinStatues);
        }
        final List<CustInsteadApply> insteadApplyes = insteadApplyService.selectByProperty(conditionMap);
        BTAssert.notEmpty(insteadApplyes, "没有找到相应的代录申请！");
        return Collections3.getFirst(insteadApplyes);
    }

    /**
     * 检查所有代录记录状态是否符合
     * @param anApplyId
     * @param businStatues
     */
    private void checkInsteadRecordByApplyId(Long anApplyId, String ... businStatues) {
        BTAssert.notNull(anApplyId, "代录记录编号不允许为空！");
        BTAssert.notEmpty(businStatues, "状态不允许为空！");
        
        List<String> businStatusList = Arrays.asList(businStatues);
        List<CustInsteadRecord> insteadRecords = insteadRecordService.findCustInsteadRecordByApplyId(anApplyId);
        for (CustInsteadRecord insteadRecord: insteadRecords) {
            String businStatus = insteadRecord.getBusinStatus();
            if (businStatusList.contains(businStatus) == false) {
                throw new BytterTradeException(20009, "代录项目状态不正确！");
            }
        }
    }
    
    /**
     * 检查代录记录编号是否有效
     * 
     * @param anId
     */
    public CustInsteadRecord checkInsteadRecord(Long anId, String ... anBusinStatus) {
        BTAssert.notNull(anId, "代录记录编号不允许为空！");
        final CustInsteadRecord tempInsteadRecord = insteadRecordService.selectByPrimaryKey(anId);
        BTAssert.notNull(tempInsteadRecord, "没有找到相应的代录记录！");
        
        List<String> businStatus = Arrays.asList(anBusinStatus);
        
        if (businStatus.contains(tempInsteadRecord.getBusinStatus()) == false) {
            throw new BytterTradeException(20099, "代录记录状态不正确!");
        }
        return tempInsteadRecord;
    }

    /**
     * 修改代录申请状态
     * 
     * @param anId
     * @param anBusinStatus
     * @return
     */
    public CustInsteadApply saveInsteadApplyStatus(Long anId, String anBusinStatus) {
        final CustInsteadApply insteadApply = insteadApplyService.saveCustInsteadApply(anId, anBusinStatus);
        BTAssert.notNull(insteadApply, "修改代录申请审核状态失败！");
        return insteadApply;
    }

    /**
     * 修改代录记录状态
     * 
     * @param anId
     * @param anBusinStatus
     */
    public CustInsteadRecord saveInsteadRecordStatus(Long anId, String anBusinStatus) {
        final CustInsteadRecord insteadRecord = insteadRecordService.saveCustInsteadRecordStatus(anId, anBusinStatus);
        BTAssert.notNull(insteadRecord, "修改代录记录审核状态失败！");
        return insteadRecord;
    }

    /**
     * 添加审核记录
     * 
     * @param anAuditType
     * @param anBusinId
     * @param anAuditResult
     * @param anReason
     * @param anInsteadItem
     * @param anCustNo
     */
    public void addCustAuditLog(String anAuditType, Long anBusinId, String anAuditResult, String anReason, String anInsteadItem, Long anCustNo) {
        CustAuditLog auditLog = auditLogService.addCustAuditLog(anAuditType, anBusinId, anAuditResult, anReason, anInsteadItem, anCustNo);
        BTAssert.notNull(auditLog, "审核记录添加失败!");
    }


}
