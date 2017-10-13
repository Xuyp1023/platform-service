package com.betterjr.modules.customer.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.modules.customer.constants.CustomerConstants;
import com.betterjr.modules.customer.dao.CustMechBankAccountTmpMapper;
import com.betterjr.modules.customer.data.ICustAuditEntityFace;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustInsteadRecord;
import com.betterjr.modules.customer.entity.CustMechBankAccount;
import com.betterjr.modules.customer.entity.CustMechBankAccountTmp;
import com.betterjr.modules.customer.helper.IFormalDataService;
import com.betterjr.modules.customer.helper.VersionHelper;
import com.betterjr.modules.document.service.CustFileItemService;

/**
 *
 * @author liuwl
 *
 */
@Service
public class CustMechBankAccountTmpService extends BaseService<CustMechBankAccountTmpMapper, CustMechBankAccountTmp> implements IFormalDataService {

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    @Resource
    private CustMechBankAccountService bankAccountService;

    @Resource
    private CustInsteadRecordService insteadRecordService;

    @Resource
    private CustChangeApplyService changeApplyService;

    @Resource
    private CustFileItemService fileItemService;

    /**
     * 查询公司银行账户流水信息
     */
    public CustMechBankAccountTmp findBankAccountTmp(final Long anId) {
        BTAssert.notNull(anId, "公司银行账户流水信息编号不允许为空！");

        final CustMechBankAccountTmp bankAccountTmp = this.selectByPrimaryKey(anId);

        return bankAccountTmp;
    }

    /**
     * 取上一版
     */
    public CustMechBankAccountTmp findBankAccountTmpPrevVersion(final CustMechBankAccountTmp anBankAccountTmp) {
        final Long custNo = anBankAccountTmp.getCustNo();
        final Long refId = anBankAccountTmp.getRefId();
        final Long version = anBankAccountTmp.getVersion();

        final Long befVersion = this.mapper.selectPrevVersion(custNo, version);

        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("version", befVersion);
        conditionMap.put("refId", refId);
        conditionMap.put("custNo", custNo);

        return Collections3.getFirst(this.selectByProperty(conditionMap));
    }

