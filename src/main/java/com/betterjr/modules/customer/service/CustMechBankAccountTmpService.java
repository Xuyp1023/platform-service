package com.betterjr.modules.customer.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.dao.CustMechBankAccountTmpMapper;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustMechBankAccountTmp;
import com.betterjr.modules.customer.helper.IFormalDataService;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechBankAccountTmpService extends BaseService<CustMechBankAccountTmpMapper, CustMechBankAccountTmp> implements IFormalDataService {
    @Resource
    private CustMechBankAccountService bankAccountService;

    @Resource
    private CustInsteadRecordService insteadRecordService;

    @Resource
    private CustChangeApplyService changeApplyService;

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

        // anCustMechBankAccountTmp.initAddValue();
        return this.insert(anCustMechBankAccountTmp);
    }

    /**
     * 法人信息变更申请
     * 
     * @param anCustMechLawTmp
     * @return
     */
    public CustChangeApply addCustChangeApply(CustMechBankAccountTmp anCustMechBankAccountTmp) {
        BTAssert.notNull(anCustMechBankAccountTmp, "基本信息变更申请不能为空");

        anCustMechBankAccountTmp.initAddValue(CustomerConstants.TMP_TYPE_CHANGE);
        this.insert(anCustMechBankAccountTmp);

        // 发起变更申请
        CustChangeApply custChangeApply = changeApplyService.addChangeApply(anCustMechBankAccountTmp.getRefId(),
                CustomerConstants.ITEM_BANKACCOUNT, String.valueOf(anCustMechBankAccountTmp.getId()));

        return custChangeApply;
    }

    @Override
    public void saveFormalData(String... anTmpIds) {
        // TODO Auto-generated method stub

    }
}