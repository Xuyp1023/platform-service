package com.betterjr.modules.customer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustMechBaseTmp;

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
     * 添加客户基本信息变更服务
     * 
     * @param anCustMechBaseTmp
     */
    public CustChangeApply addCustMechBaseChangeApply(CustMechBaseTmp anCustMechBaseTmp) {
        CustMechBaseTmp custMechBaseTmp = baseTmpService.addCustMechBaseTmp(anCustMechBaseTmp, "1");

        CustChangeApply custChangeApply = changeApplyService.addCustChangeApply(custMechBaseTmp.getRefId(),
                CustomerConstants.CHANGE_ITEM_BASE, String.valueOf(custMechBaseTmp.getId()));
        
        return custChangeApply;
    }

    /**
     * 查询变更申请列表
     * @param anCustNo
     * @return
     */
    public Page<CustChangeApply> queryChangeApply(Long anCustNo, String anChangeItem, int anFlag, int anPageNum, int anPageSize) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        
        final Map<String, Object> conditionMap = buildConditionMap(anCustNo, anChangeItem);
        return changeApplyService.selectPropertyByPage(conditionMap, anPageNum, anPageSize, anFlag == 1);
    }
    
    /**
     * 查询变更申请详情
     * @param anCustNo
     * @param anId
     * @return
     */
    public CustChangeApply findChangeApply(Long anCustNo, Long anId, String anChangeItem) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        BTAssert.notNull(anId, "申请编号不允许为空！");
        
        final Map<String, Object> conditionMap = buildConditionMap(anCustNo, anId, anChangeItem);
        List<CustChangeApply> changeApplys = changeApplyService.selectByProperty(conditionMap);
        
        if (Collections3.isEmpty(changeApplys) == true) {
            throw new BytterTradeException(20008, "变更申请详情没有找到！");
        }
        return Collections3.getFirst(changeApplys);
    }
    
    /**
     * 确认变更申请
     * @param anCustNo
     * @param anId
     * @return
     */
    public CustChangeApply saveConfirmChangeApply(Long anCustNo, Long anId) {
        return null;
    }
    
    /**
     * 作废变更申请
     * @param anCustNo
     * @param anId
     * @return
     */
    public CustChangeApply saveCancelChangeApply(Long anCustNo, Long anId) {
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
        conditionMap.put("custNo", anCustNo);
        conditionMap.put("changeItem", anChangeItem);
        return conditionMap;
    }
}
