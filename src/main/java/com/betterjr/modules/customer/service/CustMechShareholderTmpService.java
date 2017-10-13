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
import com.betterjr.modules.customer.dao.CustMechShareholderTmpMapper;
import com.betterjr.modules.customer.data.ICustAuditEntityFace;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustInsteadRecord;
import com.betterjr.modules.customer.entity.CustMechShareholder;
import com.betterjr.modules.customer.entity.CustMechShareholderTmp;
import com.betterjr.modules.customer.helper.IFormalDataService;
import com.betterjr.modules.customer.helper.VersionHelper;
import com.betterjr.modules.document.service.CustFileItemService;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechShareholderTmpService extends BaseService<CustMechShareholderTmpMapper, CustMechShareholderTmp> implements IFormalDataService {
    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    @Resource
    private CustMechShareholderService shareholderService;

    @Resource
    private CustInsteadRecordService insteadRecordService;

    @Resource
    private CustChangeApplyService changeApplyService;

    @Resource
    private CustFileItemService fileItemService;

    /**
     * 查询公司股东流水信息
     */
    public CustMechShareholderTmp findShareholderTmp(final Long anId) {
        BTAssert.notNull(anId, "公司股东流水信息编号不允许为空！");

        final CustMechShareholderTmp shareholderTmp = this.selectByPrimaryKey(anId);

        return shareholderTmp;
    }

    /**
     * 取上一版
     */
    public CustMechShareholderTmp findShareholderTmpPrevVersion(final CustMechShareholderTmp anShareholderTmp) {
        final Long custNo = anShareholderTmp.getCustNo();
        final Long refId = anShareholderTmp.getRefId();
        final Long version = anShareholderTmp.getVersion();

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
    public CustMechShareholderTmp addChangeShareholderTmp(final CustMechShareholderTmp anShareholderTmp, final String anFileList) {
        BTAssert.notNull(anShareholderTmp, "公司股东流水信息不允许为空！");

        final Long refId = anShareholderTmp.getRefId();
        BTAssert.isNull(refId, "引用编号不能有值!");

        anShareholderTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW, CustomerConstants.TMP_TYPE_CHANGE, null);
        anShareholderTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_ADD);
        anShareholderTmp.setBatchNo(fileItemService.updateCustFileItemInfo(anFileList, anShareholderTmp.getBatchNo()));
        return addShareholderTmp(anShareholderTmp);
    }

    /**
     * 添加修改变更记录
     */
    public CustMechShareholderTmp saveSaveChangeShareholderTmp(final CustMechShareholderTmp anShareholderTmp, final String anFileList) {
        BTAssert.notNull(anShareholderTmp, "公司股东流水信息不允许为空！");

        final Long refId = anShareholderTmp.getRefId();
        BTAssert.notNull(refId, "引用编号不允许为空!");

        final CustMechShareholder shareholder = shareholderService.findShareholder(refId);
        BTAssert.notNull(shareholder, "没有找到引用的记录!");

        if (shareholder.getCustNo().equals(anShareholderTmp.getCustNo()) == false) {
            throw new BytterTradeException("客户编号不匹配!");
        }

        final CustMechShareholderTmp tempShareholderTmp = findShareholderTmpByRefId(refId, CustomerConstants.TMP_TYPE_CHANGE);
        if (tempShareholderTmp == null) {
            anShareholderTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW, CustomerConstants.TMP_TYPE_CHANGE, null);
            anShareholderTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            anShareholderTmp.setBatchNo(
                    fileItemService.updateAndDuplicateConflictFileItemInfo(anFileList, anShareholderTmp.getBatchNo(), UserUtils.getOperatorInfo()));
            return addShareholderTmp(anShareholderTmp);
        }
        else {
            tempShareholderTmp.initModifyValue(anShareholderTmp);
            tempShareholderTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            tempShareholderTmp.setBatchNo(
                    fileItemService.updateAndDuplicateConflictFileItemInfo(anFileList, tempShareholderTmp.getBatchNo(), UserUtils.getOperatorInfo()));
            return saveShareholderTmp(tempShareholderTmp);
        }
    }

    /**
     * 添加删除变更记录
     */
    public CustMechShareholderTmp saveDeleteChangeShareholderTmp(final Long anRefId) {
        BTAssert.notNull(anRefId, "公司股东号不允许为空！");

        final CustMechShareholder shareholder = shareholderService.findShareholder(anRefId);
        BTAssert.notNull(shareholder, "没有找到引用的记录!");

        CustMechShareholderTmp shareholderTmp = findShareholderTmpByRefId(anRefId, CustomerConstants.TMP_TYPE_CHANGE);
        if (shareholderTmp == null) {
            shareholderTmp = new CustMechShareholderTmp();
            shareholderTmp.initAddValue(shareholder, CustomerConstants.TMP_STATUS_NEW);
            shareholderTmp.setRefId(anRefId);
            shareholderTmp.setTmpType(CustomerConstants.TMP_TYPE_CHANGE);
            shareholderTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return addShareholderTmp(shareholderTmp);
        }
        else {
            shareholderTmp.initModifyValue(shareholder, CustomerConstants.TMP_STATUS_NEW);
            shareholderTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return saveShareholderTmp(shareholderTmp);
        }
    }

    /**
     * 撤销变更记录
     */
    public int saveCancelChangeShareholderTmp(final Long anId) {
        final CustMechShareholderTmp shareholderTmp = this.findShareholderTmp(anId);

        final Long tmpVersion = shareholderTmp.getVersion();
        final Long maxVersion = VersionHelper.generateVersion(this.mapper, shareholderTmp.getCustNo());

        if (tmpVersion.equals(maxVersion) == false) {
            throw new BytterTradeException("流水信息不正确,不可撤销.");
        }

        if (BetterStringUtils.equals(shareholderTmp.getTmpType(), CustomerConstants.TMP_TYPE_CHANGE) == false) {
            throw new BytterTradeException("流水信息类型不正确,不可撤销.");
        }

        if (BetterStringUtils.equals(shareholderTmp.getBusinStatus(), CustomerConstants.TMP_STATUS_NEW) == false) {
            throw new BytterTradeException("流水信息状态不正确,不可撤销.");
        }

        return this.deleteByPrimaryKey(anId);
    }

    /**
     * 加载变更列表中的流水列表
     */
    public Collection<CustMechShareholderTmp> queryCustMechShareholderTmpByChangeApply(final Long anApplyId) {
        BTAssert.notNull(anApplyId, "公司编号不允许为空！");
        final CustChangeApply changeApply = changeApplyService.findChangeApply(anApplyId);

        final List<Long> tmpIds = COMMA_PATTERN.splitAsStream(changeApply.getTmpIds()).map(Long::valueOf).collect(Collectors.toList());

        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put(CustomerConstants.ID, tmpIds);

        return this.selectByProperty(conditionMap);
    }

    /**
     * 
     */
    private CustMechShareholderTmp findShareholderTmpByRefId(final Long anRefId, final String anTmpType) {
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
        final CustChangeApply changeApply = changeApplyService.addChangeApply(anCustNo, CustomerConstants.ITEM_SHAREHOLDER, tempTmpIds);

        for (final Long id : tmpIds) {
            saveShareholderTmpParentIdAndStatus(id, changeApply.getId(), CustomerConstants.TMP_STATUS_USEING);
        }

        final Collection<CustMechShareholder> shareholders = shareholderService.queryShareholder(anCustNo);
        final Collection<CustMechShareholderTmp> shareholderTmps = this.selectByProperty("parentId", changeApply.getId());

        saveNormalShareholders(shareholders, shareholderTmps, changeApply, CustomerConstants.TMP_TYPE_CHANGE);

        return changeApply;
    }

    /**
     * 存储未修改的记录
     */
    private void saveNormalShareholders(final Collection<CustMechShareholder> anShareholders,
            final Collection<CustMechShareholderTmp> anShareholderTmps, final CustChangeApply anChangeApply, final String anTmpType) {
        final Long parentId = anChangeApply.getId();

        anShareholders.stream().filter(shareholder -> checkIsNormalShareholder(shareholder, anShareholderTmps) == true).forEach(shareholder -> {
            final CustMechShareholderTmp shareholderTmp = new CustMechShareholderTmp();
            shareholderTmp.initAddValue(shareholder, CustomerConstants.TMP_STATUS_USEING);
            shareholderTmp.setRefId(shareholder.getId());
            shareholderTmp.setParentId(parentId);
            shareholderTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_NORMAL);
            shareholderTmp.setTmpType(anTmpType);
            addShareholderTmp(shareholderTmp);
        });

    }

    /**
     * 检查是否是未改变股东
     */
    private boolean checkIsNormalShareholder(final CustMechShareholder anShareholder, final Collection<CustMechShareholderTmp> anShareholderTmps) {
        boolean flag = true;
        final Long id = anShareholder.getId();
        for (final CustMechShareholderTmp shareholderTmp : anShareholderTmps) {
            final Long refId = shareholderTmp.getRefId();
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

        final List<Long> tmpIds = COMMA_PATTERN.splitAsStream(tempTmpIds).map(Long::valueOf).collect(Collectors.toList());

        for (final Long id : tmpIds) {
            saveShareholderTmpParentIdAndStatus(id, anApplyId, CustomerConstants.TMP_STATUS_USEING);
        }

        changeApplyService.saveChangeApply(anApplyId, tempTmpIds);

        final Collection<CustMechShareholder> shareholders = shareholderService.queryShareholder(custNo);
        final Collection<CustMechShareholderTmp> shareholderTmps = this.selectByProperty("parentId", anApplyId);
        saveNormalShareholders(shareholders, shareholderTmps, changeApply, CustomerConstants.TMP_TYPE_CHANGE);

        return changeApply;
    }

    /**
     * 加载未提交的变更流水列表
     */
    public Collection<CustMechShareholderTmp> queryNewChangeShareholderTmp(final Long anCustNo) {
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
    public Collection<CustMechShareholderTmp> queryChangeShareholderTmp(final Long anApplyId) {
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
    public Collection<CustMechShareholderTmp> queryInsteadShareholderTmp(final Long anInsteadRecordId) {
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
     * 添加公司股东流水信息
     */
    public CustMechShareholderTmp addShareholderTmp(final CustMechShareholderTmp anShareholderTmp) {
        BTAssert.notNull(anShareholderTmp, "公司股东流水信息不允许为空！");
        final Long custNo = anShareholderTmp.getCustNo();
        final Long version = VersionHelper.generateVersion(this.mapper, custNo);

        // anShareholderTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW, anTmpType, version);
        anShareholderTmp.setVersion(version);

        this.insert(anShareholderTmp);
        return anShareholderTmp;
    }

    /**
     * 修改公司股东流水信息
     */
    public CustMechShareholderTmp saveShareholderTmp(final CustMechShareholderTmp anShareholderTmp, final Long anId, final String anFileList) {
        BTAssert.notNull(anShareholderTmp, "公司股东流水信息不允许为空！");
        BTAssert.notNull(anId, "公司股东流水编号不允许为空！");

        final CustMechShareholderTmp tempShareholderTmp = this.selectByPrimaryKey(anId);

        tempShareholderTmp.initModifyValue(anShareholderTmp);
        tempShareholderTmp.setBatchNo(fileItemService.updateCustFileItemInfo(anFileList, tempShareholderTmp.getBatchNo()));
        return saveShareholderTmp(tempShareholderTmp);
    }

    public CustMechShareholderTmp saveShareholderTmp(final CustMechShareholderTmp anShareholderTmp) {
        BTAssert.notNull(anShareholderTmp, "公司股东流水信息不允许为空！");
        this.updateByPrimaryKeySelective(anShareholderTmp);
        return anShareholderTmp;
    }

    /**
     * 保存 parentId 和 状态
     */
    public CustMechShareholderTmp saveShareholderTmpParentIdAndStatus(final Long anId, final Long anParentId, final String anBusinStatus) {
        final CustMechShareholderTmp shareholderTmp = this.selectByPrimaryKey(anId);

        if (anParentId != null) {
            shareholderTmp.setParentId(anParentId);
        }

        shareholderTmp.setBusinStatus(anBusinStatus);
        this.updateByPrimaryKeySelective(shareholderTmp);

        return shareholderTmp;
    }

    /**
     * 添加代录流水
     * 
     * @param anInsteadRecordId
     */
    public CustMechShareholderTmp addInsteadShareholderTmp(final CustMechShareholderTmp anShareholderTmp, final Long anInsteadRecordId,
            final String anFileList) {
        BTAssert.notNull(anShareholderTmp, "公司股东流水信息不允许为空！");

        checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN,
                CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT, CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        final Long refId = anShareholderTmp.getRefId();

        final CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId);
        BTAssert.isNull(refId, "引用编号需要为空!");

        if (insteadRecord.getCustNo().equals(anShareholderTmp.getCustNo()) == false) {
            throw new BytterTradeException("客户编号不匹配!");
        }

        anShareholderTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW, CustomerConstants.TMP_TYPE_INSTEAD, null);
        anShareholderTmp.setParentId(anInsteadRecordId);
        anShareholderTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_ADD);
        anShareholderTmp.setBatchNo(fileItemService.updateCustFileItemInfo(anFileList, anShareholderTmp.getBatchNo()));
        return addShareholderTmp(anShareholderTmp);
    }

    /**
     * 添加修改代录流水
     */
    public CustMechShareholderTmp saveSaveInsteadShareholderTmp(final CustMechShareholderTmp anShareholderTmp, final Long anInsteadRecordId,
            final String anFileList) {
        BTAssert.notNull(anShareholderTmp, "公司股东流水信息不允许为空！");

        final CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW,
                CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN, CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT,
                CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        final Long refId = anShareholderTmp.getRefId();
        BTAssert.notNull(refId, "引用编号不允许为空!");

        final CustMechShareholder shareholder = shareholderService.findShareholder(refId);
        BTAssert.notNull(shareholder, "没有找到引用的记录!");

        if (insteadRecord.getCustNo().equals(anShareholderTmp.getCustNo()) == false) {
            throw new BytterTradeException("客户编号不匹配!");
        }

        final CustMechShareholderTmp tempShareholderTmp = findShareholderTmpByRefId(refId, CustomerConstants.TMP_TYPE_INSTEAD);
        if (tempShareholderTmp == null) {
            anShareholderTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW, CustomerConstants.TMP_TYPE_INSTEAD, null);
            anShareholderTmp.setParentId(anInsteadRecordId);
            anShareholderTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            anShareholderTmp.setBatchNo(
                    fileItemService.updateAndDuplicateConflictFileItemInfo(anFileList, anShareholderTmp.getBatchNo(), UserUtils.getOperatorInfo()));
            return addShareholderTmp(anShareholderTmp);
        }
        else {
            tempShareholderTmp.initModifyValue(anShareholderTmp);
            tempShareholderTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            tempShareholderTmp.setBatchNo(
                    fileItemService.updateAndDuplicateConflictFileItemInfo(anFileList, tempShareholderTmp.getBatchNo(), UserUtils.getOperatorInfo()));
            return saveShareholderTmp(tempShareholderTmp);
        }
    }

    /**
     * 添加删除代录流水
     * 
     * @param anInsteadRecordId
     */
    public CustMechShareholderTmp saveDeleteInsteadShareholderTmp(final Long anRefId, final Long anInsteadRecordId) {
        BTAssert.notNull(anRefId, "公司股东编号不允许为空！");

        final CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW,
                CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN, CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT,
                CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        final CustMechShareholder shareholder = shareholderService.findShareholder(anRefId);
        BTAssert.notNull(shareholder, "没有找到引用的记录!");

        if (insteadRecord.getCustNo().equals(shareholder.getCustNo()) == false) {
            throw new BytterTradeException("客户编号不匹配");
        }

        CustMechShareholderTmp shareholderTmp = findShareholderTmpByRefId(anRefId, CustomerConstants.TMP_TYPE_INSTEAD);
        if (shareholderTmp == null) {
            shareholderTmp = new CustMechShareholderTmp();
            shareholderTmp.initAddValue(shareholder, CustomerConstants.TMP_STATUS_NEW);
            shareholderTmp.setRefId(anRefId);
            shareholderTmp.setParentId(anInsteadRecordId);
            shareholderTmp.setTmpType(CustomerConstants.TMP_TYPE_INSTEAD);
            shareholderTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return addShareholderTmp(shareholderTmp);
        }
        else {
            shareholderTmp.initModifyValue(shareholder, CustomerConstants.TMP_STATUS_NEW);
            shareholderTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return saveShareholderTmp(shareholderTmp);
        }
    }

    /**
     * 撤销代录流水
     * 
     * @param anInsteadRecordId
     */
    public int saveCancelInsteadShareholderTmp(final Long anId, final Long anInsteadRecordId) {
        final CustMechShareholderTmp shareholderTmp = this.findShareholderTmp(anId);

        checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN,
                CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT, CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        final Long tmpVersion = shareholderTmp.getVersion();
        final Long maxVersion = VersionHelper.generateVersion(this.mapper, shareholderTmp.getCustNo());

        if (tmpVersion.equals(maxVersion) == false) {
            throw new BytterTradeException("流水信息不正确,不可撤销.");
        }

        if (BetterStringUtils.equals(shareholderTmp.getTmpType(), CustomerConstants.TMP_TYPE_INSTEAD) == false) {
            throw new BytterTradeException("流水信息类型不正确,不可撤销.");
        }

        if (BetterStringUtils.equals(shareholderTmp.getBusinStatus(), CustomerConstants.TMP_STATUS_NEW) == false) {
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
                .forEach(tmpId -> saveShareholderTmpParentIdAndStatus(tmpId, insteadRecord.getId(), CustomerConstants.TMP_STATUS_USEING));

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
                .forEach(tmpId -> saveShareholderTmpParentIdAndStatus(tmpId, insteadRecord.getId(), CustomerConstants.TMP_STATUS_USEING));

        insteadRecordService.saveInsteadRecord(anInsteadRecordId, tempTmpIds);

        return insteadRecord;
    }

    /**
     * 回写正式数据
     */
    @Override
    public void saveFormalData(final Long anId) {

        final Collection<CustMechShareholderTmp> shareholderTmps = this.selectByProperty("parentId", anId);

        for (final CustMechShareholderTmp shareholderTmp : shareholderTmps) {
            final String tmpOperType = shareholderTmp.getTmpOperType();
            switch (tmpOperType) {
            case CustomerConstants.TMP_OPER_TYPE_ADD:
                final CustMechShareholder shareholder = shareholderService.addCustMechShareholder(shareholderTmp);
                shareholderTmp.setRefId(shareholder.getId());
                this.updateByPrimaryKey(shareholderTmp);
                break;
            case CustomerConstants.TMP_OPER_TYPE_DELETE:
                shareholderService.deleteByPrimaryKey(shareholderTmp.getRefId());
                break;
            case CustomerConstants.TMP_OPER_TYPE_MODIFY:
                shareholderService.saveCustMechShareholder(shareholderTmp);
                break;
            case CustomerConstants.TMP_OPER_TYPE_NORMAL: // 不作处理
            default:
                break;
            }

            saveShareholderTmpParentIdAndStatus(shareholderTmp.getId(), null, CustomerConstants.TMP_STATUS_USED);
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
        if (BetterStringUtils.equals(insteadItem, CustomerConstants.ITEM_SHAREHOLDER) == false) {
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
        if (BetterStringUtils.equals(changeItem, CustomerConstants.ITEM_SHAREHOLDER) == false) {
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
        final Collection<CustMechShareholderTmp> ShareholderTmps = queryNewChangeShareholderTmp(anCustNo);
        if (anChangeIds.size() != ShareholderTmps.size()) {
            return false;
        }
        final Set<Long> tempSet = new HashSet<>();

        for (final CustMechShareholderTmp shareholderTmp : ShareholderTmps) {
            if (anCustNo.equals(shareholderTmp.getCustNo()) == false) {
                return false;
            }

            if (BetterStringUtils.equals(shareholderTmp.getTmpOperType(), CustomerConstants.TMP_OPER_TYPE_NORMAL) == true) {
                continue;
            }
            else {
                final Long id = shareholderTmp.getId();
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