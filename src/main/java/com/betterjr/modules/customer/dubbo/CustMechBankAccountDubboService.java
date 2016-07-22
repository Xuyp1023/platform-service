package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.modules.customer.ICustMechBankAccountService;
import com.betterjr.modules.customer.service.CustChangeService;
import com.betterjr.modules.customer.service.CustInsteadService;

/**
 * 银行帐户
 * 
 * @author liuwl
 *
 */
@Service(interfaceClass = ICustMechBankAccountService.class)
public class CustMechBankAccountDubboService implements ICustMechBankAccountService {
    @Resource
    private CustChangeService changeService;

    @Resource
    private CustInsteadService insteadService;

    @Override
    public String webQueryBankAccount(Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webFindBankAccount(Long anId, Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webCancelInsteadRecord(Long anInsteadId, Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webConfirmInsteadRecord(Long anInsteadId, Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webFindInsteadRecord(Long anCustNo, Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webQueryInsteadRecord(Long anInsteadId, Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webAddInsteadRecord(Map<String, Object> anParam, Long anCustNo, Long anId, Long anOperType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webCancelChangeApply(Long anChangeId, Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webConfirmChangeApply(Long anChangeId, Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webQueryChangeApply(Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webFindChangeApply(Long anCustNo, Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webAddChangeApply(Map<String, Object> anParam, Long anCustNo, Long anId, Long anOperType) {
        // TODO Auto-generated method stub
        return null;
    }

}
