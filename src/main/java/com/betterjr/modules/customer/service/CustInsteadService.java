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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.JedisUtils;
import com.betterjr.common.utils.QueryTermBuilder;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.service.CustOperatorService;
import com.betterjr.modules.customer.constants.CustomerConstants;
import com.betterjr.modules.customer.entity.CustAuditLog;
import com.betterjr.modules.customer.entity.CustInsteadApply;
import com.betterjr.modules.customer.entity.CustInsteadRecord;
import com.betterjr.modules.customer.entity.CustOpenAccountTmp;
import com.betterjr.modules.customer.helper.FormalDataHelper;
import com.betterjr.modules.customer.helper.IFormalDataService;
import com.betterjr.modules.sms.constants.SmsConstants;
import com.betterjr.modules.sms.entity.VerifyCode;
import com.betterjr.modules.sys.security.SystemAuthorizingRealm;
import com.betterjr.modules.sys.security.SystemAuthorizingRealm.HashPassword;

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
    
    @Resource
    private CustOperatorService custOperatorService;
    
    @Resource
    private CustOpenAccountTmpService custOpenAccountTmpService;
    
    private final static Pattern DEAL_PASSWORD_PATTERN = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,10}$");

    /**
     * 发起代录申请
     */
    public CustInsteadApply addInsteadApply(final Map<String, Object> anParam, final String anFileList) {
        BTAssert.notNull(anParam, "代录信息不允许为空！");

        final String insteadType = (String) anParam.get("insteadType");
        if ((insteadType.equals(CustomerConstants.INSTEAD_APPLY_TYPE_OPENACCOUNT)
                || insteadType.equals(CustomerConstants.INSTEAD_APPLY_TYPE_CHANGE)) == false) {
            throw new BytterTradeException(20040, "代录类型不正确");
        }
        Long custNo = null;
        if (insteadType.equals(CustomerConstants.INSTEAD_APPLY_TYPE_CHANGE) == true) { //变更代录才会有 custNo
            final String tempCustNo = (String) anParam.get("custNo");
            custNo = Long.valueOf(tempCustNo);
        }
        final CustInsteadApply custInsteadApply = insteadApplyService.addCustInsteadApply(insteadType, custNo, anFileList);

        final String insteadItems = (String) anParam.get("insteadItems");
        insteadRecordService.addInsteadRecord(custInsteadApply, insteadType, insteadItems);

        return custInsteadApply;
    }
    
    /**
     * PC端发起代录申请
     */
    public CustInsteadApply addOpenAccountInsteadApply(final String anCustName, final Long anOperId, final String anFileList) {
        // pc端默认值
        final String insteadType = CustomerConstants.INSTEAD_APPLY_TYPE_OPENACCOUNT;
        final String insteadItems = "0,0,0,0,0,0,0";
        Map<String, Object> anMap = QueryTermBuilder.newInstance().put("insteadType", insteadType).put("insteadItems", insteadItems).build();
        CustInsteadApply custInsteadApply = this.addInsteadApply(anMap, anFileList);
        //更新custName
        custInsteadApply.setCustName(anCustName);
        insteadApplyService.updateByPrimaryKeySelective(custInsteadApply);
        // 保存用户选择信息：客户名称、经办人信息
        CustOpenAccountTmp anCustOpenAccountTmp = this.addOpenAccountTmp(anCustName, anOperId);
        // 将开户信息保存至开户申请记录中
        fillInsteadRecordByAccountTmp(custInsteadApply.getId(), anCustOpenAccountTmp.getId());

        return custInsteadApply;
    }

    /**
     * 代录申请 - 修改申请
     */
    public CustInsteadApply saveInsteadApply(final Map<String, Object> anParam, final Long anApplyId, final String anFileList) {
        BTAssert.notNull(anApplyId, "代录编号不允许为空！");

        final CustInsteadApply tempCustInsteadApply = insteadApplyService.findCustInsteadApply(anApplyId);
        BTAssert.notNull(tempCustInsteadApply, "没有找到相应的代录申请!");

        if (BetterStringUtils.equals(tempCustInsteadApply.getBusinStatus(), CustomerConstants.INSTEAD_APPLY_STATUS_AUDIT_REJECT) == false) {
            throw new BytterTradeException("代录申请状态不允许修改！");
        }

        final CustInsteadApply custInsteadApply = insteadApplyService.saveCustInsteadApply(tempCustInsteadApply, anFileList);
        BTAssert.notNull(custInsteadApply, "保存代录申请发生错误!");

        final String insteadItems = (String) anParam.get("insteadItems");

        insteadRecordService.saveInsteadRecord(custInsteadApply, tempCustInsteadApply.getInsteadType(), insteadItems);
        return custInsteadApply;
    }

    /**
     * 代录申请列表 所有
     */
    public Page<CustInsteadApply> queryInsteadApplyList(final Map<String, Object> anParam, final int anFlag, final int anPageNum, final int anPageSize) {
        BTAssert.notEmpty(anParam, "查询参数不允许为空！");

        return insteadApplyService.queryCustInsteadApply(anParam, anFlag, anPageNum, anPageSize);
    }

    /**
     * 代录拥有列表
     */
    public Page<CustInsteadApply> queryInsteadApplyOwnList(final Map<String, Object> anParam, final int anFlag, final int anPageNum, final int anPageSize) {
        BTAssert.notEmpty(anParam, "查询参数不允许为空！");

        anParam.put("operOrg", UserUtils.getOperatorInfo().getOperOrg());
        return insteadApplyService.queryCustInsteadApply(anParam, anFlag, anPageNum, anPageSize);
    }

    /**
     * 代录申请列表 待审核 待录入 复核驳回
     */
    public Page<CustInsteadApply> queryInsteadApplyAuditList(final Map<String, Object> anParam, final int anFlag, final int anPageNum, final int anPageSize) {
        BTAssert.notEmpty(anParam, "查询参数不允许为空！");

        final String[] businStatues = {
                CustomerConstants.INSTEAD_APPLY_STATUS_NEW,
                CustomerConstants.INSTEAD_APPLY_STATUS_AUDIT_PASS,
                CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_REJECT,
                CustomerConstants.INSTEAD_APPLY_STATUS_CONFIRM_REJECT };
        anParam.put("businStatus", businStatues);
        return insteadApplyService.queryCustInsteadApply(anParam, anFlag, anPageNum, anPageSize);
    }

    /**
     * 代录申请列表 待复核
     */
    public Page<CustInsteadApply> queryInsteadApplyReviewList(final Map<String, Object> anParam, final int anFlag, final int anPageNum, final int anPageSize) {
        BTAssert.notEmpty(anParam, "查询参数不允许为空！");

        anParam.put("businStatus", CustomerConstants.INSTEAD_APPLY_STATUS_TYPE_IN);
        return insteadApplyService.queryCustInsteadApply(anParam, anFlag, anPageNum, anPageSize);
    }

    /**
     * 代录申请列表 待确认
     */
    public Page<CustInsteadApply> queryInsteadApplyConfirmList(final Map<String, Object> anParam, final int anFlag, final int anPageNum, final int anPageSize) {
        BTAssert.notEmpty(anParam, "查询参数不允许为空！");

        anParam.put("operOrg", UserUtils.getOperatorInfo().getOperOrg());
        anParam.put("businStatus", CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_PASS);

        return insteadApplyService.queryCustInsteadApply(anParam, anFlag, anPageNum, anPageSize);
    }

    /**
     * 查询代录项
     */
    public List<CustInsteadRecord> queryInsteadRecordByApply(final Long anApplyId) {
        BTAssert.notNull(anApplyId, "代录申请编号不允许为空");

        return insteadRecordService.queryInsteadRecordByApplyId(anApplyId);
    }

    /**
     * 修改代录申请状态： 审核通过
     */
    public CustInsteadApply saveAuditPassInsteadApply(final Long anId, final String anReason) {
        checkInsteadApply(anId, CustomerConstants.INSTEAD_APPLY_STATUS_NEW);

        final CustInsteadApply insteadApply = saveInsteadApplyStatus(anId, CustomerConstants.INSTEAD_APPLY_STATUS_AUDIT_PASS);

        addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADAPPLY, CustomerConstants.AUDIT_STEP_AUDIT, anId, CustomerConstants.AUDIT_RESULT_PASS,
                BetterStringUtils.isNotBlank(anReason) ? anReason : "同意" , null, insteadApply.getCustNo());

        return insteadApply;
    }

    /**
     * 修改代录申请状态： 审核驳回
     */
    public CustInsteadApply saveAuditRejectInsteadApply(final Long anId, final String anReason) {
        BTAssert.isTrue(BetterStringUtils.isNotBlank(anReason), "驳回原因不允许为空！");

        checkInsteadApply(anId, CustomerConstants.INSTEAD_APPLY_STATUS_NEW);

        final CustInsteadApply insteadApply = saveInsteadApplyStatus(anId, CustomerConstants.INSTEAD_APPLY_STATUS_AUDIT_REJECT);

        addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADAPPLY, CustomerConstants.AUDIT_STEP_AUDIT, anId, CustomerConstants.AUDIT_RESULT_REJECT,
                anReason, null, insteadApply.getCustNo());

        return insteadApply;
    }

    /**
     * 复核提交
     */
    public CustInsteadApply saveSubmitReviewInsteadApply(final Long anId) {
        checkInsteadApply(anId, CustomerConstants.INSTEAD_APPLY_STATUS_TYPE_IN);

        final List<CustInsteadRecord> insteadRecords = insteadRecordService.queryInsteadRecordByApplyId(anId);
        final List<String> businStatus = Arrays.asList(CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_PASS,
                CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT, CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_PASS);
        boolean includeReviewRejectFlag = false;
        for (final CustInsteadRecord insteadRecord : insteadRecords) {
            if (businStatus.contains(insteadRecord.getBusinStatus()) == false) {
                throw new BytterTradeException(20008, "还有未复核代录项目！");
            }
            if (BetterStringUtils.equals(insteadRecord.getBusinStatus(), CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT)) {
                includeReviewRejectFlag = true;
            }
        }

        if (includeReviewRejectFlag) {
            final CustInsteadApply insteadApply = saveInsteadApplyStatus(anId, CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_REJECT);

            addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADAPPLY, CustomerConstants.AUDIT_STEP_REVIEW, anId,
                    CustomerConstants.AUDIT_RESULT_REJECT, "复核驳回:有未审核通过项", null, insteadApply.getCustNo());

            return insteadApply;
        }
        else {
            final CustInsteadApply insteadApply = saveInsteadApplyStatus(anId, CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_PASS);

            addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADAPPLY, CustomerConstants.AUDIT_STEP_REVIEW, anId, CustomerConstants.AUDIT_RESULT_PASS,
                    "复核通过", null, insteadApply.getCustNo());

            return insteadApply;
        }

    }

    /**
     * 确认提交 这里只处理 变更代录
     */
    public CustInsteadApply saveSubmitConfirmInsteadApply(final Long anId) {
        BTAssert.notNull(anId, "代录申请编号不允许为空!");

        final CustInsteadApply tempInsteadApply = checkInsteadApply(anId, CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_PASS);

        final String insteadType = tempInsteadApply.getInsteadType();
        if (BetterStringUtils.equals(insteadType, CustomerConstants.INSTEAD_APPLY_TYPE_OPENACCOUNT) == true) {
            throw new BytterTradeException(20009, "开户代录不需要确认提交!");
        }

        final List<CustInsteadRecord> insteadRecords = insteadRecordService.selectByProperty("applyId", anId);
        final List<String> businStatus = Arrays.asList(
                CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_PASS,
                CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);
        boolean includeConfirmRejectFlag = false;
        for (final CustInsteadRecord insteadRecord : insteadRecords) {

            if (businStatus.contains(insteadRecord.getBusinStatus()) == false) {
                throw new BytterTradeException(20007, "还有代录项目未确认！");
            }

            if (BetterStringUtils.equals(insteadRecord.getBusinStatus(), CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT)) {
                includeConfirmRejectFlag = true;
            }
        }

        if (includeConfirmRejectFlag) {
            final CustInsteadApply insteadApply = saveInsteadApplyStatus(anId, CustomerConstants.INSTEAD_APPLY_STATUS_CONFIRM_REJECT);

            addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADAPPLY, CustomerConstants.AUDIT_STEP_CONFIRM, anId,
                    CustomerConstants.AUDIT_RESULT_REJECT, "确认驳回:有未确认通过项", null, insteadApply.getCustNo());

            return insteadApply;
        }
        else {
            for (final CustInsteadRecord insteadRecord : insteadRecords) {
                final IFormalDataService formalDataService = FormalDataHelper.getFormalDataService(insteadRecord);
                formalDataService.saveFormalData(insteadRecord.getId());
            }

            final CustInsteadApply insteadApply = saveInsteadApplyStatus(anId, CustomerConstants.INSTEAD_APPLY_STATUS_CONFIRM_PASS);

            addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADAPPLY, CustomerConstants.AUDIT_STEP_CONFIRM, anId,
                    CustomerConstants.AUDIT_RESULT_PASS, "确认通过", null, insteadApply.getCustNo());

            return insteadApply;
        }
    }

    /**
     * 撤销代录申请
     */
    public CustInsteadApply saveCancelInsteadApply(final Long anId, final String anReason) {
        BTAssert.notNull(anId, "代录申请编号不允许为空!");

        checkInsteadApply(anId);

        final List<CustInsteadRecord> insteadRecords = insteadRecordService.selectByProperty("applyId", anId);

        for (final CustInsteadRecord insteadRecord : insteadRecords) {
            final IFormalDataService formalDataService = FormalDataHelper.getFormalDataService(insteadRecord);
            formalDataService.saveCancelData(insteadRecord.getId());
        }

        final CustInsteadApply insteadApply = saveInsteadApplyStatus(anId, CustomerConstants.INSTEAD_APPLY_STATUS_CANCEL);

        addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADAPPLY, null, anId, CustomerConstants.AUDIT_RESULT_CANCEL, "作费申请", null,
                insteadApply.getCustNo());

        return insteadApply;
    }

    /**
     * 录入提交
     */
    public CustInsteadApply saveSubmitTypeInInsteadApply(final Long anId) {
        checkInsteadApply(anId,
                CustomerConstants.INSTEAD_APPLY_STATUS_AUDIT_PASS,
                CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_REJECT,
                CustomerConstants.INSTEAD_APPLY_STATUS_CONFIRM_REJECT);

        checkInsteadRecordByApplyId(anId,
                CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN,
                CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_PASS,
                CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_PASS);

        final CustInsteadApply insteadApply = saveInsteadApplyStatus(anId, CustomerConstants.INSTEAD_APPLY_STATUS_TYPE_IN);

        addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADAPPLY, CustomerConstants.AUDIT_STEP_TYPEIN, anId, CustomerConstants.AUDIT_RESULT_PASS,
                "代录提交", null, insteadApply.getCustNo());

        return insteadApply;
    }

    /**
     * 修改代录申请状态： 复核通过
     */
    @Deprecated
    public CustInsteadApply saveReviewPassInsteadApply(final Long anId, final String anReason) {
        checkInsteadApply(anId, CustomerConstants.INSTEAD_APPLY_STATUS_TYPE_IN);

        final List<CustInsteadRecord> insteadRecords = insteadRecordService.selectByProperty("applyId", anId);
        final List<String> businStatus = Arrays.asList(
                CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_PASS,
                CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_PASS);
        for (final CustInsteadRecord insteadRecord : insteadRecords) {
            if (businStatus.contains(insteadRecord.getBusinStatus()) == false) {
                throw new BytterTradeException(20008, "还有未复核代录项目！");
            }
        }

        final CustInsteadApply insteadApply = saveInsteadApplyStatus(anId, CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_PASS);

        addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADAPPLY, CustomerConstants.AUDIT_STEP_REVIEW, anId, CustomerConstants.AUDIT_RESULT_PASS,
                anReason, null, insteadApply.getCustNo());

        return insteadApply;
    }

    /**
     * 修改代录申请状态： 复核驳回
     */
    @Deprecated
    public CustInsteadApply saveReviewRejectInsteadApply(final Long anId, final String anReason) {
        checkInsteadApply(anId, CustomerConstants.INSTEAD_APPLY_STATUS_TYPE_IN);

        final CustInsteadApply insteadApply = saveInsteadApplyStatus(anId, CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_REJECT);

        addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADAPPLY, CustomerConstants.AUDIT_STEP_REVIEW, anId, CustomerConstants.AUDIT_RESULT_REJECT,
                anReason, null, insteadApply.getCustNo());

        return insteadApply;
    }

    /**
     * 修改代录申请状态： 确认通过
     */
    @Deprecated
    public CustInsteadApply saveConfirmPassInsteadApply(final Long anId, final String anReason) {
        final CustInsteadApply tempInsteadApply = checkInsteadApply(anId, CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_PASS);

        final String insteadType = tempInsteadApply.getInsteadType();
        if (BetterStringUtils.equals(insteadType, CustomerConstants.INSTEAD_APPLY_TYPE_OPENACCOUNT) == false) {
            final List<CustInsteadRecord> insteadRecords = insteadRecordService.selectByProperty("applyId", anId);
            for (final CustInsteadRecord insteadRecord : insteadRecords) {
                final String businStatus = insteadRecord.getBusinStatus();

                if (BetterStringUtils.equals(businStatus, CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_PASS) == false) {
                    throw new BytterTradeException(20007, "还有代录项目未确认！");
                }
            }
            for (final CustInsteadRecord insteadRecord : insteadRecords) {
                final IFormalDataService formalDataService = FormalDataHelper.getFormalDataService(insteadRecord);
                formalDataService.saveFormalData(insteadRecord.getId());
            }
        }

        final CustInsteadApply insteadApply = saveInsteadApplyStatus(anId, CustomerConstants.INSTEAD_APPLY_STATUS_CONFIRM_PASS);

        addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADAPPLY, CustomerConstants.AUDIT_STEP_CONFIRM, anId, CustomerConstants.AUDIT_RESULT_PASS,
                anReason, null, insteadApply.getCustNo());

        return insteadApply;
    }

    /**
     * 修改代录申请状态： 确认驳回
     */
    @Deprecated
    public CustInsteadApply saveConfirmRejectInsteadApply(final Long anId, final String anReason) {
        checkInsteadApply(anId, CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_PASS);

        final CustInsteadApply insteadApply = saveInsteadApplyStatus(anId, CustomerConstants.INSTEAD_APPLY_STATUS_CONFIRM_REJECT);

        addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADAPPLY, CustomerConstants.AUDIT_STEP_CONFIRM, anId, CustomerConstants.AUDIT_RESULT_REJECT,
                anReason, null, insteadApply.getCustNo());

        return insteadApply;
    }

    /**
     * 修改代录记录状态： 复核通过
     */
    public CustInsteadRecord saveReviewPassInsteadRecord(final Long anId, final String anReason) {
        checkInsteadRecord(anId, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN);

        final CustInsteadRecord insteadRecord = saveInsteadRecordStatus(anId, CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_PASS);

        addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADRECORD, CustomerConstants.AUDIT_STEP_REVIEW, anId, CustomerConstants.AUDIT_RESULT_PASS,
                BetterStringUtils.isNotBlank(anReason) ? anReason : "同意", insteadRecord.getInsteadItem(), insteadRecord.getCustNo());

        return insteadRecord;
    }

    /**
     * 修改代录记录状态： 复核驳回
     */
    public CustInsteadRecord saveReviewRejectInsteadRecord(final Long anId, final String anReason) {
//        BTAssert.isTrue(BetterStringUtils.isNotBlank(anReason), "驳回原因不允许为空！");

        checkInsteadRecord(anId, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN);

        final CustInsteadRecord insteadRecord = saveInsteadRecordStatus(anId, CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT);

        addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADRECORD, CustomerConstants.AUDIT_STEP_REVIEW, anId, CustomerConstants.AUDIT_RESULT_REJECT,
                anReason, insteadRecord.getInsteadItem(), insteadRecord.getCustNo());

        return insteadRecord;
    }

    /**
     * 修改代录记录状态： 确认通过
     *
     * 这里需要处理 开户的情况 如果是开户,需要将 申请也一并处理
     *
     */
    public CustInsteadRecord saveConfirmPassInsteadRecord(final Long anId, final String anReason) {
        final CustInsteadRecord tempInsteadRecord = checkInsteadRecord(anId, CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_PASS);

        final CustInsteadApply tempInsteadApply = checkInsteadApply(tempInsteadRecord.getApplyId(),
                CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_PASS);

        final String insteadType = tempInsteadApply.getInsteadType();
        if (BetterStringUtils.equals(insteadType, CustomerConstants.INSTEAD_APPLY_TYPE_OPENACCOUNT) == true) {
            final CustInsteadApply insteadApply = saveInsteadApplyStatus(tempInsteadApply.getId(),
                    CustomerConstants.INSTEAD_APPLY_STATUS_CONFIRM_PASS);

            addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADAPPLY, CustomerConstants.AUDIT_STEP_CONFIRM, anId,
                    CustomerConstants.AUDIT_RESULT_PASS, "确认通过", null, insteadApply.getCustNo());

            final IFormalDataService formalDataService = FormalDataHelper.getFormalDataService(tempInsteadRecord);
            formalDataService.saveFormalData(tempInsteadRecord.getId());
        }

        final CustInsteadRecord insteadRecord = saveInsteadRecordStatus(anId, CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_PASS);

        addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADRECORD, CustomerConstants.AUDIT_STEP_CONFIRM, anId, CustomerConstants.AUDIT_RESULT_PASS,
                BetterStringUtils.isNotBlank(anReason) ? anReason : "同意", insteadRecord.getInsteadItem(), insteadRecord.getCustNo());

        return insteadRecord;
    }

    /**
     * 修改代录记录状态： 确认驳回
     */
    public CustInsteadRecord saveConfirmRejectInsteadRecord(final Long anId, final String anReason) {
        BTAssert.isTrue(BetterStringUtils.isNotBlank(anReason), "驳回原因不允许为空！");

        final CustInsteadRecord tempInsteadRecord = checkInsteadRecord(anId, CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_PASS);

        final CustInsteadApply tempInsteadApply = checkInsteadApply(tempInsteadRecord.getApplyId(),
                CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_PASS);

        final String insteadType = tempInsteadApply.getInsteadType();
        if (BetterStringUtils.equals(insteadType, CustomerConstants.INSTEAD_APPLY_TYPE_OPENACCOUNT) == true) {
            final CustInsteadApply insteadApply = saveInsteadApplyStatus(tempInsteadApply.getId(),
                    CustomerConstants.INSTEAD_APPLY_STATUS_CONFIRM_REJECT);

            addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADAPPLY, CustomerConstants.AUDIT_STEP_CONFIRM, anId,
                    CustomerConstants.AUDIT_RESULT_REJECT, "确认驳回:未通过开户确认", null, insteadApply.getCustNo());
        }

        final CustInsteadRecord insteadRecord = saveInsteadRecordStatus(anId, CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        addCustAuditLog(CustomerConstants.AUDIT_TYPE_INSTEADRECORD, CustomerConstants.AUDIT_STEP_CONFIRM, anId, CustomerConstants.AUDIT_RESULT_REJECT,
                anReason, insteadRecord.getInsteadItem(), insteadRecord.getCustNo());

        return insteadRecord;
    }

    /**
     * 检查代录申请是否有效
     */
    public CustInsteadApply checkInsteadApply(final Long anId, final String... anBusinStatues) {
        BTAssert.notNull(anId, "代录申请编号不允许为空！");
        final Map<String, Object> conditionMap = new HashMap<>();
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
     */
    private void checkInsteadRecordByApplyId(final Long anApplyId, final String... businStatues) {
        BTAssert.notNull(anApplyId, "代录记录编号不允许为空！");
        BTAssert.notEmpty(businStatues, "状态不允许为空！");

        final List<String> businStatusList = Arrays.asList(businStatues);
        final List<CustInsteadRecord> insteadRecords = insteadRecordService.queryInsteadRecordByApplyId(anApplyId);
        for (final CustInsteadRecord insteadRecord : insteadRecords) {
            final String businStatus = insteadRecord.getBusinStatus();
            if (businStatusList.contains(businStatus) == false) {
                throw new BytterTradeException(20009, "代录项目状态不正确！");
            }
        }
    }

    /**
     * 检查代录记录编号是否有效
     */
    public CustInsteadRecord checkInsteadRecord(final Long anId, final String... anBusinStatus) {
        BTAssert.notNull(anId, "代录记录编号不允许为空！");
        final CustInsteadRecord tempInsteadRecord = insteadRecordService.selectByPrimaryKey(anId);
        BTAssert.notNull(tempInsteadRecord, "没有找到相应的代录记录！");

        final List<String> businStatus = Arrays.asList(anBusinStatus);

        if (businStatus.contains(tempInsteadRecord.getBusinStatus()) == false) {
            throw new BytterTradeException(20099, "代录记录状态不正确!");
        }
        return tempInsteadRecord;
    }

    /**
     * 修改代录申请状态
     */
    public CustInsteadApply saveInsteadApplyStatus(final Long anId, final String anBusinStatus) {
        final CustInsteadApply insteadApply = insteadApplyService.saveCustInsteadApplyStatus(anId, anBusinStatus);
        BTAssert.notNull(insteadApply, "修改代录申请审核状态失败！");
        return insteadApply;
    }

    /**
     * 修改代录记录状态
     */
    public CustInsteadRecord saveInsteadRecordStatus(final Long anId, final String anBusinStatus) {
        final CustInsteadRecord insteadRecord = insteadRecordService.saveInsteadRecordStatus(anId, anBusinStatus);
        BTAssert.notNull(insteadRecord, "修改代录记录审核状态失败！");
        return insteadRecord;
    }

    /**
     * 添加审核记录
     */
    public void addCustAuditLog(final String anAuditType, final String anStepNode, final Long anBusinId, final String anAuditResult, final String anReason, final String anInsteadItem,
            final Long anCustNo) {
        final CustAuditLog auditLog = auditLogService.addCustAuditLog(anAuditType, anStepNode, anBusinId, anAuditResult, anReason, anInsteadItem, anCustNo);
        BTAssert.notNull(auditLog, "审核记录添加失败!");
    }
    

    /**
     * 保存PC端填写开户数据至信息表
     */
    private CustOpenAccountTmp addOpenAccountTmp(String anCustName, Long anOperId) {
        CustOpenAccountTmp anCustOpenAccountTmp = new CustOpenAccountTmp();
        anCustOpenAccountTmp.initAddValue();
        anCustOpenAccountTmp.setCustName(anCustName);
        CustOperatorInfo anOperator = custOperatorService.selectByPrimaryKey(anOperId);
        // 经办人信息
        anCustOpenAccountTmp.setOperName(anOperator.getName());
        anCustOpenAccountTmp.setOperIdenttype(anOperator.getIdentType());
        anCustOpenAccountTmp.setOperIdentno(anOperator.getIdentNo());
        anCustOpenAccountTmp.setOperValiddate(anOperator.getValidDate());
        anCustOpenAccountTmp.setOperMobile(anOperator.getMobileNo());
        anCustOpenAccountTmp.setOperEmail(anOperator.getEmail());
        anCustOpenAccountTmp.setOperPhone(anOperator.getPhone());
        anCustOpenAccountTmp.setOperFaxNo(anOperator.getFaxNo());
        //初始化代录默认值(由于前端默认值实现被覆盖，不得已采用后端赋默认值)
        anCustOpenAccountTmp.initDefaultValue();
        //开户类型 "1"--PC代录
        anCustOpenAccountTmp.setDataSource(CustomerConstants.OPEN_ACCOUNT_TYPE_PC_INSTEAD);
        //保存数据
        custOpenAccountTmpService.insert(anCustOpenAccountTmp);
        return anCustOpenAccountTmp;
    }
    
    /**
     * 将开户信息保存至开户申请记录
     */
    private void fillInsteadRecordByAccountTmp(Long anApplyId, Long anAccountTmpid) {
        // 查询对应insteadRecord
        Map<String, Object> anMap = QueryTermBuilder.newInstance().put("applyId", anApplyId).put("insteadItem", CustomerConstants.ITEM_OPENACCOUNT)
                .build();
        CustInsteadRecord insteadRecord = Collections3.getFirst(insteadRecordService.selectByProperty(anMap));
        insteadRecord.setTmpIds(anAccountTmpid.toString());
        insteadRecordService.updateByPrimaryKeySelective(insteadRecord);
    }
    
    /**
     * 微信端代录申请
     * !!-- 在此处生成operId、OperName、OperOrg --!!
     */
    public CustInsteadApply wechatAddInsteadApply(Map<String, Object> anMap, Long anId, String anFileList) {
        //取出相应数据
        final String anCustName = (String) anMap.get("custName");
        //验证码
        final String anVerifyCode = (String) anMap.get("verifyCode");
        
        //获取开户信息
        CustOpenAccountTmp anOpenAccountInfo = custOpenAccountTmpService.selectByPrimaryKey(anId);
        BTAssert.notNull(anOpenAccountInfo, "无法获取开户信息！");
        //根据请求处理密码相关
        generatePassword(anOpenAccountInfo, anMap);
        //验证手机验证码
        verifyMobileMessage(anOpenAccountInfo.getOperMobile(), anVerifyCode);
        
        //生成代录申请及代录记录
        CustInsteadApply custInsteadApply = addWeChatInsteadApply(anCustName, anFileList);
        
        //生成operOrg
        anOpenAccountInfo.setOperOrg(custInsteadApply.getOperOrg());
        anOpenAccountInfo.setRegOperId(custInsteadApply.getRegOperId());
        anOpenAccountInfo.setRegOperName(custInsteadApply.getRegOperName());
        //设置开户类型 "2"--微信代录
        anOpenAccountInfo.setDataSource(CustomerConstants.OPEN_ACCOUNT_TYPE_WECHAT_INSTEAD);
        //更新开户信息数据
        custOpenAccountTmpService.updateByPrimaryKey(anOpenAccountInfo);
        
        // 保存用户选择信息：客户名称、经办人信息。讲信息与申请进行关联
        fillInsteadRecordByAccountTmp(custInsteadApply.getId(), anOpenAccountInfo.getId());

        return custInsteadApply;
    }
    
    /**
     * 根据入参填充相应密码信息
     */
    public void generatePassword(CustOpenAccountTmp anAccountTmp, Map<String, Object> anMap) {
        //交易密码
        final String anNewDealPassword = (String) anMap.get("newDealPassword");
        final String anOkDealPassword = (String) anMap.get("okDealPassword");
        //登录信息
        final String anLoginUserName = (String) anMap.get("loginUserName");
        final String anNewLoginPassword = (String) anMap.get("newLoginPassword");
        final String anOkLoginPassword = (String) anMap.get("okLoginPassword");
        //数据校验
        if(BetterStringUtils.isEmpty(anLoginUserName)) {
            BTAssert.notNull(null, "用户登录名不能为空！");
        }
        if(!BetterStringUtils.equals(anNewLoginPassword, anOkLoginPassword)) {
            BTAssert.notNull(null, "所输入两次登录密码不一致，请检查！");
        }
        if(!BetterStringUtils.equals(anNewDealPassword, anOkDealPassword)) {
            BTAssert.notNull(null, "所输入两次登录密码不一致，请检查！");
        }
        if(!DEAL_PASSWORD_PATTERN.matcher(anNewDealPassword).matches()) {
            BTAssert.notNull(null, "交易密码为6-18位并包含数字和字母！");
        }
        if(!DEAL_PASSWORD_PATTERN.matcher(anNewLoginPassword).matches()) {
            BTAssert.notNull(null, "交易密码为6-18位并包含数字和字母！");
        }
        HashPassword dealPassResult = SystemAuthorizingRealm.encrypt(anNewDealPassword);
        HashPassword loginPassResult = SystemAuthorizingRealm.encrypt(anNewLoginPassword);
        anAccountTmp.setDealPassword(dealPassResult.password);
        anAccountTmp.setDealPasswordSalt(dealPassResult.salt);
        anAccountTmp.setLoginUserName(anLoginUserName);
        anAccountTmp.setLoginPassword(loginPassResult.password);
        anAccountTmp.setLoginPasswordSalt(loginPassResult.salt);
    }
    
    /**
     * 校验手机验证码
     */
    private void verifyMobileMessage(String anMobile, String anVerifyCode) {

        final VerifyCode verifyCode = JedisUtils.getObject(SmsConstants.smsOpenAccountVerifyCodePrefix + anMobile);
        BTAssert.notNull(verifyCode, "验证码已过期");

        if (BetterStringUtils.equals(verifyCode.getVerifiCode(), anVerifyCode)) {
        } else {
            throw new BytterTradeException(40001, "验证码不正确!");
        }
    }
    
    
    /**
     * 微信代录申请
     */
    public CustInsteadApply addWeChatInsteadApply(final String anCustName, final String anFileList) {
        final String insteadType = CustomerConstants.INSTEAD_APPLY_TYPE_OPENACCOUNT;
        final String insteadItems = "0,0,0,0,0,0,0";
        final CustInsteadApply custInsteadApply = insteadApplyService.addWeChatCustInsteadApply(insteadType, anCustName, anFileList);
        insteadRecordService.addInsteadRecord(custInsteadApply, insteadType, insteadItems);

        return custInsteadApply;
    }
    
    /**
     * 根据开户信息tmp id 查询开户申请
     */
    public CustInsteadApply findInsteadApplyByAccountTmpId(Long anId) {
        return insteadApplyService.findInsteadApplyByAccountTmpId(anId);
    }
    
    /**
     * 代录开户激活操作
     */
    public CustInsteadRecord saveActiveOpenAccount(Long anId) {
        CustInsteadRecord anInsteadRecord = insteadRecordService.selectByPrimaryKey(anId);
        //调用原有确认开户操作
        this.saveConfirmPassInsteadRecord(anId, "代录开户激活");
        //调用原有提交操作   已经修改。不需再调用
//        CustInsteadApply anInsteadApply = custInsteadService.saveSubmitConfirmInsteadApply(anInsteadRecord.getApplyId());
        return anInsteadRecord;
    }
    
}
