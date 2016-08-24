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
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.dao.CustMechBankAccountTmpMapper;
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
    public CustMechBankAccountTmp findBankAccountTmp(Long anId) {
        BTAssert.notNull(anId, "公司银行账户流水信息编号不允许为空！");

        final CustMechBankAccountTmp bankAccountTmp = this.selectByPrimaryKey(anId);

        return bankAccountTmp;
    }

    /**
     * 取上一版
     */
    public CustMechBankAccountTmp findBankAccountTmpPrevVersion(CustMechBankAccountTmp anBankAccountTmp) {
        Long custNo = anBankAccountTmp.getCustNo();
        Long refId = anBankAccountTmp.getRefId();
        Long version = anBankAccountTmp.getVersion();

        Long befVersion = this.mapper.selectPrevVersion(custNo, version);

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("version", befVersion);
        conditionMap.put("refId", refId);
        conditionMap.put("custNo", custNo);

        return Collections3.getFirst(this.selectByProperty(conditionMap));
    }

    /**
     * 添加新增变更流水记录
     */
    public CustMechBankAccountTmp addChangeBankAccountTmp(CustMechBankAccountTmp anBankAccountTmp, String anFileList) {
        BTAssert.notNull(anBankAccountTmp, "公司银行账户流水信息不允许为空！");

        final Long refId = anBankAccountTmp.getRefId();
        BTAssert.isNull(refId, "引用编号不能有值!");

        anBankAccountTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW);
        anBankAccountTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_ADD);
        anBankAccountTmp.setBatchNo(fileItemService.updateCustFileItemInfo(anFileList, anBankAccountTmp.getBatchNo()));

        return addBankAccountTmp(anBankAccountTmp, CustomerConstants.TMP_TYPE_CHANGE);
    }

    /**
     * 添加修改变更记录
     */
    public CustMechBankAccountTmp saveSaveChangeBankAccountTmp(CustMechBankAccountTmp anBankAccountTmp, String anFileList) {
        BTAssert.notNull(anBankAccountTmp, "公司银行账户流水信息不允许为空！");

        final Long refId = anBankAccountTmp.getRefId();
        BTAssert.notNull(refId, "引用编号不允许为空!");

        CustMechBankAccount bankAccount = bankAccountService.findCustMechBankAccount(refId);
        BTAssert.notNull(bankAccount, "没有找到引用的记录!");

        if (bankAccount.getCustNo().equals(anBankAccountTmp.getCustNo()) == false) {
            throw new BytterTradeException("客户编号不匹配!");
        }

        CustMechBankAccountTmp tempBankAccountTmp = findBankAccountTmpByRefId(refId, CustomerConstants.TMP_TYPE_CHANGE);
        if (tempBankAccountTmp == null) {
            anBankAccountTmp.setBusinStatus(CustomerConstants.TMP_STATUS_NEW);
            anBankAccountTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            // TODO @@@@@@@@@@@@@@@@@@ 需要处理
            anBankAccountTmp.setBatchNo(fileItemService.updateCustFileItemInfo(anFileList, anBankAccountTmp.getBatchNo()));
            return addBankAccountTmp(anBankAccountTmp, CustomerConstants.TMP_TYPE_CHANGE);
        }
        else {
            tempBankAccountTmp.initModifyValue(anBankAccountTmp);
            tempBankAccountTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            return saveBankAccountTmp(tempBankAccountTmp, tempBankAccountTmp.getId(), anFileList);
        }
    }

    /**
     * 添加删除变更记录
     */
    public CustMechBankAccountTmp saveDeleteChangeBankAccountTmp(Long anRefId) {
        BTAssert.notNull(anRefId, "公司银行账户号不允许为空！");

        CustMechBankAccount bankAccount = bankAccountService.findCustMechBankAccount(anRefId);
        BTAssert.notNull(bankAccount, "没有找到引用的记录!");

        CustMechBankAccountTmp bankAccountTmp = findBankAccountTmpByRefId(anRefId, CustomerConstants.TMP_TYPE_CHANGE);
        if (bankAccountTmp == null) {
            bankAccountTmp = new CustMechBankAccountTmp();
            bankAccountTmp.initAddValue(bankAccount, CustomerConstants.TMP_STATUS_NEW);
            bankAccountTmp.setRefId(anRefId);
            bankAccountTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return addBankAccountTmp(bankAccountTmp, CustomerConstants.TMP_TYPE_CHANGE);
        }
        else {
            bankAccountTmp.initModifyValue(bankAccount, CustomerConstants.TMP_STATUS_NEW);
            bankAccountTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return saveBankAccountTmp(bankAccountTmp, bankAccountTmp.getId(), null);
        }
    }

    /**
     * 撤销变更记录
     */
    public int saveCancelChangeBankAccountTmp(Long anId) {
        CustMechBankAccountTmp bankAccountTmp = this.findBankAccountTmp(anId);

        Long tmpVersion = bankAccountTmp.getVersion();
        Long maxVersion = VersionHelper.generateVersion(this.mapper, bankAccountTmp.getCustNo());

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
    public Collection<CustMechBankAccountTmp> queryBankAccountTmpByChangeApply(Long anApplyId) {
        BTAssert.notNull(anApplyId, "公司编号不允许为空！");
        CustChangeApply changeApply = changeApplyService.findChangeApply(anApplyId);

        long[] tmpIds = COMMA_PATTERN.splitAsStream(changeApply.getTmpIds()).mapToLong(Long::valueOf).toArray();

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put(CustomerConstants.ID, tmpIds);

        return this.selectByProperty(conditionMap);
    }

    /**
     * 
     */
    private CustMechBankAccountTmp findBankAccountTmpByRefId(Long anRefId, String anTmpType) {
        Map<String, Object> conditionMap = new HashMap<>();

        conditionMap.put("refId", anRefId);
        conditionMap.put("tmpType", anTmpType);
        conditionMap.put("businStatus", CustomerConstants.TMP_STATUS_NEW);

        return Collections3.getFirst(this.selectByProperty(conditionMap));
    }

    /**
     * 添加一个变更申请
     */
    public CustChangeApply addChangeApply(Map<String, Object> anParam, Long anCustNo) {
        BTAssert.notNull(anCustNo, "公司编号不允许为空！");
        BTAssert.notNull(anParam, "参数不允许为空！");

        String tempTmpIds = (String) anParam.get("tmpIds");

        List<Long> tmpIds = COMMA_PATTERN.splitAsStream(tempTmpIds).map(Long::valueOf).collect(Collectors.toList());

        if (checkMatchNewChange(tmpIds, anCustNo) == false) {
            throw new BytterTradeException("代录编号列表不正确,请检查.");
        }
        CustChangeApply changeApply = changeApplyService.addChangeApply(anCustNo, CustomerConstants.ITEM_BANKACCOUNT, tempTmpIds);

        for (Long id : tmpIds) {
            saveBankAccountTmpParentIdAndStatus(id, changeApply.getId(), CustomerConstants.TMP_STATUS_USEING);
        }

        Collection<CustMechBankAccount> bankAccounts = bankAccountService.queryCustMechBankAccount(anCustNo);
        Collection<CustMechBankAccountTmp> bankAccountTmps = this.selectByProperty("parentId", changeApply.getId());

        saveNormalBankAccounts(bankAccounts, bankAccountTmps, changeApply, CustomerConstants.TMP_TYPE_CHANGE);

        return changeApply;
    }

    /**
     * 存储未修改的记录
     */
    private void saveNormalBankAccounts(Collection<CustMechBankAccount> anBankAccounts, Collection<CustMechBankAccountTmp> anBankAccountTmps,
            CustChangeApply anChangeApply, String anTmpType) {
        Long parentId = anChangeApply.getId();
        for (CustMechBankAccount bankAccount : anBankAccounts) {
            if (checkIsNormalBankAccount(bankAccount, anBankAccountTmps) == true) {
                CustMechBankAccountTmp bankAccountTmp = new CustMechBankAccountTmp();
                bankAccountTmp.initAddValue(bankAccount, CustomerConstants.TMP_STATUS_USEING);
                bankAccountTmp.setRefId(bankAccount.getId());
                bankAccountTmp.setParentId(parentId);
                bankAccountTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_NORMAL);
                addBankAccountTmp(bankAccountTmp, anTmpType);
            }
        }
    }

    /**
     * 检查是否是未改变银行账户
     */
    private boolean checkIsNormalBankAccount(CustMechBankAccount anBankAccount, Collection<CustMechBankAccountTmp> anBankAccountTmps) {
        boolean flag = true;
        Long id = anBankAccount.getId();
        for (CustMechBankAccountTmp bankAccountTmp : anBankAccountTmps) {
            Long refId = bankAccountTmp.getRefId();
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
    public CustChangeApply saveChangeApply(Map<String, Object> anParam, Long anApplyId) {
        BTAssert.notNull(anApplyId, "变更申请编号不允许为空！");
        BTAssert.notNull(anParam, "参数不允许为空！");

        CustChangeApply changeApply = checkChangeApply(anApplyId);

        Long custNo = changeApply.getCustNo();

        String tempTmpIds = (String) anParam.get("tmpIds");

        COMMA_PATTERN.splitAsStream(tempTmpIds).map(Long::valueOf)
                .forEach(tmpId -> saveBankAccountTmpParentIdAndStatus(tmpId, anApplyId, CustomerConstants.TMP_STATUS_USEING));

        changeApplyService.saveChangeApply(anApplyId, tempTmpIds);

        Collection<CustMechBankAccount> bankAccounts = bankAccountService.queryCustMechBankAccount(custNo);
        Collection<CustMechBankAccountTmp> bankAccountTmps = this.selectByProperty("parentId", anApplyId);
        saveNormalBankAccounts(bankAccounts, bankAccountTmps, changeApply, CustomerConstants.TMP_TYPE_CHANGE);

        return changeApply;
    }

    /**
     * 加载未提交的变更流水列表
     */
    public Collection<CustMechBankAccountTmp> queryNewChangeBankAccountTmp(Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空!");

        Map<String, Object> conditionMap = new HashMap<>();

        Long version = VersionHelper.generateVersion(this.mapper, anCustNo);

        conditionMap.put("custNo", anCustNo);
        conditionMap.put("version", version);
        conditionMap.put("tmpType", CustomerConstants.TMP_TYPE_CHANGE);
        conditionMap.put("businStatus", CustomerConstants.TMP_STATUS_NEW);

        return this.selectByProperty(conditionMap);
    }

    /**
     * 加载变更流水列表
     */
    public Collection<CustMechBankAccountTmp> queryChangeBankAccountTmp(Long anApplyId) {
        BTAssert.notNull(anApplyId, "变更申请编号不允许为空!");

        CustChangeApply changeApply = checkChangeApply(anApplyId);

        Long custNo = changeApply.getCustNo();

        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("custNo", custNo);
        conditionMap.put("parentId", anApplyId);
        conditionMap.put("tmpType", CustomerConstants.TMP_TYPE_CHANGE);

        return this.selectByProperty(conditionMap);
    }

    /**
     * 加载代录流水列表
     */
    public Collection<CustMechBankAccountTmp> queryInsteadBankAccountTmp(Long anInsteadRecordId) {
        BTAssert.notNull(anInsteadRecordId, "代录记录编号不允许为空!");

        CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId);

        Long custNo = insteadRecord.getCustNo();

        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("custNo", custNo);
        conditionMap.put("parentId", insteadRecord.getId());
        conditionMap.put("tmpType", CustomerConstants.TMP_TYPE_INSTEAD);

        return this.selectByProperty(conditionMap);
    }

    /**
     * 添加公司银行账户流水信息
     */
    public CustMechBankAccountTmp addBankAccountTmp(CustMechBankAccountTmp anBankAccountTmp, String anTmpType) {
        BTAssert.notNull(anBankAccountTmp, "公司银行账户流水信息不允许为空！");
        Long custNo = anBankAccountTmp.getCustNo();
        Long version = VersionHelper.generateVersion(this.mapper, custNo);

        anBankAccountTmp.setVersion(version);
        anBankAccountTmp.setTmpType(anTmpType);
        // anCustMechBankAccountTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW, anTmpType, version);
        this.insert(anBankAccountTmp);
        return anBankAccountTmp;
    }

    /**
     * 修改公司银行账户流水信息
     */
    public CustMechBankAccountTmp saveBankAccountTmp(CustMechBankAccountTmp anBankAccountTmp, Long anId, String anFileList) {
        BTAssert.notNull(anBankAccountTmp, "公司银行账户流水信息不允许为空！");
        BTAssert.notNull(anId, "公司银行账户流水编号不允许为空！");

        CustMechBankAccountTmp tempBankAccountTmp = this.selectByPrimaryKey(anId);
        tempBankAccountTmp.initModifyValue(anBankAccountTmp);
        tempBankAccountTmp.setBatchNo(fileItemService.updateCustFileItemInfo(anFileList, tempBankAccountTmp.getBatchNo()));
        this.updateByPrimaryKeySelective(tempBankAccountTmp);

        return tempBankAccountTmp;
    }

    /**
     * 保存 parentId 和 状态
     */
    public CustMechBankAccountTmp saveBankAccountTmpParentIdAndStatus(Long anId, Long anParentId, String anBusinStatus) {
        CustMechBankAccountTmp bankAccountTmp = this.selectByPrimaryKey(anId);

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
    public CustMechBankAccountTmp addInsteadBankAccountTmp(CustMechBankAccountTmp anBankAccountTmp, Long anInsteadRecordId, String anFileList) {
        BTAssert.notNull(anBankAccountTmp, "公司银行账户流水信息不允许为空！");

        checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN,
                CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT, CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        final Long refId = anBankAccountTmp.getRefId();

        CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId);
        BTAssert.isNull(refId, "引用编号需要为空!");

        if (insteadRecord.getCustNo().equals(anBankAccountTmp.getCustNo()) == false) {
            throw new BytterTradeException("客户编号不匹配!");
        }

        anBankAccountTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW);
        anBankAccountTmp.setParentId(anInsteadRecordId);
        anBankAccountTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_ADD);
        anBankAccountTmp.setBatchNo(fileItemService.updateCustFileItemInfo(anFileList, anBankAccountTmp.getBatchNo()));

        return addBankAccountTmp(anBankAccountTmp, CustomerConstants.TMP_TYPE_INSTEAD);
    }

    /**
     * 添加修改代录流水
     */
    public CustMechBankAccountTmp saveSaveInsteadBankAccountTmp(CustMechBankAccountTmp anBankAccountTmp, Long anInsteadRecordId, String anFileList) {
        BTAssert.notNull(anBankAccountTmp, "公司银行账户流水信息不允许为空！");

        CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW,
                CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN, CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT,
                CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        final Long refId = anBankAccountTmp.getRefId();
        BTAssert.notNull(refId, "引用编号不允许为空!");

        CustMechBankAccount bankAccount = bankAccountService.findCustMechBankAccount(refId);
        BTAssert.notNull(bankAccount, "没有找到引用的记录!");

        if (insteadRecord.getCustNo().equals(anBankAccountTmp.getCustNo()) == false) {
            throw new BytterTradeException("客户编号不匹配!");
        }

        CustMechBankAccountTmp tempBankAccountTmp = findBankAccountTmpByRefId(refId, CustomerConstants.TMP_TYPE_INSTEAD);
        if (tempBankAccountTmp == null) {
            anBankAccountTmp.setParentId(anInsteadRecordId);
            anBankAccountTmp.setBusinStatus(CustomerConstants.TMP_STATUS_NEW);
            anBankAccountTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            // TODO @@@@@@@@@ batchNo 需要处理
            anBankAccountTmp.setBatchNo(fileItemService.updateCustFileItemInfo(anFileList, anBankAccountTmp.getBatchNo()));
            return addBankAccountTmp(anBankAccountTmp, CustomerConstants.TMP_TYPE_INSTEAD);
        }
        else {
            tempBankAccountTmp.initModifyValue(anBankAccountTmp);
            tempBankAccountTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            return saveBankAccountTmp(tempBankAccountTmp, tempBankAccountTmp.getId(), anFileList);
        }
    }

    /**
     * 添加删除代录流水
     * 
     * @param anInsteadRecordId
     */
    public CustMechBankAccountTmp saveDeleteInsteadBankAccountTmp(Long anRefId, Long anInsteadRecordId) {
        BTAssert.notNull(anRefId, "公司银行账户号不允许为空！");

        CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW,
                CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN, CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT,
                CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        CustMechBankAccount bankAccount = bankAccountService.findCustMechBankAccount(anRefId);
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
            bankAccountTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return addBankAccountTmp(bankAccountTmp, CustomerConstants.TMP_TYPE_INSTEAD);
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
    public int saveCancelInsteadBankAccountTmp(Long anId, Long anInsteadRecordId) {
        CustMechBankAccountTmp bankAccountTmp = this.findBankAccountTmp(anId);

        checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN,
                CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT, CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        Long tmpVersion = bankAccountTmp.getVersion();
        Long maxVersion = VersionHelper.generateVersion(this.mapper, bankAccountTmp.getCustNo());

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
    public CustInsteadRecord addInsteadRecord(Map<String, Object> anParam, Long anInsteadRecordId) {
        BTAssert.notNull(anInsteadRecordId, "代录编号不允许为空！");
        BTAssert.notNull(anParam, "参数不允许为空！");

        CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW);

        String tempTmpIds = (String) anParam.get("tmpIds");

        COMMA_PATTERN.splitAsStream(tempTmpIds).map(Long::valueOf)
                .forEach(tmpId -> saveBankAccountTmpParentIdAndStatus(tmpId, insteadRecord.getId(), CustomerConstants.TMP_STATUS_USEING));

        insteadRecordService.saveInsteadRecordStatus(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN);

        return insteadRecord;
    }

    /**
     * 修改代录项目
     */
    public CustInsteadRecord saveInsteadRecord(Map<String, Object> anParam, Long anInsteadRecordId) {
        BTAssert.notNull(anInsteadRecordId, "代录编号不允许为空！");
        BTAssert.notNull(anParam, "参数不允许为空！");

        CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId);

        String tempTmpIds = (String) anParam.get("tmpIds");

        COMMA_PATTERN.splitAsStream(tempTmpIds).map(Long::valueOf)
                .forEach(tmpId -> saveBankAccountTmpParentIdAndStatus(tmpId, insteadRecord.getId(), CustomerConstants.TMP_STATUS_USEING));

        insteadRecordService.saveInsteadRecord(anInsteadRecordId, tempTmpIds);

        return insteadRecord;
    }

    /**
     * 回写正式数据
     */
    @Override
    public void saveFormalData(Long anId) {
        Collection<CustMechBankAccountTmp> bankAccountTmps = this.selectByProperty("parentId", anId);

        for (CustMechBankAccountTmp bankAccountTmp : bankAccountTmps) {
            String tmpOperType = bankAccountTmp.getTmpOperType();
            switch (tmpOperType) {
            case CustomerConstants.TMP_OPER_TYPE_ADD:
                CustMechBankAccount bankAccount = bankAccountService.addCustMechBankAccount(bankAccountTmp);
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
    public void saveCancelData(Long anId) {

    }

    /**
     * 检查并返回代录记录
     */
    private CustInsteadRecord checkInsteadRecord(Long anInsteadRecordId, String... anBusinStatus) {
        BTAssert.notNull(anInsteadRecordId, "代录记录编号不允许为空！");

        CustInsteadRecord insteadRecord = insteadRecordService.findInsteadRecord(anInsteadRecordId);
        BTAssert.notNull(insteadRecord, "没有找到对应的代录记录");

        String insteadItem = insteadRecord.getInsteadItem();
        if (BetterStringUtils.equals(insteadItem, CustomerConstants.ITEM_BANKACCOUNT) == false) {
            throw new BytterTradeException(20072, "代录项目不匹配！");
        }

        if (anBusinStatus.length != 0) {
            List<String> businStatus = Arrays.asList(anBusinStatus);
            if (businStatus.contains(insteadRecord.getBusinStatus()) == false) {
                throw new BytterTradeException(20071, "此代录项目状态不正确！");
            }
        }

        return insteadRecord;
    }

    /**
     * 检查并返回变更申请
     */
    public CustChangeApply checkChangeApply(Long anApplyId, String... anBusinStatus) {
        BTAssert.notNull(anApplyId, "变更申请-编号 不能为空");

        CustChangeApply changeApply = changeApplyService.findChangeApply(anApplyId);
        String changeItem = changeApply.getChangeItem();
        if (BetterStringUtils.equals(changeItem, CustomerConstants.ITEM_BANKACCOUNT) == false) {
            throw new BytterTradeException(20074, "变更项目不匹配!");
        }

        if (anBusinStatus.length != 0) {
            List<String> businStatus = Arrays.asList(anBusinStatus);
            if (businStatus.contains(changeApply.getBusinStatus()) == false) {
                throw new BytterTradeException(20071, "此变更申请状态不正确！");
            }
        }

        return changeApply;
    }

    /**
     * 检查是否匹配
     */
    private boolean checkMatchNewChange(List<Long> anChangeIds, Long anCustNo) {
        Collection<CustMechBankAccountTmp> bankAccountTmps = queryNewChangeBankAccountTmp(anCustNo);
        if (anChangeIds.size() != bankAccountTmps.size()) {
            return false;
        }
        Set<Long> tempSet = new HashSet<>();

        for (CustMechBankAccountTmp bankAccountTmp : bankAccountTmps) {
            if (anCustNo.equals(bankAccountTmp.getCustNo()) == false) {
                return false;
            }

            if (BetterStringUtils.equals(bankAccountTmp.getTmpOperType(), CustomerConstants.TMP_OPER_TYPE_NORMAL) == true) {
                continue;
            }
            else {
                Long id = bankAccountTmp.getId();
                for (Long changeId : anChangeIds) {
                    if (id.equals(changeId) == true) {
                        tempSet.add(changeId);
                    }
                }
            }
        }

        return (tempSet.size() == anChangeIds.size());
    }

}