package com.betterjr.modules.customer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.modules.customer.dao.CustMechBankAccountTmpMapper;
import com.betterjr.modules.customer.entity.CustMechBankAccountTmp;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechBankAccountTmpService extends BaseService<CustMechBankAccountTmpMapper,CustMechBankAccountTmp> {
    private static Logger logger = LoggerFactory.getLogger(CustMechBankAccountTmpService.class);

    /**
     * 查询银行账户流水信息
     * 
     * @param anCustNo
     * @return
     */
    public CustMechBankAccountTmp findCustMechBankAccountTmpByCustNo(Long anId) {
        BTAssert.notNull(anId, "银行账户流水信息编号不允许为空！");
        
        return this.selectByPrimaryKey(anId);
    }

    /**
     * 保存银行账户流水信息
     * 
     * @param anCustMechBankAccountTmp
     * @return
     */
    public int saveCustMechBankAccountTmp(CustMechBankAccountTmp anCustMechBankAccountTmp, Long anId) {
        BTAssert.notNull(anId, "银行账户流水编号不允许为空！");
        
        final CustMechBankAccountTmp tempCustMechBankAccountTmp = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustMechBankAccountTmp, "没有找到对应的银行账户流水信息！");
        
        tempCustMechBankAccountTmp.initModifyValue(anCustMechBankAccountTmp);
        return this.updateByPrimaryKey(tempCustMechBankAccountTmp);
    }

    /**
     * 添加银行账户流水信息
     * 
     * @param anCustMechBankAccountTmp
     * @return
     */
    public int addCustMechBankAccountTmp(CustMechBankAccountTmp anCustMechBankAccountTmp) {
        BTAssert.notNull(anCustMechBankAccountTmp, "银行账户流水信息编号不允许为空！");
        
        anCustMechBankAccountTmp.initAddValue();
        return this.insert(anCustMechBankAccountTmp);
    }
}