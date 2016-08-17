package com.betterjr.modules.customer.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.customer.dao.CustMechBankAccountMapper;
import com.betterjr.modules.customer.entity.CustMechBankAccount;
import com.betterjr.modules.customer.entity.CustMechBankAccountTmp;

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
     * 查询银行账户列表
     * @param anCustNo
     * @return
     */
    public List<CustMechBankAccount> queryCustMechBankAccount(Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        
        return this.selectByProperty("custNo", anCustNo);
    }
    
    /**
     * 查询银行账户信息
     */
    public CustMechBankAccount findCustMechBankAccount(Long anId) {
        BTAssert.notNull(anId, "银行账户编号不允许为空！");
        
        CustMechBankAccount bankAccount = this.selectByPrimaryKey(anId);
        return bankAccount;
    }
    
    /**
     * 添加银行账户信息
     */
    public CustMechBankAccount addCustMechBankAccount(CustMechBankAccountTmp anBankAccountTmp) {
        BTAssert.notNull(anBankAccountTmp, "银行账户流水信息不允许为空！");
        
        CustMechBankAccount bankAccount = new CustMechBankAccount();
        bankAccount.initAddValue(anBankAccountTmp);
        
        this.insert(bankAccount);
        return bankAccount;
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
    
    /**
     * 保存银行账户信息
     */
    public CustMechBankAccount saveCustMechBankAccount(CustMechBankAccount anCustMechBankAccount, Long anId) {
        BTAssert.notNull(anId, "银行账户编号不允许为空！");
        BTAssert.notNull(anCustMechBankAccount, "银行账户信息不允许为空！");
        
        final CustMechBankAccount tempCustMechBankAccount = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustMechBankAccount, "对应的银行账户信息没有找到！");
        
        tempCustMechBankAccount.initModifyValue(anCustMechBankAccount);
        this.updateByPrimaryKeySelective(tempCustMechBankAccount);
        return tempCustMechBankAccount;
    }

    /**
     * 通过银行账户流水修改银行账户 
     */
    public CustMechBankAccount saveCustMechBankAccount(CustMechBankAccountTmp anBankAccountTmp) {
        BTAssert.notNull(anBankAccountTmp, "银行账户流水编号不允许为空！");
        
        final CustMechBankAccount tempCustMechBankAccount = this.selectByPrimaryKey(anBankAccountTmp.getRefId());
        BTAssert.notNull(tempCustMechBankAccount, "对应的银行账户信息没有找到！");
        
        tempCustMechBankAccount.initModifyValue(anBankAccountTmp);
        this.updateByPrimaryKeySelective(tempCustMechBankAccount);
        return tempCustMechBankAccount;
    }


}