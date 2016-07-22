package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.modules.customer.ICustBankFlowService;

/**
 * 银行流水
 * @author liuwl
 *
 */
@Service(interfaceClass = ICustBankFlowService.class)
public class CustBankFlowDubboService implements ICustBankFlowService {

    @Override
    public String webAddBankFlowRecord(Map<String, Object> anParam, Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webFindBankFlowRecord(Long anCustNo, Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webQueryBankFlowRecordList(Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webSaveBankFlowRecord(Map<String, Object> anParam, Long anCustNo, Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

}
