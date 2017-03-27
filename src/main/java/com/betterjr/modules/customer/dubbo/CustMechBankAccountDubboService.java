package com.betterjr.modules.customer.dubbo;

import static com.betterjr.common.web.AjaxObject.newOk;
import static com.betterjr.common.web.AjaxObject.newOkWithPage;

import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.customer.ICustMechBankAccountService;
import com.betterjr.modules.customer.constants.CustomerConstants;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustMechBankAccount;
import com.betterjr.modules.customer.entity.CustMechBankAccountTmp;
import com.betterjr.modules.customer.helper.ChangeDetailBean;
import com.betterjr.modules.customer.service.CustChangeService;
import com.betterjr.modules.customer.service.CustInsteadService;
import com.betterjr.modules.customer.service.CustMechBankAccountService;
import com.betterjr.modules.customer.service.CustMechBankAccountTmpService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

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

    @Resource
    private CustMechBankAccountService bankAccountService;

    @Resource
    private CustMechBankAccountTmpService bankAccountTmpService;

    @Override
    public String webQueryBankAccount(final Long anCustNo) {
        return newOk("查询公司银行账户列表成功", bankAccountService.queryCustMechBankAccount(anCustNo)).toJson();
    }

    @Override
    public CustMechBankAccount findDefaultBankAccount(final Long anCustNo) {
        final CustMechBankAccount bankAccount = bankAccountService.findDefaultCustMechBankAccount(anCustNo);
        return bankAccount;
    }

    @Override
    public String webFindBankAccount(final Long anId) {
        final CustMechBankAccount bankAccount = bankAccountService.findCustMechBankAccount(anId);
        BTAssert.notNull(bankAccount, "没有找到银行账户信息!");
        return newOk("查询公司银行账户详情成功!", bankAccount).toJson();
    }

    @Override
    public String webFindBankAccountTmp(final Long anId) {
        return newOk("查询公司银行账户列表成功", bankAccountTmpService.findBankAccountTmp(anId)).toJson();
    }

    @Override
    public String webSaveBankAccountTmp(final Map<String, Object> anParam, final Long anId, final String anFileList) {
        final CustMechBankAccountTmp bankAccountTmp = RuleServiceDubboFilterInvoker.getInputObj();
        return newOk("公司银行账户-流水信息 修改成功", bankAccountTmpService.saveBankAccountTmp(bankAccountTmp, anId, anFileList)).toJson();
    }

    @Override
    public String webAddChangeBankAccountTmp(final Map<String, Object> anMap, final String anFileList) {
        final CustMechBankAccountTmp custMechBankAccountTmp = RuleServiceDubboFilterInvoker.getInputObj();
        return newOk("公司银行账户-流水信息 变更添加成功", bankAccountTmpService.addChangeBankAccountTmp(custMechBankAccountTmp, anFileList)).toJson();
    }

    @Override
    public String webSaveChangeBankAccountTmp(final Map<String, Object> anParam, final String anFileList) {
        final CustMechBankAccountTmp custMechBankAccountTmp = RuleServiceDubboFilterInvoker.getInputObj();
        return newOk("公司银行账户-流水信息 变更修改成功", bankAccountTmpService.saveSaveChangeBankAccountTmp(custMechBankAccountTmp, anFileList)).toJson();
    }

    @Override
    public String webDeleteChangeBankAccountTmp(final Long anRefId) {
        return newOk("公司银行账户-流水信息 变更删除成功", bankAccountTmpService.saveDeleteChangeBankAccountTmp(anRefId)).toJson();
    }

    @Override
    public String webCancelChangeBankAccountTmp(final Long anId) {
        return newOk("公司银行账户-流水信息 变更撤销成功", bankAccountTmpService.saveCancelChangeBankAccountTmp(anId)).toJson();
    }

    @Override
    public String webQueryNewChangeBankAccountTmp(final Long anCustNo) {
        return newOk("公司银行账户-流水信息 列表查询成功", bankAccountTmpService.queryNewChangeBankAccountTmp(anCustNo)).toJson();
    }

    @Override
    public String webQueryChangeBankAccountTmp(final Long anApplyId) {
        return newOk("公司银行账户-流水信息 列表查询成功", bankAccountTmpService.queryChangeBankAccountTmp(anApplyId)).toJson();
    }


    @Override
    public String webAddChangeApply(final Map<String, Object> anParam, final Long anCustNo) {
        return newOk("公司银行账户-变更申请 成功", bankAccountTmpService.addChangeApply(anParam, anCustNo)).toJson();
    }

    @Override
    public String webSaveChangeApply(final Map<String, Object> anParam, final Long anApplyId) {
        return newOk("公司银行账户-变更申请 成功", bankAccountTmpService.saveChangeApply(anParam, anApplyId)).toJson();
    }

    @Override
    public String webQueryChangeApply(final Long anCustNo, final int anFlag, final int anPageNum, final int anPageSize) {
        final Page<CustChangeApply> changeApplys = changeService.queryChangeApply(anCustNo, CustomerConstants.ITEM_BANKACCOUNT, anFlag, anPageNum,
                anPageSize);
        return newOkWithPage("银行账户信息-变更列表查询 成功", changeApplys).toJson();
    }

    @Override
    public String webFindChangeApply(final Long anApplyId, final Long anTmpId) {
        final CustChangeApply changeApply = changeService.findChangeApply(anApplyId, CustomerConstants.ITEM_BANKACCOUNT);

        final CustMechBankAccountTmp nowData = bankAccountTmpService.findBankAccountTmp(anTmpId);
        final CustMechBankAccountTmp befData = bankAccountTmpService.findBankAccountTmpPrevVersion(nowData);

        final ChangeDetailBean<CustMechBankAccountTmp> changeDetailBean = new ChangeDetailBean<>();
        changeDetailBean.setChangeApply(changeApply);
        if (BetterStringUtils.equals(nowData.getTmpOperType(), CustomerConstants.TMP_OPER_TYPE_DELETE) == false) {
            changeDetailBean.setNowData(nowData);
        }
        changeDetailBean.setBefData(befData);

        return newOk("公司银行账户-变更详情查询 成功", changeDetailBean).toJson();
    }

    @Override
    public String webAddInsteadBankAccountTmp(final Map<String, Object> anMap, final Long anInsteadRecordId, final String anFileList) {
        final CustMechBankAccountTmp bankAccountTmp = RuleServiceDubboFilterInvoker.getInputObj();
        return newOk("公司银行账户-流水信息 代录添加成功", bankAccountTmpService.addInsteadBankAccountTmp(bankAccountTmp, anInsteadRecordId, anFileList)).toJson();
    }

    @Override
    public String webSaveInsteadBankAccountTmp(final Map<String, Object> anParam, final Long anInsteadRecordId, final String anFileList) {
        final CustMechBankAccountTmp bankAccountTmp = RuleServiceDubboFilterInvoker.getInputObj();
        return newOk("公司银行账户-流水信息 代录修改成功", bankAccountTmpService.saveSaveInsteadBankAccountTmp(bankAccountTmp, anInsteadRecordId, anFileList)).toJson();

    }

    @Override
    public String webDeleteInsteadBankAccountTmp(final Long anRefId, final Long anInsteadRecordId) {
        return newOk("公司银行账户-流水信息 代录删除成功", bankAccountTmpService.saveDeleteInsteadBankAccountTmp(anRefId, anInsteadRecordId)).toJson();
    }

    @Override
    public String webCancelInsteadBankAccountTmp(final Long anId, final Long anInsteadRecordId) {
        return newOk("公司银行账户-流水信息 代录删除成功", bankAccountTmpService.saveCancelInsteadBankAccountTmp(anId, anInsteadRecordId)).toJson();
    }

    @Override
    public String webQueryInsteadBankAccountTmp(final Long anInsteadRecordId) {
        return newOk("公司银行账户-流水信息 列表查询成功", bankAccountTmpService.queryInsteadBankAccountTmp(anInsteadRecordId))
                .toJson();
    }

    @Override
    public String webAddInsteadRecord(final Map<String, Object> anParam, final Long anInsteadRecordId) {
        return newOk("公司银行账户-添加代录 成功", bankAccountTmpService.addInsteadRecord(anParam, anInsteadRecordId)).toJson();
    }

    @Override
    public String webFindInsteadRecord(final Long anId) {
        return null;
    }

    @Override
    public String webSaveInsteadRecord(final Map<String, Object> anParam, final Long anInsteadRecordId) {
        return newOk("公司银行账户-修改代录 成功", bankAccountTmpService.saveInsteadRecord(anParam, anInsteadRecordId)).toJson();
    }

    @Override
    public CustMechBankAccount findCustMechBankAccount(final String anBankAcco, final String anBankAccoName) {
        return bankAccountService.findCustMechBankAccount(anBankAcco, anBankAccoName);
    }
}
