package com.betterjr.modules.customer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
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
     * @param anBankAccount
     * @return
     */
    public CustMechBankAccount addCustMechBankAccount(CustMechBankAccount anBankAccount, Long anCustNo) {
        BTAssert.notNull(anBankAccount, "银行账户信息不允许为空！");
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");

        final CustInfo custInfo = accountService.selectByPrimaryKey(anCustNo);
        anBankAccount.initAddValue(anCustNo, custInfo.getCustName(), custInfo.getRegOperId(), custInfo.getRegOperName(),
                custInfo.getOperOrg());
        this.insert(anBankAccount);
        return anBankAccount;
    }

    /**
     * 保存银行账户信息
     */
    public CustMechBankAccount saveCustMechBankAccount(CustMechBankAccount anBankAccount, Long anId) {
        BTAssert.notNull(anId, "银行账户编号不允许为空！");
        BTAssert.notNull(anBankAccount, "银行账户信息不允许为空！");

        final CustMechBankAccount tempBankAccount = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempBankAccount, "对应的银行账户信息没有找到！");

        tempBankAccount.initModifyValue(anBankAccount);
        this.updateByPrimaryKeySelective(tempBankAccount);
        return tempBankAccount;
    }

    /**
     * 通过银行账户流水修改银行账户
     */
    public CustMechBankAccount saveCustMechBankAccount(CustMechBankAccountTmp anBankAccountTmp) {
        BTAssert.notNull(anBankAccountTmp, "银行账户流水编号不允许为空！");

        final CustMechBankAccount tempBankAccount = this.selectByPrimaryKey(anBankAccountTmp.getRefId());
        BTAssert.notNull(tempBankAccount, "对应的银行账户信息没有找到！");

        tempBankAccount.initModifyValue(anBankAccountTmp);
        this.updateByPrimaryKeySelective(tempBankAccount);
        return tempBankAccount;
    }

    /**
     * @param anCustNo
     * @return
     */
    public CustMechBankAccount findDefaultCustMechBankAccount(Long anCustNo) {
        BTAssert.notNull(anCustNo, "公司编号不允许为空！");

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("custNo", anCustNo);
        conditionMap.put("isDefault", true);
        return Collections3.getFirst(this.selectByProperty(conditionMap));
    }


}