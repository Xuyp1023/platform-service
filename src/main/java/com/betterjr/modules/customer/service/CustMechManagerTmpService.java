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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.modules.customer.constants.CustomerConstants;
import com.betterjr.modules.customer.dao.CustMechManagerTmpMapper;
import com.betterjr.modules.customer.data.ICustAuditEntityFace;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustInsteadRecord;
import com.betterjr.modules.customer.entity.CustMechManager;
import com.betterjr.modules.customer.entity.CustMechManagerTmp;
import com.betterjr.modules.customer.helper.IFormalDataService;
import com.betterjr.modules.customer.helper.VersionHelper;
import com.betterjr.modules.document.service.CustFileItemService;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechManagerTmpService extends BaseService<CustMechManagerTmpMapper, CustMechManagerTmp> implements IFormalDataService {
    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    @Resource
    private CustMechManagerService managerService;

    @Resource
    private CustInsteadRecordService insteadRecordService;

    @Resource
    private CustChangeApplyService changeApplyService;

    @Autowired
    private CustFileItemService fileItemService;

    /**
     * 查询公司高管流水信息
     */
    public CustMechManagerTmp findManagerTmp(final Long anId) {
        BTAssert.notNull(anId, "公司高管流水信息编号不允许为空！");

        final CustMechManagerTmp managerTmp = this.selectByPrimaryKey(anId);

        return managerTmp;
    }

    /**
     * 取上一版
     */
    public CustMechManagerTmp findManagerTmpPrevVersion(final CustMechManagerTmp anManagerTmp) {
        final Long custNo = anManagerTmp.getCustNo();
        final Long refId = anManagerTmp.getRefId();
        final Long version = anManagerTmp.getVersion();

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
    public CustMechManagerTmp addChangeManagerTmp(final CustMechManagerTmp anManagerTmp, final String anFileList) {
        BTAssert.notNull(anManagerTmp, "公司高管流水信息不允许为空！");

        final Long refId = anManagerTmp.getRefId();
        BTAssert.isNull(refId, "引用编号不能有值!");

        anManagerTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW, CustomerConstants.TMP_TYPE_CHANGE, null);
        anManagerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_ADD);
        anManagerTmp.setBatchNo(fileItemService.updateCustFileItemInfo(anFileList, anManagerTmp.getBatchNo()));

        return addManagerTmp(anManagerTmp);
    }

    /**
     * 添加修改变更记录
     */
    public CustMechManagerTmp saveSaveChangeManagerTmp(final CustMechManagerTmp anManagerTmp, final String anFileList) {
        BTAssert.notNull(anManagerTmp, "公司高管流水信息不允许为空！");

        final Long refId = anManagerTmp.getRefId();
        BTAssert.notNull(refId, "引用编号不允许为空!");

        final CustMechManager manager = managerService.findCustMechManager(refId);
        BTAssert.notNull(manager, "没有找到引用的记录!");

        if (manager.getCustNo().equals(anManagerTmp.getCustNo()) == false) {
            throw new BytterTradeException("客户编号不匹配!");
        }

        final CustMechManagerTmp tempManagerTmp = findManagerTmpByRefId(refId, CustomerConstants.TMP_TYPE_CHANGE);
        if (tempManagerTmp == null) {
            anManagerTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW, CustomerConstants.TMP_TYPE_CHANGE, null);
            anManagerTmp.setBusinStatus(CustomerConstants.TMP_STATUS_NEW);
            anManagerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            anManagerTmp.setBatchNo(
                    fileItemService.updateAndDuplicateConflictFileItemInfo(anFileList, anManagerTmp.getBatchNo(), UserUtils.getOperatorInfo()));
            return addManagerTmp(anManagerTmp);
        }
        else {
            tempManagerTmp.initModifyValue(anManagerTmp);
            tempManagerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            tempManagerTmp.setBatchNo(
                    fileItemService.updateAndDuplicateConflictFileItemInfo(anFileList, tempManagerTmp.getBatchNo(), UserUtils.getOperatorInfo()));
            return saveManagerTmp(tempManagerTmp);
        }
    }

    /**
     * 添加删除变更记录
     */
    public CustMechManagerTmp saveDeleteChangeManagerTmp(final Long anRefId) {
        BTAssert.notNull(anRefId, "公司高管号不允许为空！");

        final CustMechManager manager = managerService.findCustMechManager(anRefId);
        BTAssert.notNull(manager, "没有找到引用的记录!");

        CustMechManagerTmp managerTmp = findManagerTmpByRefId(anRefId, CustomerConstants.TMP_TYPE_CHANGE);
        if (managerTmp == null) {
            managerTmp = new CustMechManagerTmp();
            managerTmp.initAddValue(manager, CustomerConstants.TMP_STATUS_NEW);
            managerTmp.setRefId(anRefId);
            managerTmp.setTmpType(CustomerConstants.TMP_TYPE_CHANGE);
            managerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return addManagerTmp(managerTmp);
        }
        else {
            managerTmp.initModifyValue(manager, CustomerConstants.TMP_STATUS_NEW);
            managerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return saveManagerTmp(managerTmp);
        }
    }

    /**
     * 撤销变更记录
     */
    public int saveCancelChangeManagerTmp(final Long anId) {
        final CustMechManagerTmp managerTmp = this.findManagerTmp(anId);

        final Long tmpVersion = managerTmp.getVersion();
        final Long maxVersion = VersionHelper.generateVersion(this.mapper, managerTmp.getCustNo());

        if (tmpVersion.equals(maxVersion) == false) {
            throw new BytterTradeException("流水信息不正确,不可撤销.");
        }

        if (BetterStringUtils.equals(managerTmp.getTmpType(), CustomerConstants.TMP_TYPE_CHANGE) == false) {
            throw new BytterTradeException("流水信息类型不正确,不可撤销.");
        }

        if (BetterStringUtils.equals(managerTmp.getBusinStatus(), CustomerConstants.TMP_STATUS_NEW) == false) {
            throw new BytterTradeException("流水信息状态不正确,不可撤销.");
        }

        return this.deleteByPrimaryKey(anId);
    }

    /**
     * 加载变更列表中的流水列表
     */
    public Collection<CustMechManagerTmp> queryManagerTmpByChangeApply(final Long anApplyId) {
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
    private CustMechManagerTmp findManagerTmpByRefId(final Long anRefId, final String anTmpType) {
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
        final CustChangeApply changeApply = changeApplyService.addChangeApply(anCustNo, CustomerConstants.ITEM_MANAGER, tempTmpIds);

        for (final Long id : tmpIds) {
            saveManagerTmpParentIdAndStatus(id, changeApply.getId(), CustomerConstants.TMP_STATUS_USEING);
        }

        final Collection<CustMechManager> managers = managerService.queryCustMechManager(anCustNo);
        final Collection<CustMechManagerTmp> managerTmps = this.selectByProperty("parentId", changeApply.getId());

        saveNormalManagers(managers, managerTmps, changeApply, CustomerConstants.TMP_TYPE_CHANGE);

        return changeApply;
    }

    /**
     * 存储未修改的记录
     */
    private void saveNormalManagers(final Collection<CustMechManager> anManagers, final Collection<CustMechManagerTmp> anManagerTmps,
            final CustChangeApply anChangeApply, final String anTmpType) {
        final Long parentId = anChangeApply.getId();
        for (final CustMechManager manager : anManagers) {
            if (checkIsNormalManager(manager, anManagerTmps) == true) {
                final CustMechManagerTmp managerTmp = new CustMechManagerTmp();
                managerTmp.initAddValue(manager, CustomerConstants.TMP_STATUS_USEING);
                managerTmp.setRefId(manager.getId());
                managerTmp.setParentId(parentId);
                managerTmp.setTmpType(anTmpType);
                managerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_NORMAL);
                addManagerTmp(managerTmp);
            }
        }
    }

    /**
     * 检查是否是未改变高管
     */
    private boolean checkIsNormalManager(final CustMechManager anManager, final Collection<CustMechManagerTmp> anManagerTmps) {
        boolean flag = true;
        final Long id = anManager.getId();
        for (final CustMechManagerTmp managerTmp : anManagerTmps) {
            final Long refId = managerTmp.getRefId();
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
                .forEach(tmpId -> saveManagerTmpParentIdAndStatus(tmpId, anApplyId, CustomerConstants.TMP_STATUS_USEING));

        changeApplyService.saveChangeApply(anApplyId, tempTmpIds);

        final Collection<CustMechManager> managers = managerService.queryCustMechManager(custNo);
        final Collection<CustMechManagerTmp> managerTmps = this.selectByProperty("parentId", anApplyId);
        saveNormalManagers(managers, managerTmps, changeApply, CustomerConstants.TMP_TYPE_CHANGE);

        return changeApply;
    }

    /**
     * 加载未提交的变更流水列表
     */
    public Collection<CustMechManagerTmp> queryNewChangeManagerTmp(final Long anCustNo) {
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
    public Collection<CustMechManagerTmp> queryChangeManagerTmp(final Long anApplyId) {
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
    public Collection<CustMechManagerTmp> queryInsteadManagerTmp(final Long anInsteadRecordId) {
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
     * 添加公司高管流水信息
     */
    public CustMechManagerTmp addManagerTmp(final CustMechManagerTmp anManagerTmp) {
        BTAssert.notNull(anManagerTmp, "公司高管流水信息不允许为空！");
        final Long custNo = anManagerTmp.getCustNo();
        final Long version = VersionHelper.generateVersion(this.mapper, custNo);

        // anCustMechManagerTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW, anTmpType, version);
        anManagerTmp.setVersion(version);
        this.insert(anManagerTmp);
        return anManagerTmp;
    }

    /**
     * 修改公司高管流水信息
     */
    public CustMechManagerTmp saveManagerTmp(final CustMechManagerTmp anManagerTmp, final Long anId, final String anFileList) {
        BTAssert.notNull(anManagerTmp, "公司高管流水信息不允许为空！");
        BTAssert.notNull(anId, "公司高管流水编号不允许为空！");

        final CustMechManagerTmp tempManagerTmp = this.selectByPrimaryKey(anId);
        tempManagerTmp.initModifyValue(anManagerTmp);
        tempManagerTmp.setBatchNo(fileItemService.updateCustFileItemInfo(anFileList, tempManagerTmp.getBatchNo()));
        return saveManagerTmp(tempManagerTmp);
    }

    public CustMechManagerTmp saveManagerTmp(final CustMechManagerTmp anManagerTmp) {
        BTAssert.notNull(anManagerTmp, "公司高管流水信息不允许为空！");
        this.updateByPrimaryKeySelective(anManagerTmp);
        return anManagerTmp;
    }

    /**
     * 保存 parentId 和 状态
     */
    public CustMechManagerTmp saveManagerTmpParentIdAndStatus(final Long anId, final Long anParentId, final String anBusinStatus) {
        final CustMechManagerTmp managerTmp = this.selectByPrimaryKey(anId);

        if (anParentId != null) {
            managerTmp.setParentId(anParentId);
        }

        managerTmp.setBusinStatus(anBusinStatus);
        this.updateByPrimaryKeySelective(managerTmp);

        return managerTmp;
    }

    /**
     * 添加代录流水
     * 
     * @param anInsteadRecordId
     */
    public CustMechManagerTmp addInsteadManagerTmp(final CustMechManagerTmp anManagerTmp, final Long anInsteadRecordId, final String anFileList) {
        BTAssert.notNull(anManagerTmp, "公司高管流水信息不允许为空！");

        checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN,
                CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT, CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        final Long refId = anManagerTmp.getRefId();

        final CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId);
        BTAssert.isNull(refId, "引用编号需要为空!");

        if (insteadRecord.getCustNo().equals(anManagerTmp.getCustNo()) == false) {
            throw new BytterTradeException("客户编号不匹配!");
        }

        anManagerTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW, CustomerConstants.TMP_TYPE_INSTEAD, null);
        anManagerTmp.setParentId(anInsteadRecordId);
        anManagerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_ADD);
        anManagerTmp.setBatchNo(fileItemService.updateCustFileItemInfo(anFileList, anManagerTmp.getBatchNo()));

        return addManagerTmp(anManagerTmp);
    }

    /**
     * 添加修改代录流水
     */
    public CustMechManagerTmp saveSaveInsteadManagerTmp(final CustMechManagerTmp anManagerTmp, final Long anInsteadRecordId,
            final String anFileList) {
        BTAssert.notNull(anManagerTmp, "公司高管流水信息不允许为空！");

        final CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW,
                CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN, CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT,
                CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        final Long refId = anManagerTmp.getRefId();
        BTAssert.notNull(refId, "引用编号不允许为空!");

        final CustMechManager manager = managerService.findCustMechManager(refId);
        BTAssert.notNull(manager, "没有找到引用的记录!");

        if (insteadRecord.getCustNo().equals(anManagerTmp.getCustNo()) == false) {
            throw new BytterTradeException("客户编号不匹配!");
        }

        final CustMechManagerTmp tempManagerTmp = findManagerTmpByRefId(refId, CustomerConstants.TMP_TYPE_INSTEAD);
        if (tempManagerTmp == null) {
            anManagerTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW, CustomerConstants.TMP_TYPE_INSTEAD, null);
            anManagerTmp.setParentId(anInsteadRecordId);
            anManagerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            anManagerTmp.setBatchNo(
                    fileItemService.updateAndDuplicateConflictFileItemInfo(anFileList, anManagerTmp.getBatchNo(), UserUtils.getOperatorInfo()));
            return addManagerTmp(anManagerTmp);
        }
        else {
            tempManagerTmp.initModifyValue(anManagerTmp);
            tempManagerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            tempManagerTmp.setBatchNo(
                    fileItemService.updateAndDuplicateConflictFileItemInfo(anFileList, tempManagerTmp.getBatchNo(), UserUtils.getOperatorInfo()));
            return saveManagerTmp(tempManagerTmp);
        }
    }

    /**
     * 添加删除代录流水
     * 
     * @param anInsteadRecordId
     */
    public CustMechManagerTmp saveDeleteInsteadManagerTmp(final Long anRefId, final Long anInsteadRecordId) {
        BTAssert.notNull(anRefId, "公司高管号不允许为空！");

        final CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW,
                CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN, CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT,
                CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        final CustMechManager manager = managerService.findCustMechManager(anRefId);
        BTAssert.notNull(manager, "没有找到引用的记录!");

        if (insteadRecord.getCustNo().equals(manager.getCustNo()) == false) {
            throw new BytterTradeException("客户编号不匹配");
        }

        CustMechManagerTmp managerTmp = findManagerTmpByRefId(anRefId, CustomerConstants.TMP_TYPE_INSTEAD);
        if (managerTmp == null) {
            managerTmp = new CustMechManagerTmp();
            managerTmp.initAddValue(manager, CustomerConstants.TMP_STATUS_NEW);
            managerTmp.setRefId(anRefId);
            managerTmp.setParentId(anInsteadRecordId);
            managerTmp.setTmpType(CustomerConstants.TMP_TYPE_INSTEAD);
            managerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return addManagerTmp(managerTmp);
        }
        else {
            managerTmp.initModifyValue(manager, CustomerConstants.TMP_STATUS_NEW);
            managerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return saveManagerTmp(managerTmp);
        }
    }

    /**
     * 撤销代录流水
     * 
     * @param anInsteadRecordId
     */
    public int saveCancelInsteadManagerTmp(final Long anId, final Long anInsteadRecordId) {
        final CustMechManagerTmp managerTmp = this.findManagerTmp(anId);

        checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN,
                CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT, CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        final Long tmpVersion = managerTmp.getVersion();
        final Long maxVersion = VersionHelper.generateVersion(this.mapper, managerTmp.getCustNo());

        if (tmpVersion.equals(maxVersion) == false) {
            throw new BytterTradeException("流水信息不正确,不可撤销.");
        }

        if (BetterStringUtils.equals(managerTmp.getTmpType(), CustomerConstants.TMP_TYPE_INSTEAD) == false) {
            throw new BytterTradeException("流水信息类型不正确,不可撤销.");
        }

        if (BetterStringUtils.equals(managerTmp.getBusinStatus(), CustomerConstants.TMP_STATUS_NEW) == false) {
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
                .forEach(tmpId -> saveManagerTmpParentIdAndStatus(tmpId, insteadRecord.getId(), CustomerConstants.TMP_STATUS_USEING));

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
                .forEach(tmpId -> saveManagerTmpParentIdAndStatus(tmpId, insteadRecord.getId(), CustomerConstants.TMP_STATUS_USEING));

        insteadRecordService.saveInsteadRecord(anInsteadRecordId, tempTmpIds);

        return insteadRecord;
    }

    /**
     * 回写正式数据
     */
    @Override
    public void saveFormalData(final Long anId) {

        final Collection<CustMechManagerTmp> managerTmps = this.selectByProperty("parentId", anId);

        for (final CustMechManagerTmp managerTmp : managerTmps) {
            final String tmpOperType = managerTmp.getTmpOperType();
            switch (tmpOperType) {
            case CustomerConstants.TMP_OPER_TYPE_ADD:
                final CustMechManager manager = managerService.addCustMechManager(managerTmp);
                managerTmp.setRefId(manager.getId());
                this.updateByPrimaryKey(managerTmp);
                break;
            case CustomerConstants.TMP_OPER_TYPE_DELETE:
                managerService.deleteByPrimaryKey(managerTmp.getRefId());
                break;
            case CustomerConstants.TMP_OPER_TYPE_MODIFY:
                managerService.saveCustMechManager(managerTmp);
                break;
            case CustomerConstants.TMP_OPER_TYPE_NORMAL: // 不作处理
            default:
                break;
            }

            saveManagerTmpParentIdAndStatus(managerTmp.getId(), null, CustomerConstants.TMP_STATUS_USED);
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
        if (BetterStringUtils.equals(insteadItem, CustomerConstants.ITEM_MANAGER) == false) {
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
        if (BetterStringUtils.equals(changeItem, CustomerConstants.ITEM_MANAGER) == false) {
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
        final Collection<CustMechManagerTmp> managerTmps = queryNewChangeManagerTmp(anCustNo);
        if (anChangeIds.size() != managerTmps.size()) {
            return false;
        }
        final Set<Long> tempSet = new HashSet<>();

        for (final CustMechManagerTmp managerTmp : managerTmps) {
            if (anCustNo.equals(managerTmp.getCustNo()) == false) {
                return false;
            }

            if (BetterStringUtils.equals(managerTmp.getTmpOperType(), CustomerConstants.TMP_OPER_TYPE_NORMAL) == true) {
                continue;
            }
            else {
                final Long id = managerTmp.getId();
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