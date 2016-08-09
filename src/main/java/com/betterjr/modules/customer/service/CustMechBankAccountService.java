package com.betterjr.modules.customer.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.dao.CustMechBankAccountMapper;
import com.betterjr.modules.customer.entity.CustMechBankAccount;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechBankAccountService extends BaseService<CustMechBankAccountMapper, CustMechBankAccount> {
    @Resource
    private CustAccountService accountService;
    @Resource
    private CustMechBankAccountTmpService bankAccountTmpService;

    /**
     * 查询银行账户信息
     * 
     * @param anCustNo
     * @return
     */
    public CustMechBankAccount findCustMechBankAccountByCustNo(Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");

        final List<CustMechBankAccount> BankAccounts = this.selectByProperty(CustomerConstants.CUST_NO, anCustNo);
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
    public CustMechBankAccount addCustMechBankAccount(CustMechBankAccount anCustMechBankAccount, Long anCustNo) {
        BTAssert.notNull(anCustMechBankAccount, "银行账户信息不允许为空！");
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");

        final CustInfo custInfo = accountService.selectByPrimaryKey(anCustNo);
        anCustMechBankAccount.initAddValue(anCustNo, custInfo.getCustName(), custInfo.getRegOperId(), custInfo.getRegOperName(),
                custInfo.getOperOrg());
        this.insert(anCustMechBankAccount);
        return anCustMechBankAccount;
    }
}