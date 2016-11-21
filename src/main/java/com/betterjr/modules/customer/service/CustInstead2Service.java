package com.betterjr.modules.customer.service;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.modules.customer.constants.CustomerConstants;
import com.betterjr.modules.customer.entity.CustInsteadApply;

/**
 * 代录服务
 */
@Service
public class CustInstead2Service {
    
    @Resource
    private CustInsteadRecordService insteadRecordService;
    @Resource
    private CustInsteadApplyService insteadApplyService;
    
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
}
