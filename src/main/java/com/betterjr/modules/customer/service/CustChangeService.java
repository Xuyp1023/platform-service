package com.betterjr.modules.customer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.customer.constants.CustomerConstants;
import com.betterjr.modules.customer.data.ICustAuditEntityFace;
import com.betterjr.modules.customer.entity.CustAuditLog;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.helper.FormalDataHelper;
import com.betterjr.modules.customer.helper.IFormalDataService;
import com.betterjr.modules.document.ICustFileService;

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
    @Reference(interfaceClass = ICustFileService.class)
    private ICustFileService fileItemService;

    /**
     * 查询变更申请详情
     */
    public CustChangeApply findChangeApply(final Long anId, final String anChangeItem) {
        BTAssert.notNull(anId, "申请编号不允许为空！");

        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put(CustomerConstants.ID, anId);
        conditionMap.put("changeItem", anChangeItem);
        final List<CustChangeApply> changeApplys = changeApplyService.selectByProperty(conditionMap);

        if (Collections3.isEmpty(changeApplys) == true) {
            throw new BytterTradeException(20008, "变更申请详情没有找到！");
        }
        return Collections3.getFirst(changeApplys);
    }

    /**
     * 查询变更申请列表 分类查询
     */
    public Page<CustChangeApply> queryChangeApply(final Long anCustNo, final String anChangeItem, final int anFlag,
            final int anPageNum, final int anPageSize) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空!");
        BTAssert.notNull(anChangeItem, "变更项不允许为空!");

        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put(CustomerConstants.CUST_NO, anCustNo);
        conditionMap.put("changeItem", anChangeItem);

        return changeApplyService.queryCustChangeApply(conditionMap, anFlag, anPageNum, anPageSize);
    }

    /**
     * 查询变更申请列表 审核使用
     */
    public Page<CustChangeApply> queryChangeApplyList(final Map<String, Object> anParam, final int anFlag,
            final int anPageNum, final int anPageSize) {
        BTAssert.notNull(anParam, "查询条件不允许为空!");

        return changeApplyService.queryCustChangeApply(anParam, anFlag, anPageNum, anPageSize);
    }

    /**
     * 保存审核通过 将数据转入正式表
     */
    public CustChangeApply saveAuditPassChangeApply(final Long anId, final String anReason) {
        BTAssert.notNull(anId, "变更申请编号不允许为空!");

        final CustChangeApply tempChangeApply = changeApplyService.selectByPrimaryKey(anId);
        BTAssert.notNull(tempChangeApply, "没有找到相应的变更申请！");

        if (StringUtils.equals(tempChangeApply.getBusinStatus(),
                CustomerConstants.CHANGE_APPLY_STATUS_NEW) == false) {
            throw new BytterTradeException(20010, "变更申请状态不正确，审核失败！");
        }

        final CustChangeApply changeApply = changeApplyService.saveChangeApplyStatus(anId,
                CustomerConstants.CHANGE_APPLY_STATUS_AUDIT_PASS);
        BTAssert.notNull(changeApply, "修改变更申请审核状态失败！");

        final IFormalDataService formalDataService = FormalDataHelper.getFormalDataService(changeApply);
        BTAssert.notNull(formalDataService, "变更项目不正确！");

        final ICustAuditEntityFace entityFace = formalDataService.findSaveDataByParentId(changeApply.getId());

        formalDataService.saveFormalData(changeApply.getId());

        fileItemService.savePlatformAduitFile(entityFace.getCustNo(), entityFace.getBatchNo());

        final CustAuditLog auditLog = auditLogService.addCustAuditLog(CustomerConstants.AUDIT_TYPE_CHANGEAPPLY,
                CustomerConstants.AUDIT_STEP_AUDIT, anId, CustomerConstants.AUDIT_RESULT_PASS,
                StringUtils.isNotBlank(anReason) ? anReason : "同意", changeApply.getChangeItem(),
                changeApply.getCustNo());
        BTAssert.notNull(auditLog, "审核记录添加失败!");

        return changeApply;
    }

    /**
     * 保存审核驳回 修改状态
     */
    public CustChangeApply saveAuditRejectChangeApply(final Long anId, final String anReason) {
        BTAssert.isTrue(StringUtils.isNotBlank(anReason), "驳回原因不允许为空！");
        BTAssert.notNull(anId, "变更申请编号不允许为空!");

        final CustChangeApply tempChangeApply = changeApplyService.selectByPrimaryKey(anId);
        BTAssert.notNull(tempChangeApply, "没有找到相应的变更申请！");

        if (StringUtils.equals(tempChangeApply.getBusinStatus(),
                CustomerConstants.CHANGE_APPLY_STATUS_NEW) == false) {
            throw new BytterTradeException(20010, "变更申请状态不正确，审核失败！");
        }

        final CustChangeApply changeApply = changeApplyService.saveChangeApplyStatus(anId,
                CustomerConstants.CHANGE_APPLY_STATUS_AUDIT_REJECT);

        final CustAuditLog auditLog = auditLogService.addCustAuditLog(CustomerConstants.AUDIT_TYPE_CHANGEAPPLY,
                CustomerConstants.AUDIT_STEP_AUDIT, anId, CustomerConstants.AUDIT_RESULT_REJECT, anReason,
                changeApply.getChangeItem(), changeApply.getCustNo());
        BTAssert.notNull(auditLog, "审核记录添加失败!");

        return changeApply;
    }

    /**
     * 作废变更申请
     */
    public CustChangeApply saveCancelChangeApply(final Long anId, final String anReason) {
        BTAssert.notNull(anId, "变更申请编号不允许为空!");

        final CustChangeApply tempChangeApply = changeApplyService.findChangeApply(anId);
        BTAssert.notNull(tempChangeApply, "没有找到相应的变更申请！");
        // 检查变更申请状态

        final CustChangeApply changeApply = changeApplyService.saveChangeApplyStatus(anId,
                CustomerConstants.CHANGE_APPLY_STATUS_CANCEL);
        BTAssert.notNull(changeApply, "修改变更申请审核状态失败！");

        final IFormalDataService formalDataService = FormalDataHelper.getFormalDataService(changeApply);
        BTAssert.notNull(formalDataService, "变更项目不正确！");

        formalDataService.saveCancelData(changeApply.getId());

        final CustAuditLog auditLog = auditLogService.addCustAuditLog(CustomerConstants.AUDIT_TYPE_CHANGEAPPLY, null,
                anId, CustomerConstants.AUDIT_RESULT_CANCEL, anReason, changeApply.getChangeItem(),
                changeApply.getCustNo());
        BTAssert.notNull(auditLog, "审核记录添加失败!");

        return tempChangeApply;
    }

}
