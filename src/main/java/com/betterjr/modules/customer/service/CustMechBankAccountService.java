package com.betterjr.modules.customer.service;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.customer.dao.CustMechBankAccountMapper;
import com.betterjr.modules.customer.entity.CustMechBankAccount;
import com.betterjr.modules.customer.entity.CustMechBankAccount;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechBankAccountService extends BaseService<CustMechBankAccountMapper,CustMechBankAccount> {
    private static final String CUST_NO = "custNo";
    
    private static Logger logger = LoggerFactory.getLogger(CustMechBankAccountService.class);

    /**
     * 查询银行账户信息
     * 
     * @param anCustNo
     * @return
     */
    public CustMechBankAccount findCustMechBankAccountByCustNo(Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        
        final List<CustMechBankAccount> BankAccounts = this.selectByProperty(CUST_NO, anCustNo);
        return Collections3.getFirst(BankAccounts);
    }

    /**
     * 修改银行账户信息
     * 
     * @param anCustMechBankAccount
     * @return
     */
    public CustMechBankAccount saveCustMechBankAccount(CustMechBankAccount anCustMechBankAccount, Long anId) {
        BTAssert.notNull(anId, "银行账户编号不允许为空");
        final CustMechBankAccount tempCustMechBankAccount = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustMechBankAccount, "对应的银行账户信息没有找到！");
        
        tempCustMechBankAccount.initModifyValue(anCustMechBankAccount);
        this.updateByPrimaryKeySelective(tempCustMechBankAccount);
        return tempCustMechBankAccount;
    }
    
    /**
     * 添加银行账户信息
     * 
     * @param anCustMechBankAccount
     * @return
     */
    public CustMechBankAccount addCustMechBankAccount(CustMechBankAccount anCustMechBankAccount) {
        BTAssert.notNull(anCustMechBankAccount, "银行账户信息不允许为空！");
        
        anCustMechBankAccount.initAddValue();
        this.insert(anCustMechBankAccount);
        return anCustMechBankAccount;
    }
}