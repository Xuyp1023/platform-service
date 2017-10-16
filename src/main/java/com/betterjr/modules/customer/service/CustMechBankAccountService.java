package com.betterjr.modules.customer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.data.SimpleDataEntity;
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
    public List<CustMechBankAccount> queryCustMechBankAccount(final Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");

        return this.selectByProperty("custNo", anCustNo);
    }

    /**
     * 查询银行跟账号用于下拉
     * @param anCustNo
     * @return
     */
    public List<SimpleDataEntity> querBankAccountKeyAndValue(final Long anCustNo) {
        List<CustMechBankAccount> accountList = this.queryCustMechBankAccount(anCustNo);
        List<SimpleDataEntity> result = new ArrayList<SimpleDataEntity>();
        for (CustMechBankAccount account : accountList) {
            result.add(new SimpleDataEntity(account.getBankAcco() + "-" + account.getBankName(),
                    String.valueOf(account.getBankAcco())));
        }
        return result;
    }

    /**
     * 查询银行账户信息
     */
    public CustMechBankAccount findCustMechBankAccount(final Long anId) {
        BTAssert.notNull(anId, "银行账户编号不允许为空！");

        final CustMechBankAccount bankAccount = this.selectByPrimaryKey(anId);
        return bankAccount;
    }

    /**
     * 添加银行账户信息
     */
    public CustMechBankAccount addCustMechBankAccount(final CustMechBankAccountTmp anBankAccountTmp) {
        BTAssert.notNull(anBankAccountTmp, "银行账户流水信息不允许为空！");

        final CustMechBankAccount bankAccount = new CustMechBankAccount();
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
    public CustMechBankAccount addCustMechBankAccount(final CustMechBankAccount anBankAccount, final Long anCustNo) {
        BTAssert.notNull(anBankAccount, "银行账户信息不允许为空！");
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");

        final CustInfo custInfo = accountService.selectByPrimaryKey(anCustNo);
        anBankAccount.initAddValue(anCustNo, custInfo.getCustName(), custInfo.getRegOperId(), custInfo.getRegOperName(),
                custInfo.getOperOrg());
        this.insert(anBankAccount);

        bankAccountTmpService.addCustMechBankAccountTmp(anBankAccount);
        return anBankAccount;
    }

    /**
     * 保存银行账户信息
     */
    public CustMechBankAccount saveCustMechBankAccount(final CustMechBankAccount anBankAccount, final Long anId) {
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
    public CustMechBankAccount saveCustMechBankAccount(final CustMechBankAccountTmp anBankAccountTmp) {
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
    public CustMechBankAccount findDefaultCustMechBankAccount(final Long anCustNo) {
        BTAssert.notNull(anCustNo, "公司编号不允许为空！");

        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("custNo", anCustNo);
        conditionMap.put("isDefault", true);
        return Collections3.getFirst(this.selectByProperty(conditionMap));
    }

    /**
     * @param anTmpAcco
     * @param anTmpAccoName
     * @return
     */
    public CustMechBankAccount findCustMechBankAccount(final String anBankAcco, final String anBankAccoName) {
        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("bankAcco", anBankAcco);
        conditionMap.put("bankAccoName", anBankAccoName);
        return Collections3.getFirst(this.selectByProperty(conditionMap));
    }

    /**
     * @param anTmpAcco
     * @return
     */
    public CustMechBankAccount findBankAccountByAcco(final String anBankAcco) {
        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("bankAcco", anBankAcco);
        return Collections3.getFirst(this.selectByProperty(conditionMap));
    }

}