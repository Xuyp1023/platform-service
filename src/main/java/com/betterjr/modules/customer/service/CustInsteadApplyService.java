package com.betterjr.modules.customer.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.dao.CustInsteadApplyMapper;
import com.betterjr.modules.customer.entity.CustInsteadApply;

/**
 * 代录申请
 * 
 * @author liuwl
 *
 */
@Service
public class CustInsteadApplyService extends BaseService<CustInsteadApplyMapper, CustInsteadApply> {
    @Resource
    private CustAccountService accountService;

    /**
     * 添加代录申请 检查是否已经有申请在进行中
     * 
     * @param anCustInsteadApply
     * @return
     */
    public CustInsteadApply addCustInsteadApply(String anInsteadType, Long anCustNo) {
        if (BetterStringUtils.isBlank(anInsteadType) == true) {
            throw new BytterTradeException(20061, "代录申请类型不允许为空！");
        }

        final CustInsteadApply custInsteadApply = new CustInsteadApply();
        if (anInsteadType.equals(CustomerConstants.INSTEAD_APPLY_TYPE_OPENACCOUNT) == true) {
            custInsteadApply.initAddValue(anInsteadType, null, null);
        }
        else {// 变更代录 需要 custNo 和 custName
            BTAssert.notNull(anCustNo, "客户编号不能为空！");

            if (checkExistActiveInsteadApply(anCustNo) == true) {
                throw new BytterTradeException(20062, "所选客户有正在进行的代录申请！");
            }

            String custName = accountService.queryCustName(anCustNo);
            custInsteadApply.initAddValue(anInsteadType, anCustNo, custName);
        }

        this.insert(custInsteadApply);
        return custInsteadApply;
    }

    /**
     * 
     * @param anCustNo
     * @return
     */
    public Boolean checkExistActiveInsteadApply(Long anCustNo) {
        Map<String, Object> conditionMap = new HashMap<>();
        
        conditionMap.put(CustomerConstants.CUST_NO, anCustNo);
        //INSTEAD_APPLY_STATUS_CONFIRM_PASS  这两种状态表明 此申请已经完成 或者 取消
        //INSTEAD_APPLY_STATUS_CANCEL
        String[] businStatues = {
                CustomerConstants.INSTEAD_APPLY_STATUS_NEW,
                CustomerConstants.INSTEAD_APPLY_STATUS_AUDIT_PASS,
                CustomerConstants.INSTEAD_APPLY_STATUS_AUDIT_REJECT,
                CustomerConstants.INSTEAD_APPLY_STATUS_TYPE_IN,
                CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_PASS,
                CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_REJECT,
                CustomerConstants.INSTEAD_APPLY_STATUS_CONFIRM_REJECT
        };
        conditionMap.put("businStatus", businStatues);
        return Collections3.isEmpty(this.selectByProperty(conditionMap)) == false;
    }

    /**
     * 查询代录申请
     * 
     * @param anCustNo
     * @param anId
     * @return
     */
    public CustInsteadApply findCustInsteadApply(Long anCustNo, Long anId) {
        return null;
    }

    /**
     * 保存代录申请
     * 
     * @param anCustInsteadApply
     * @return
     */
    public CustInsteadApply saveCustInsteadApply(Long anId, String anBusinStatus) {
        BTAssert.notNull(anId, "编号不允许为空！");
        BTAssert.notNull(anBusinStatus, "状态不允许为空！");

        final CustInsteadApply tempCustInsteadApply = this.selectByPrimaryKey(anId);
        tempCustInsteadApply.initModifyValue(anBusinStatus);
        this.updateByPrimaryKeySelective(tempCustInsteadApply);
        return tempCustInsteadApply;
    }

    /**
     * 查询代表列表 平台使用
     * 
     * @return
     */
    public Page<CustInsteadApply> queryCustInsteadApply(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        return this.selectPropertyByPage(CustInsteadApply.class, anParam, anPageNum, anPageSize, anFlag == 1);
    }
}