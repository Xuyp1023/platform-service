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
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustInsteadApply;
import com.betterjr.modules.customer.entity.CustInsteadRecord;
import com.betterjr.modules.customer.entity.CustMechBaseTmp;

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
    private CustMechBaseTmpService baseTmpService;

    @Resource
    private CustMechBankAccountTmpService bankAccountTmpService;

    @Resource
    private CustMechBusinLicenceTmpService businLicenceTmpService;

    @Resource
    private CustMechLawTmpService lawTmpService;

    @Resource
    private CustMechContacterTmpService contacterTmpService;

    @Resource
    private CustMechManagerTmpService managerTmpService;

    @Resource
    private CustMechShareholderTmpService shareholderTmpService;

    @Resource
    private CustOpenAccountTmpService openAccountTmpService;

    /**
     * 保存基本信息代录内容
     * 
     * @param anCustMechBaseTmp
     */
    public CustMechBaseTmp saveCustMechBaseInsteadRecord(CustMechBaseTmp anCustMechBaseTmp, Long anId) {
        BTAssert.notNull(anCustMechBaseTmp, "公司基本信息不允许为空");
        BTAssert.notNull(anId, "代录项目编号不允许为空！");

        final CustMechBaseTmp custMechBaseTmp = baseTmpService.addCustMechBaseTmp(anCustMechBaseTmp, "0");
        final String tmpIds = String.valueOf(custMechBaseTmp.getId());
        final CustInsteadRecord custInsteadRecord = insteadRecordService.saveCustInsteadRecord(anId, "0", tmpIds);
        return custMechBaseTmp;
    }

    /**
     * 查询客户基本信息变更申请列表
     * 
     * @param anCustNo
     * @return
     */
    public Page<CustInsteadRecord> queryCustMechBaseInsteadRecord(Long anCustNo, int anFlag, int anPageNum, int anPageSize) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");

        final Map<String, Object> conditionMap = buildConditionMap(anCustNo, CustomerConstants.CHANGE_ITEM_BASE);
        return insteadRecordService.selectPropertyByPage(conditionMap, anPageNum, anPageSize, anFlag == 1);
    }

    /**
     * 查询客户基本信息变更申请详情
     * 
     * @param anCustNo
     * @param anId
     * @return
     */
    public CustInsteadRecord findCustMechBaseInsteadRecord(Long anCustNo, Long anId) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        BTAssert.notNull(anId, "申请编号不允许为空！");

        final Map<String, Object> conditionMap = buildConditionMap(anCustNo, anId, CustomerConstants.CHANGE_ITEM_BASE);
        List<CustInsteadRecord> changeApplys = insteadRecordService.selectByProperty(conditionMap);
        return Collections3.getFirst(changeApplys);
    }

    /**
     * 发起代录申请
     * 
     * @param anCustInsteadApply
     * @return
     */
    public CustInsteadApply addInsteadApply(CustInsteadApply anCustInsteadApply, String insteadItems) {
        BTAssert.notNull(anCustInsteadApply, "代录信息不允许为空！");

        anCustInsteadApply.initAddValue();
        insteadApplyService.insert(anCustInsteadApply);

        Long applyId = anCustInsteadApply.getId();
        String insteadType = anCustInsteadApply.getInsteadType();

        // 0 开户代录 1 变更代录
        if (BetterStringUtils.equals(insteadType, "0")) {
            insteadRecordService.addCustInsteadRecord(applyId, "7");
        }
        else {
            BTAssert.notNull(insteadItems, "代录项目数据不能为空");
            final String[] tempInsteadItems = BetterStringUtils.split(insteadItems, ",");
            if (tempInsteadItems.length == 7) {
                int insteadItemIndex = 0;
                // 代录项目: 0公司基本信息，1法人信息，2股东信息，3高管信息，4营业执照，5联系人信息，6银行账户, 7开户代录
                for (String insteadItem : tempInsteadItems) {
                    if (BetterStringUtils.equals(insteadItem, "1")) {
                        // 代录项目: 0公司基本信息，1法人信息，2股东信息，3高管信息，4营业执照，5联系人信息，6银行账户, 7开户代录
                        insteadRecordService.addCustInsteadRecord(applyId, String.valueOf(insteadItemIndex));
                    }
                    insteadItemIndex++;
                }
            }
            else {
                throw new BytterTradeException(20005, "代录项目数据不规范");
            }
        }

        return anCustInsteadApply;
    }

    /**
     * 
     * @param anApplyId
     * @param anId
     * @return
     */
    public CustInsteadRecord saveConfirmInstaedRecord(Long anApplyId, Long anId) {
        BTAssert.notNull(anApplyId, "代录申请编号不允许为空！");
        BTAssert.notNull(anId, "代录记录编号不允许为空！");
        return null;
    }

    /**
     * 
     * @param anApplyId
     * @param anId
     * @return
     */
    public CustInsteadRecord saveCancelInsteadRecord(Long anApplyId, Long anId) {
        BTAssert.notNull(anApplyId, "代录申请编号不允许为空！");
        BTAssert.notNull(anId, "代录记录编号不允许为空！");

        return null;
    }

    /**
     * 
     * @param anCustNo
     * @param anId
     * @return
     */
    public CustInsteadApply saveConfirmInsteadApply(Long anCustNo, Long anId) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        BTAssert.notNull(anId, "代录申请编号不允许为空！");

        final CustInsteadApply custInsteadApply = insteadApplyService.selectByPrimaryKey(anId);
        BTAssert.notNull(custInsteadApply, "没有找到相应的代录申请！");

        // 检查审批状态

        // 根据类型处理数据

        // 修改代录申请状态
        return null;
    }

    /**
     * 
     * @param anCustNo
     * @param anId
     * @return
     */
    public CustInsteadApply saveCancelInsteadApply(Long anCustNo, Long anId) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        BTAssert.notNull(anId, "代录申请编号不允许为空！");

        return null;
    }

    /**
     * 组建查询条件
     * 
     * @param anCustNo
     * @param anChangeItem
     * @return
     */
    private Map<String, Object> buildConditionMap(Long anCustNo, String anChangeItem) {
        return buildConditionMap(anCustNo, null, anChangeItem);
    }

    /**
     * 组建查询条件
     * 
     * @param anCustNo
     * @param anId
     * @param anChangeItem
     * @return
     */
    private Map<String, Object> buildConditionMap(Long anCustNo, Long anId, String anChangeItem) {
        final Map<String, Object> conditionMap = new HashMap<>();
        if (anId != null) {
            conditionMap.put("id", anId);
        }
        conditionMap.put("changeItem", anChangeItem);
        conditionMap.put("custNo", anCustNo);
        return conditionMap;
    }
}