    /**
     * 添加新增变更流水记录
     */
    public CustMechBankAccountTmp addChangeBankAccountTmp(final CustMechBankAccountTmp anBankAccountTmp, final String anFileList) {
        BTAssert.notNull(anBankAccountTmp, "公司银行账户流水信息不允许为空！");

        final Long refId = anBankAccountTmp.getRefId();
        BTAssert.isNull(refId, "引用编号不能有值!");

        anBankAccountTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW, CustomerConstants.TMP_TYPE_CHANGE, null);
        anBankAccountTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_ADD);
        anBankAccountTmp.setBatchNo(fileItemService.updateCustFileItemInfo(anFileList, anBankAccountTmp.getBatchNo()));

        return addBankAccountTmp(anBankAccountTmp);
    }

    /**
     * 流水信息-添加 从银行账户信息建立流水
     */
    public CustMechBankAccountTmp addCustMechBankAccountTmp(final CustMechBankAccount anBankAccount) {
        BTAssert.notNull(anBankAccount, "公司银行账户信息 不能为空！");

        final CustMechBankAccountTmp bankAccountTmp = new CustMechBankAccountTmp();

        bankAccountTmp.initAddValue(anBankAccount, CustomerConstants.TMP_TYPE_INITDATA, CustomerConstants.TMP_STATUS_USED);
        bankAccountTmp.setVersion(VersionHelper.generateVersion(this.mapper, anBankAccount.getCustNo()));
        bankAccountTmp.setRefId(anBankAccount.getId());// 引用编号

        this.insert(bankAccountTmp);

        return bankAccountTmp;
    }

    /**
     * 添加修改变更记录
     */
    public CustMechBankAccountTmp saveSaveChangeBankAccountTmp(final CustMechBankAccountTmp anBankAccountTmp, final String anFileList) {
        BTAssert.notNull(anBankAccountTmp, "公司银行账户流水信息不允许为空！");

        final Long refId = anBankAccountTmp.getRefId();
        BTAssert.notNull(refId, "引用编号不允许为空!");

        final CustMechBankAccount bankAccount = bankAccountService.findCustMechBankAccount(refId);
        BTAssert.notNull(bankAccount, "没有找到引用的记录!");

        if (bankAccount.getCustNo().equals(anBankAccountTmp.getCustNo()) == false) {
            throw new BytterTradeException("客户编号不匹配!");
        }

        final CustMechBankAccountTmp tempBankAccountTmp = findBankAccountTmpByRefId(refId, CustomerConstants.TMP_TYPE_CHANGE);
        if (tempBankAccountTmp == null) {
            anBankAccountTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW, CustomerConstants.TMP_TYPE_CHANGE, null);
            anBankAccountTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            anBankAccountTmp.setBatchNo(
                    fileItemService.updateAndDuplicateConflictFileItemInfo(anFileList, anBankAccountTmp.getBatchNo(), UserUtils.getOperatorInfo()));
            return addBankAccountTmp(anBankAccountTmp);
        }
        else {
            tempBankAccountTmp.initModifyValue(anBankAccountTmp);
            tempBankAccountTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            tempBankAccountTmp.setBatchNo(
                    fileItemService.updateAndDuplicateConflictFileItemInfo(anFileList, tempBankAccountTmp.getBatchNo(), UserUtils.getOperatorInfo()));
            return saveBankAccountTmp(tempBankAccountTmp);
        }
    }

    /**
     * 添加删除变更记录
     */
    public CustMechBankAccountTmp saveDeleteChangeBankAccountTmp(final Long anRefId) {
        BTAssert.notNull(anRefId, "公司银行账户号不允许为空！");

        final CustMechBankAccount bankAccount = bankAccountService.findCustMechBankAccount(anRefId);
        BTAssert.notNull(bankAccount, "没有找到引用的记录!");

        CustMechBankAccountTmp bankAccountTmp = findBankAccountTmpByRefId(anRefId, CustomerConstants.TMP_TYPE_CHANGE);
        if (bankAccountTmp == null) {
            bankAccountTmp = new CustMechBankAccountTmp();
            bankAccountTmp.initAddValue(bankAccount, CustomerConstants.TMP_STATUS_NEW);
            bankAccountTmp.setRefId(anRefId);
            bankAccountTmp.setTmpType(CustomerConstants.TMP_TYPE_CHANGE);
            bankAccountTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return addBankAccountTmp(bankAccountTmp);
        }
        else {
            bankAccountTmp.initModifyValue(bankAccount, CustomerConstants.TMP_STATUS_NEW);
            bankAccountTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return saveBankAccountTmp(bankAccountTmp);
        }
    }

    /**
     * 撤销变更记录
     */
    public int saveCancelChangeBankAccountTmp(final Long anId) {
        final CustMechBankAccountTmp bankAccountTmp = this.findBankAccountTmp(anId);

        final Long tmpVersion = bankAccountTmp.getVersion();
        final Long maxVersion = VersionHelper.generateVersion(this.mapper, bankAccountTmp.getCustNo());

        if (tmpVersion.equals(maxVersion) == false) {
            throw new BytterTradeException("流水信息不正确,不可撤销.");
        }

        if (BetterStringUtils.equals(bankAccountTmp.getTmpType(), CustomerConstants.TMP_TYPE_CHANGE) == false) {
            throw new BytterTradeException("流水信息类型不正确,不可撤销.");
        }

        if (BetterStringUtils.equals(bankAccountTmp.getBusinStatus(), CustomerConstants.TMP_STATUS_NEW) == false) {
            throw new BytterTradeException("流水信息状态不正确,不可撤销.");
        }

        return this.deleteByPrimaryKey(anId);
    }

    /**
     * 加载变更列表中的流水列表
     */
    public Collection<CustMechBankAccountTmp> queryBankAccountTmpByChangeApply(final Long anApplyId) {
        BTAssert.notNull(anApplyId, "公司编号不允许为空！");
        final CustChangeApply changeApply = changeApplyService.findChangeApply(anApplyId);

        final long[] tmpIds = COMMA_PATTERN.splitAsStream(changeApply.getTmpIds()).mapToLong(Long::valueOf).toArray();

        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put(CustomerConstants.ID, tmpIds);

        return this.selectByProperty(conditionMap);
    }

    /**
     *
     */
    private CustMechBankAccountTmp findBankAccountTmpByRefId(final Long anRefId, final String anTmpType) {
        final Map<String, Object> conditionMap = new HashMap<>();

        conditionMap.put("refId", anRefId);
        conditionMap.put("tmpType", anTmpType);
        conditionMap.put("businStatus", CustomerConstants.TMP_STATUS_NEW);

        return Collections3.getFirst(this.selectByProperty(conditionMap));
    }

    /**
     * 添加一个变更申请
     */
    public CustChangeApply addChangeApply(final Map<String, Object> anParam, final Long anCustNo) {
        BTAssert.notNull(anCustNo, "公司编号不允许为空！");
        BTAssert.notNull(anParam, "参数不允许为空！");

        final String tempTmpIds = (String) anParam.get("tmpIds");

        final List<Long> tmpIds = COMMA_PATTERN.splitAsStream(tempTmpIds).map(Long::valueOf).collect(Collectors.toList());

        if (checkMatchNewChange(tmpIds, anCustNo) == false) {
            throw new BytterTradeException("代录编号列表不正确,请检查.");
        }
        final CustChangeApply changeApply = changeApplyService.addChangeApply(anCustNo, CustomerConstants.ITEM_BANKACCOUNT, tempTmpIds);

        for (final Long id : tmpIds) {
            saveBankAccountTmpParentIdAndStatus(id, changeApply.getId(), CustomerConstants.TMP_STATUS_USEING);
        }

        final Collection<CustMechBankAccount> bankAccounts = bankAccountService.queryCustMechBankAccount(anCustNo);
        final Collection<CustMechBankAccountTmp> bankAccountTmps = this.selectByProperty("parentId", changeApply.getId());

        saveNormalBankAccounts(bankAccounts, bankAccountTmps, changeApply, CustomerConstants.TMP_TYPE_CHANGE);

        checkDefaultBankAccount(changeApply.getId());

        return changeApply;
    }

    /**
     * @param anId
     */
    private void checkDefaultBankAccount(final Long anId) {
        final Collection<CustMechBankAccountTmp> bankAccountTmps = this.selectByProperty("parentId", anId);

        BTAssert.isTrue(Collections3.isEmpty(bankAccountTmps) == false, "至少需要一个默认银行账户");

        int defaultCount = 0;
        for (final CustMechBankAccountTmp bankAccountTmp : bankAccountTmps) {
            if (bankAccountTmp.getIsDefault()) {
                defaultCount++;
            }
        }

        BTAssert.isTrue(defaultCount == 1, "只允许有一个默认银行账户");
    }

    /**
     * 存储未修改的记录
     */
    private void saveNormalBankAccounts(final Collection<CustMechBankAccount> anBankAccounts,
            final Collection<CustMechBankAccountTmp> anBankAccountTmps, final CustChangeApply anChangeApply, final String anTmpType) {
        final Long parentId = anChangeApply.getId();
        for (final CustMechBankAccount bankAccount : anBankAccounts) {
            if (checkIsNormalBankAccount(bankAccount, anBankAccountTmps) == true) {
                final CustMechBankAccountTmp bankAccountTmp = new CustMechBankAccountTmp();
                bankAccountTmp.initAddValue(bankAccount, CustomerConstants.TMP_STATUS_USEING);
                bankAccountTmp.setRefId(bankAccount.getId());
                bankAccountTmp.setParentId(parentId);
                bankAccountTmp.setTmpType(anTmpType);
                bankAccountTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_NORMAL);
                addBankAccountTmp(bankAccountTmp);
            }
        }
    }

    /**
     * 检查是否是未改变银行账户
     */
    private boolean checkIsNormalBankAccount(final CustMechBankAccount anBankAccount, final Collection<CustMechBankAccountTmp> anBankAccountTmps) {
        boolean flag = true;
        final Long id = anBankAccount.getId();
        for (final CustMechBankAccountTmp bankAccountTmp : anBankAccountTmps) {
            final Long refId = bankAccountTmp.getRefId();
            if (refId != null && refId.equals(id)) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * 修改一个变更申请
     */
    public CustChangeApply saveChangeApply(final Map<String, Object> anParam, final Long anApplyId) {
        BTAssert.notNull(anApplyId, "变更申请编号不允许为空！");
        BTAssert.notNull(anParam, "参数不允许为空！");

        final CustChangeApply changeApply = checkChangeApply(anApplyId);

        final Long custNo = changeApply.getCustNo();

        final String tempTmpIds = (String) anParam.get("tmpIds");

        COMMA_PATTERN.splitAsStream(tempTmpIds).map(Long::valueOf)
                .forEach(tmpId -> saveBankAccountTmpParentIdAndStatus(tmpId, anApplyId, CustomerConstants.TMP_STATUS_USEING));

        changeApplyService.saveChangeApply(anApplyId, tempTmpIds);

        final Collection<CustMechBankAccount> bankAccounts = bankAccountService.queryCustMechBankAccount(custNo);
        final Collection<CustMechBankAccountTmp> bankAccountTmps = this.selectByProperty("parentId", anApplyId);
        saveNormalBankAccounts(bankAccounts, bankAccountTmps, changeApply, CustomerConstants.TMP_TYPE_CHANGE);

        checkDefaultBankAccount(anApplyId);

        return changeApply;
    }

    /**
     * 加载未提交的变更流水列表
     */
    public Collection<CustMechBankAccountTmp> queryNewChangeBankAccountTmp(final Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空!");

        final Map<String, Object> conditionMap = new HashMap<>();

        final Long version = VersionHelper.generateVersion(this.mapper, anCustNo);

        conditionMap.put("custNo", anCustNo);
        conditionMap.put("version", version);
        conditionMap.put("tmpType", CustomerConstants.TMP_TYPE_CHANGE);
        conditionMap.put("businStatus", CustomerConstants.TMP_STATUS_NEW);

        return this.selectByProperty(conditionMap);
    }

    /**
     * 加载变更流水列表
     */
    public Collection<CustMechBankAccountTmp> queryChangeBankAccountTmp(final Long anApplyId) {
        BTAssert.notNull(anApplyId, "变更申请编号不允许为空!");

        final CustChangeApply changeApply = checkChangeApply(anApplyId);

        final Long custNo = changeApply.getCustNo();

        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("custNo", custNo);
        conditionMap.put("parentId", anApplyId);
        conditionMap.put("tmpType", CustomerConstants.TMP_TYPE_CHANGE);

        return this.selectByProperty(conditionMap);
    }

    /**
     * 加载代录流水列表
     */
    public Collection<CustMechBankAccountTmp> queryInsteadBankAccountTmp(final Long anInsteadRecordId) {
        BTAssert.notNull(anInsteadRecordId, "代录记录编号不允许为空!");

        final CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId);

        final Long custNo = insteadRecord.getCustNo();

        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("custNo", custNo);
        conditionMap.put("parentId", insteadRecord.getId());
        conditionMap.put("tmpType", CustomerConstants.TMP_TYPE_INSTEAD);

        return this.selectByProperty(conditionMap);
    }

    /**
     * 添加公司银行账户流水信息
     */
    public CustMechBankAccountTmp addBankAccountTmp(final CustMechBankAccountTmp anBankAccountTmp) {
        BTAssert.notNull(anBankAccountTmp, "公司银行账户流水信息不允许为空！");
        final Long custNo = anBankAccountTmp.getCustNo();
        final Long version = VersionHelper.generateVersion(this.mapper, custNo);

        anBankAccountTmp.setVersion(version);
        // anCustMechBankAccountTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW, anTmpType, version);
        this.insert(anBankAccountTmp);
        return anBankAccountTmp;
    }

    /**
     * 修改公司银行账户流水信息
     */
    public CustMechBankAccountTmp saveBankAccountTmp(final CustMechBankAccountTmp anBankAccountTmp, final Long anId, final String anFileList) {
        BTAssert.notNull(anBankAccountTmp, "公司银行账户流水信息不允许为空！");
        BTAssert.notNull(anId, "公司银行账户流水编号不允许为空！");

        final CustMechBankAccountTmp tempBankAccountTmp = this.selectByPrimaryKey(anId);
        tempBankAccountTmp.initModifyValue(anBankAccountTmp);
        tempBankAccountTmp.setBatchNo(fileItemService.updateCustFileItemInfo(anFileList, tempBankAccountTmp.getBatchNo()));
        return saveBankAccountTmp(tempBankAccountTmp);
    }

    public CustMechBankAccountTmp saveBankAccountTmp(final CustMechBankAccountTmp anBankAccountTmp) {
        BTAssert.notNull(anBankAccountTmp, "公司银行账户流水信息不允许为空！");
        this.updateByPrimaryKeySelective(anBankAccountTmp);
        return anBankAccountTmp;
    }

    /**
     * 保存 parentId 和 状态
     */
    public CustMechBankAccountTmp saveBankAccountTmpParentIdAndStatus(final Long anId, final Long anParentId, final String anBusinStatus) {
        final CustMechBankAccountTmp bankAccountTmp = this.selectByPrimaryKey(anId);

        if (anParentId != null) {
            bankAccountTmp.setParentId(anParentId);
        }

        bankAccountTmp.setBusinStatus(anBusinStatus);
        this.updateByPrimaryKeySelective(bankAccountTmp);

        return bankAccountTmp;
    }

    /**
     * 添加代录流水
     *
     * @param anInsteadRecordId
     */
    public CustMechBankAccountTmp addInsteadBankAccountTmp(final CustMechBankAccountTmp anBankAccountTmp, final Long anInsteadRecordId,
            final String anFileList) {
        BTAssert.notNull(anBankAccountTmp, "公司银行账户流水信息不允许为空！");

        checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN,
                CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT, CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        final Long refId = anBankAccountTmp.getRefId();

        final CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId);
        BTAssert.isNull(refId, "引用编号需要为空!");

        if (insteadRecord.getCustNo().equals(anBankAccountTmp.getCustNo()) == false) {
            throw new BytterTradeException("客户编号不匹配!");
        }

        anBankAccountTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW, CustomerConstants.TMP_TYPE_INSTEAD, null);
        anBankAccountTmp.setParentId(anInsteadRecordId);
        anBankAccountTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_ADD);
        anBankAccountTmp.setBatchNo(fileItemService.updateCustFileItemInfo(anFileList, anBankAccountTmp.getBatchNo()));

        return addBankAccountTmp(anBankAccountTmp);
    }

    /**
     * 添加修改代录流水
     */
    public CustMechBankAccountTmp saveSaveInsteadBankAccountTmp(final CustMechBankAccountTmp anBankAccountTmp, final Long anInsteadRecordId,
            final String anFileList) {
        BTAssert.notNull(anBankAccountTmp, "公司银行账户流水信息不允许为空！");

        final CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW,
                CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN, CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT,
                CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        final Long refId = anBankAccountTmp.getRefId();
        BTAssert.notNull(refId, "引用编号不允许为空!");

        final CustMechBankAccount bankAccount = bankAccountService.findCustMechBankAccount(refId);
        BTAssert.notNull(bankAccount, "没有找到引用的记录!");

        if (insteadRecord.getCustNo().equals(anBankAccountTmp.getCustNo()) == false) {
            throw new BytterTradeException("客户编号不匹配!");
        }

        final CustMechBankAccountTmp tempBankAccountTmp = findBankAccountTmpByRefId(refId, CustomerConstants.TMP_TYPE_INSTEAD);
        if (tempBankAccountTmp == null) {
            anBankAccountTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW, CustomerConstants.TMP_TYPE_INSTEAD, null);
            anBankAccountTmp.setParentId(anInsteadRecordId);
            anBankAccountTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            anBankAccountTmp.setBatchNo(
                    fileItemService.updateAndDuplicateConflictFileItemInfo(anFileList, anBankAccountTmp.getBatchNo(), UserUtils.getOperatorInfo()));
            return addBankAccountTmp(anBankAccountTmp);
        }
        else {
            tempBankAccountTmp.initModifyValue(anBankAccountTmp);
            tempBankAccountTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            tempBankAccountTmp.setBatchNo(
                    fileItemService.updateAndDuplicateConflictFileItemInfo(anFileList, tempBankAccountTmp.getBatchNo(), UserUtils.getOperatorInfo()));
            return saveBankAccountTmp(tempBankAccountTmp);
        }
    }

    /**
     * 添加删除代录流水
     *
     * @param anInsteadRecordId
     */
    public CustMechBankAccountTmp saveDeleteInsteadBankAccountTmp(final Long anRefId, final Long anInsteadRecordId) {
        BTAssert.notNull(anRefId, "公司银行账户号不允许为空！");

        final CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW,
                CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN, CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT,
                CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        final CustMechBankAccount bankAccount = bankAccountService.findCustMechBankAccount(anRefId);
        BTAssert.notNull(bankAccount, "没有找到引用的记录!");

        if (insteadRecord.getCustNo().equals(bankAccount.getCustNo()) == false) {
            throw new BytterTradeException("客户编号不匹配");
        }

        CustMechBankAccountTmp bankAccountTmp = findBankAccountTmpByRefId(anRefId, CustomerConstants.TMP_TYPE_INSTEAD);
        if (bankAccountTmp == null) {
            bankAccountTmp = new CustMechBankAccountTmp();
            bankAccountTmp.initAddValue(bankAccount, CustomerConstants.TMP_STATUS_NEW);
            bankAccountTmp.setRefId(anRefId);
            bankAccountTmp.setParentId(anInsteadRecordId);
            bankAccountTmp.setTmpType(CustomerConstants.TMP_TYPE_INSTEAD);
            bankAccountTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return addBankAccountTmp(bankAccountTmp);
        }
        else {
            bankAccountTmp.initModifyValue(bankAccount, CustomerConstants.TMP_STATUS_NEW);
            bankAccountTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return saveBankAccountTmp(bankAccountTmp, bankAccountTmp.getId(), null);
        }
    }

    /**
     * 撤销代录流水
     *
     * @param anInsteadRecordId
     */
    public int saveCancelInsteadBankAccountTmp(final Long anId, final Long anInsteadRecordId) {
        final CustMechBankAccountTmp bankAccountTmp = this.findBankAccountTmp(anId);

        checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN,
                CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT, CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        final Long tmpVersion = bankAccountTmp.getVersion();
        final Long maxVersion = VersionHelper.generateVersion(this.mapper, bankAccountTmp.getCustNo());

        if (tmpVersion.equals(maxVersion) == false) {
            throw new BytterTradeException("流水信息不正确,不可撤销.");
        }

        if (BetterStringUtils.equals(bankAccountTmp.getTmpType(), CustomerConstants.TMP_TYPE_INSTEAD) == false) {
            throw new BytterTradeException("流水信息类型不正确,不可撤销.");
        }

        if (BetterStringUtils.equals(bankAccountTmp.getBusinStatus(), CustomerConstants.TMP_STATUS_NEW) == false) {
            throw new BytterTradeException("流水信息状态不正确,不可撤销.");
        }

        return this.deleteByPrimaryKey(anId);
    }

    /**
     * 提交代录项目
     */
    public CustInsteadRecord addInsteadRecord(final Map<String, Object> anParam, final Long anInsteadRecordId) {
        BTAssert.notNull(anInsteadRecordId, "代录编号不允许为空！");
        BTAssert.notNull(anParam, "参数不允许为空！");

        final CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW);

        final String tempTmpIds = (String) anParam.get("tmpIds");

        COMMA_PATTERN.splitAsStream(tempTmpIds).map(Long::valueOf)
                .forEach(tmpId -> saveBankAccountTmpParentIdAndStatus(tmpId, insteadRecord.getId(), CustomerConstants.TMP_STATUS_USEING));

        insteadRecordService.saveInsteadRecordStatus(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN);

        return insteadRecord;
    }

    /**
     * 修改代录项目
     */
    public CustInsteadRecord saveInsteadRecord(final Map<String, Object> anParam, final Long anInsteadRecordId) {
        BTAssert.notNull(anInsteadRecordId, "代录编号不允许为空！");
        BTAssert.notNull(anParam, "参数不允许为空！");

        final CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId);

        final String tempTmpIds = (String) anParam.get("tmpIds");

        COMMA_PATTERN.splitAsStream(tempTmpIds).map(Long::valueOf)
                .forEach(tmpId -> saveBankAccountTmpParentIdAndStatus(tmpId, insteadRecord.getId(), CustomerConstants.TMP_STATUS_USEING));

        insteadRecordService.saveInsteadRecord(anInsteadRecordId, tempTmpIds);

        return insteadRecord;
    }

    /**
     * 回写正式数据
     */
    @Override
    public void saveFormalData(final Long anId) {
        final Collection<CustMechBankAccountTmp> bankAccountTmps = this.selectByProperty("parentId", anId);

        for (final CustMechBankAccountTmp bankAccountTmp : bankAccountTmps) {
            final String tmpOperType = bankAccountTmp.getTmpOperType();
            switch (tmpOperType) {
            case CustomerConstants.TMP_OPER_TYPE_ADD:
                final CustMechBankAccount bankAccount = bankAccountService.addCustMechBankAccount(bankAccountTmp);
                bankAccountTmp.setRefId(bankAccount.getId());
                this.updateByPrimaryKey(bankAccountTmp);
                break;
            case CustomerConstants.TMP_OPER_TYPE_DELETE:
                bankAccountService.deleteByPrimaryKey(bankAccountTmp.getRefId());
                break;
            case CustomerConstants.TMP_OPER_TYPE_MODIFY:
                bankAccountService.saveCustMechBankAccount(bankAccountTmp);
                break;
            case CustomerConstants.TMP_OPER_TYPE_NORMAL: // 不作处理
            default:
                break;
            }

            saveBankAccountTmpParentIdAndStatus(bankAccountTmp.getId(), null, CustomerConstants.TMP_STATUS_USED);
        }
    }

    /**
     * 回写作废记录
     */
    @Override
    public void saveCancelData(final Long anId) {

    }

    /**
     * 检查并返回代录记录
     */
    private CustInsteadRecord checkInsteadRecord(final Long anInsteadRecordId, final String... anBusinStatus) {
        BTAssert.notNull(anInsteadRecordId, "代录记录编号不允许为空！");

        final CustInsteadRecord insteadRecord = insteadRecordService.findInsteadRecord(anInsteadRecordId);
        BTAssert.notNull(insteadRecord, "没有找到对应的代录记录");

        final String insteadItem = insteadRecord.getInsteadItem();
        if (BetterStringUtils.equals(insteadItem, CustomerConstants.ITEM_BANKACCOUNT) == false) {
            throw new BytterTradeException(20072, "代录项目不匹配！");
        }

        if (anBusinStatus.length != 0) {
            final List<String> businStatus = Arrays.asList(anBusinStatus);
            if (businStatus.contains(insteadRecord.getBusinStatus()) == false) {
                throw new BytterTradeException(20071, "此代录项目状态不正确！");
            }
        }

        return insteadRecord;
    }

    /**
     * 检查并返回变更申请
     */
    public CustChangeApply checkChangeApply(final Long anApplyId, final String... anBusinStatus) {
        BTAssert.notNull(anApplyId, "变更申请-编号 不能为空");

        final CustChangeApply changeApply = changeApplyService.findChangeApply(anApplyId);
        final String changeItem = changeApply.getChangeItem();
        if (BetterStringUtils.equals(changeItem, CustomerConstants.ITEM_BANKACCOUNT) == false) {
            throw new BytterTradeException(20074, "变更项目不匹配!");
        }

        if (anBusinStatus.length != 0) {
            final List<String> businStatus = Arrays.asList(anBusinStatus);
            if (businStatus.contains(changeApply.getBusinStatus()) == false) {
                throw new BytterTradeException(20071, "此变更申请状态不正确！");
            }
        }

        return changeApply;
    }

    /**
     * 检查是否匹配
     */
    private boolean checkMatchNewChange(final List<Long> anChangeIds, final Long anCustNo) {
        final Collection<CustMechBankAccountTmp> bankAccountTmps = queryNewChangeBankAccountTmp(anCustNo);
        if (anChangeIds.size() != bankAccountTmps.size()) {
            return false;
        }
        final Set<Long> tempSet = new HashSet<>();

        for (final CustMechBankAccountTmp bankAccountTmp : bankAccountTmps) {
            if (anCustNo.equals(bankAccountTmp.getCustNo()) == false) {
                return false;
            }

            if (BetterStringUtils.equals(bankAccountTmp.getTmpOperType(), CustomerConstants.TMP_OPER_TYPE_NORMAL) == true) {
                continue;
            }
            else {
                final Long id = bankAccountTmp.getId();
                for (final Long changeId : anChangeIds) {
                    if (id.equals(changeId) == true) {
                        tempSet.add(changeId);
                    }
                }
            }
        }

        return (tempSet.size() == anChangeIds.size());
    }

    @Override
    public ICustAuditEntityFace findSaveDataByParentId(final Long anParentId) {

        return Collections3.getFirst(this.selectByProperty("parentId", anParentId));
    }

}