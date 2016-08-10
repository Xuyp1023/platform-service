package com.betterjr.modules.customer.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.dao.CustMechManagerTmpMapper;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustInsteadRecord;
import com.betterjr.modules.customer.entity.CustMechManager;
import com.betterjr.modules.customer.entity.CustMechManagerTmp;
import com.betterjr.modules.customer.helper.IFormalDataService;
import com.betterjr.modules.customer.helper.VersionHelper;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechManagerTmpService extends BaseService<CustMechManagerTmpMapper, CustMechManagerTmp> implements IFormalDataService {
    @Resource
    private CustMechManagerService managerService;

    @Resource
    private CustInsteadRecordService insteadRecordService;

    @Resource
    private CustChangeApplyService changeApplyService;

    /**
     * 查询公司高管流水信息
     */
    public CustMechManagerTmp findCustMechManagerTmp(Long anId) {
        BTAssert.notNull(anId, "公司高管流水信息编号不允许为空！");

        final CustMechManagerTmp managerTmp = this.selectByPrimaryKey(anId);

        return managerTmp;
    }

    /**
     * 添加新增变更流水记录
     */
    public CustMechManagerTmp addChangeManagerTmp(CustMechManagerTmp anCustMechManagerTmp) {
        BTAssert.notNull(anCustMechManagerTmp, "公司高管流水信息不允许为空！");

        final Long refId = anCustMechManagerTmp.getRefId();
        BTAssert.isNull(refId, "引用编号不能有值!");
        
        anCustMechManagerTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW);
        anCustMechManagerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_ADD);

        return addManagerTmp(anCustMechManagerTmp, CustomerConstants.TMP_TYPE_CHANGE);
    }

    /**
     * 添加修改变更记录
     */
    public CustMechManagerTmp saveSaveChangeManagerTmp(CustMechManagerTmp anCustMechManagerTmp) {
        BTAssert.notNull(anCustMechManagerTmp, "公司高管流水信息不允许为空！");

        final Long refId = anCustMechManagerTmp.getRefId();
        BTAssert.notNull(refId, "引用编号不允许为空!");

        CustMechManager manager = managerService.findCustMechManager(refId);
        BTAssert.notNull(manager, "没有找到引用的记录!");
        
        if (manager.getCustNo().equals(anCustMechManagerTmp.getCustNo()) == false) {
            throw new BytterTradeException("客户编号不匹配!");
        }

        CustMechManagerTmp tempManagerTmp = findManagerTmpByRefId(refId, CustomerConstants.TMP_TYPE_CHANGE);
        if (tempManagerTmp == null) {
            anCustMechManagerTmp.setBusinStatus(CustomerConstants.TMP_STATUS_NEW);
            anCustMechManagerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            return addManagerTmp(anCustMechManagerTmp, CustomerConstants.TMP_TYPE_CHANGE);
        }
        else {
            tempManagerTmp.initModifyValue(anCustMechManagerTmp);
            tempManagerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            return saveManagerTmp(tempManagerTmp, tempManagerTmp.getId());
        }
    }

    /**
     * 添加删除变更记录
     */
    public CustMechManagerTmp saveDelChangeManagerTmp(Long anRefId) {
        BTAssert.notNull(anRefId, "公司高管号不允许为空！");

        CustMechManager manager = managerService.findCustMechManager(anRefId);
        BTAssert.notNull(manager, "没有找到引用的记录!");
        
        CustMechManagerTmp managerTmp = findManagerTmpByRefId(anRefId, CustomerConstants.TMP_TYPE_CHANGE);
        if (managerTmp == null) {
            managerTmp = new CustMechManagerTmp();
            managerTmp.initAddValue(manager, CustomerConstants.TMP_STATUS_NEW);
            managerTmp.setRefId(anRefId);
            managerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return addManagerTmp(managerTmp, CustomerConstants.TMP_TYPE_CHANGE);
        }
        else {
            managerTmp.initModifyValue(manager, CustomerConstants.TMP_STATUS_NEW);
            managerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return saveManagerTmp(managerTmp, managerTmp.getId());
        }
    }

    /**
     * 撤销变更记录
     */
    public int saveCancelChangeManagerTmp(Long anId) {
        CustMechManagerTmp managerTmp = this.findCustMechManagerTmp(anId);

        Long tmpVersion = managerTmp.getVersion();
        Long maxVersion = VersionHelper.generateVersion(this.mapper, managerTmp.getCustNo());

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
    public Collection<CustMechManagerTmp> queryCustMechManagerTmpByChangeApply(Long anApplyId) {
        BTAssert.notNull(anApplyId, "公司编号不允许为空！");
        CustChangeApply changeApply = changeApplyService.findChangeApply(anApplyId);
        List<String> strTmpIds = Arrays.asList(BetterStringUtils.split(changeApply.getTmpIds(), ","));

        List<Long> tmpIds = new ArrayList<>();
        strTmpIds.forEach(strTmpId -> tmpIds.add(Long.valueOf(strTmpId)));

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put(CustomerConstants.ID, tmpIds.toArray(new Long[tmpIds.size()]));

        return this.selectByProperty(conditionMap);
    }

    /**
     * 
     */
    private CustMechManagerTmp findManagerTmpByRefId(Long anRefId, String anTmpType) {
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

        List<String> strTmpIds = Arrays.asList(BetterStringUtils.split(tempTmpIds, ","));

        List<Long> tmpIds = new ArrayList<>();

        strTmpIds.forEach(strTmpId -> tmpIds.add(Long.valueOf(strTmpId)));

        if (checkMatchNewChange(tmpIds.toArray(new Long[tmpIds.size()]), anCustNo) == false) {
            throw new BytterTradeException("代录编号列表不正确,请检查.");
        }
        CustChangeApply changeApply = changeApplyService.addChangeApply(anCustNo, CustomerConstants.ITEM_MANAGER, tempTmpIds);

        for (Long id : tmpIds) {
            saveManagerTmpParentIdAndStatus(id, changeApply.getId(), CustomerConstants.TMP_STATUS_USEING);
        }

        Collection<CustMechManager> managers = managerService.queryCustMechManager(anCustNo);
        Collection<CustMechManagerTmp> managerTmps = this.selectByProperty("parentId", changeApply.getId());

        saveNormalManagers(managers, managerTmps, changeApply, CustomerConstants.TMP_TYPE_CHANGE);

        return changeApply;
    }

    /**
     * 存储未修改的记录
     */
    private void saveNormalManagers(Collection<CustMechManager> anManagers, Collection<CustMechManagerTmp> anManagerTmps,
            CustChangeApply anChangeApply, String anTmpType) {
        Long parentId = anChangeApply.getId();
        for (CustMechManager manager : anManagers) {
            if (checkIsNormalManager(manager, anManagerTmps) == true) {
                CustMechManagerTmp managerTmp = new CustMechManagerTmp();
                managerTmp.initAddValue(manager, CustomerConstants.TMP_STATUS_USEING);
                managerTmp.setRefId(manager.getId());
                managerTmp.setParentId(parentId);
                managerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_NORMAL);
                addManagerTmp(managerTmp, anTmpType);
            }
        }
    }

    /**
     * 检查是否是未改变高管
     */
    private boolean checkIsNormalManager(CustMechManager anManager, Collection<CustMechManagerTmp> anManagerTmps) {
        boolean flag = true;
        Long id = anManager.getId();
        for (CustMechManagerTmp managerTmp : anManagerTmps) {
            Long refId = managerTmp.getRefId();
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

        List<String> strTmpIds = Arrays.asList(BetterStringUtils.split(tempTmpIds, ","));

        List<Long> tmpIds = new ArrayList<>();

        strTmpIds.forEach(strTmpId -> tmpIds.add(Long.valueOf(strTmpId)));

        for (Long id : tmpIds) {
            saveManagerTmpParentIdAndStatus(id, anApplyId, CustomerConstants.TMP_STATUS_USEING);
        }

        changeApplyService.saveChangeApply(anApplyId, tempTmpIds);

        Collection<CustMechManager> managers = managerService.queryCustMechManager(custNo);
        Collection<CustMechManagerTmp> managerTmps = this.selectByProperty("parentId", anApplyId);
        saveNormalManagers(managers, managerTmps, changeApply, CustomerConstants.TMP_TYPE_CHANGE);

        return changeApply;
    }

    /**
     * 加载未提交的变更流水列表
     */
    public Collection<CustMechManagerTmp> queryNewChangeCustMechManagerTmp(Long anCustNo) {
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
    public Collection<CustMechManagerTmp> queryChangeCustMechManagerTmp(Long anApplyId) {
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
    public Collection<CustMechManagerTmp> queryInsteadManagerTmp(Long anInsteadRecordId) {
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
     * 添加公司高管流水信息
     */
    public CustMechManagerTmp addManagerTmp(CustMechManagerTmp anCustMechManagerTmp, String anTmpType) {
        BTAssert.notNull(anCustMechManagerTmp, "公司高管流水信息不允许为空！");
        Long custNo = anCustMechManagerTmp.getCustNo();
        Long version = VersionHelper.generateVersion(this.mapper, custNo);

        anCustMechManagerTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW, anTmpType, version);
        this.insert(anCustMechManagerTmp);
        return anCustMechManagerTmp;
    }

    /**
     * 修改公司高管流水信息
     */
    public CustMechManagerTmp saveManagerTmp(CustMechManagerTmp anCustMechManagerTmp, Long anId) {
        BTAssert.notNull(anCustMechManagerTmp, "公司高管流水信息不允许为空！");
        BTAssert.notNull(anId, "公司高管流水编号不允许为空！");

        CustMechManagerTmp tempCustMechManagerTmp = this.selectByPrimaryKey(anId);

        tempCustMechManagerTmp.initModifyValue(anCustMechManagerTmp);

        this.updateByPrimaryKeySelective(tempCustMechManagerTmp);

        return tempCustMechManagerTmp;
    }

    /**
     * 保存 parentId 和 状态
     */
    public CustMechManagerTmp saveManagerTmpParentIdAndStatus(Long anId, Long anParentId, String anBusinStatus) {
        CustMechManagerTmp managerTmp = this.selectByPrimaryKey(anId);

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
    public CustMechManagerTmp addInsteadManagerTmp(CustMechManagerTmp anCustMechManagerTmp, Long anInsteadRecordId) {
        BTAssert.notNull(anCustMechManagerTmp, "公司高管流水信息不允许为空！");

        checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN,
                CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT, CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        final Long refId = anCustMechManagerTmp.getRefId();

        CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId);
        BTAssert.isNull(refId, "引用编号需要为空!");
        
        if (insteadRecord.getCustNo().equals(anCustMechManagerTmp.getCustNo()) == false) {
            throw new BytterTradeException("客户编号不匹配!");
        }

        anCustMechManagerTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW);
        anCustMechManagerTmp.setParentId(anInsteadRecordId);
        anCustMechManagerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_ADD);

        return addManagerTmp(anCustMechManagerTmp, CustomerConstants.TMP_TYPE_INSTEAD);
    }

    /**
     * 添加修改代录流水
     */
    public CustMechManagerTmp saveSaveInsteadManagerTmp(CustMechManagerTmp anCustMechManagerTmp, Long anInsteadRecordId) {
        BTAssert.notNull(anCustMechManagerTmp, "公司高管流水信息不允许为空！");

        CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW,
                CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN, CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT,
                CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        final Long refId = anCustMechManagerTmp.getRefId();
        BTAssert.notNull(refId, "引用编号不允许为空!");

        CustMechManager manager = managerService.findCustMechManager(refId);
        BTAssert.notNull(manager, "没有找到引用的记录!");
        
        if (insteadRecord.getCustNo().equals(anCustMechManagerTmp.getCustNo()) == false) {
            throw new BytterTradeException("客户编号不匹配!");
        }

        CustMechManagerTmp tempManagerTmp = findManagerTmpByRefId(refId, CustomerConstants.TMP_TYPE_INSTEAD);
        if (tempManagerTmp == null) {
            anCustMechManagerTmp.setParentId(anInsteadRecordId);
            anCustMechManagerTmp.setBusinStatus(CustomerConstants.TMP_STATUS_NEW);
            anCustMechManagerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            return addManagerTmp(anCustMechManagerTmp, CustomerConstants.TMP_TYPE_INSTEAD);
        }
        else {
            tempManagerTmp.initModifyValue(anCustMechManagerTmp);
            tempManagerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            return saveManagerTmp(tempManagerTmp, tempManagerTmp.getId());
        }
    }

    /**
     * 添加删除代录流水
     * 
     * @param anInsteadRecordId
     */
    public CustMechManagerTmp saveDelInsteadManagerTmp(Long anRefId, Long anInsteadRecordId) {
        BTAssert.notNull(anRefId, "公司高管号不允许为空！");

        CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW,
                CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN, CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT,
                CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        CustMechManager manager = managerService.findCustMechManager(anRefId);
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
            managerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return addManagerTmp(managerTmp, CustomerConstants.TMP_TYPE_INSTEAD);
        }
        else {
            managerTmp.initAddValue(manager, CustomerConstants.TMP_STATUS_NEW);
            managerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return saveManagerTmp(managerTmp, managerTmp.getId());
        }
    }

    /**
     * 撤销代录流水
     * 
     * @param anInsteadRecordId
     */
    public int saveCancelInsteadManagerTmp(Long anId, Long anInsteadRecordId) {
        CustMechManagerTmp managerTmp = this.findCustMechManagerTmp(anId);

        checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN,
                CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT, CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        Long tmpVersion = managerTmp.getVersion();
        Long maxVersion = VersionHelper.generateVersion(this.mapper, managerTmp.getCustNo());

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
    public CustInsteadRecord addInsteadRecord(Map<String, Object> anParam, Long anInsteadRecordId) {
        BTAssert.notNull(anInsteadRecordId, "代录编号不允许为空！");
        BTAssert.notNull(anParam, "参数不允许为空！");

        CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW);

        String tempTmpIds = (String) anParam.get("tmpIds");

        List<String> strTmpIds = Arrays.asList(BetterStringUtils.split(tempTmpIds, ","));

        List<Long> tmpIds = new ArrayList<>();

        strTmpIds.forEach(strTmpId -> tmpIds.add(Long.valueOf(strTmpId)));

        for (Long id : tmpIds) {
            CustMechManagerTmp managerTmp = saveManagerTmpParentIdAndStatus(id, insteadRecord.getId(), CustomerConstants.TMP_STATUS_USEING);
        }

        insteadRecordService.saveCustInsteadRecordStatus(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN);

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

        List<String> strTmpIds = Arrays.asList(BetterStringUtils.split(tempTmpIds, ","));

        List<Long> tmpIds = new ArrayList<>();

        strTmpIds.forEach(strTmpId -> tmpIds.add(Long.valueOf(strTmpId)));

        for (Long id : tmpIds) {
            saveManagerTmpParentIdAndStatus(id, insteadRecord.getId(), CustomerConstants.TMP_STATUS_USEING);
        }

        insteadRecordService.saveCustInsteadRecord(anInsteadRecordId, tempTmpIds);

        return insteadRecord;
    }

    /**
     * 回写正式数据
     */
    @Override
    public void saveFormalData(Long anId) {

        Collection<CustMechManagerTmp> managerTmps = this.selectByProperty("parentId", anId);

        for (CustMechManagerTmp managerTmp : managerTmps) {
            String tmpOperType = managerTmp.getTmpOperType();
            switch (tmpOperType) {
            case CustomerConstants.TMP_OPER_TYPE_ADD:
                managerService.addCustMechManager(managerTmp);
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
    public void saveCancelData(Long anId) {

    }

    /**
     * 检查并返回代录记录
     */
    private CustInsteadRecord checkInsteadRecord(Long anInsteadRecordId, String... anBusinStatus) {
        BTAssert.notNull(anInsteadRecordId, "代录记录编号不允许为空！");

        CustInsteadRecord insteadRecord = insteadRecordService.findCustInsteadRecord(anInsteadRecordId);
        BTAssert.notNull(insteadRecord, "没有找到对应的代录记录");

        String insteadItem = insteadRecord.getInsteadItem();
        if (BetterStringUtils.equals(insteadItem, CustomerConstants.ITEM_MANAGER) == false) {
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
        if (BetterStringUtils.equals(changeItem, CustomerConstants.ITEM_MANAGER) == false) {
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
    private boolean checkMatchNewChange(Long[] anChangeIds, Long anCustNo) {
        Collection<CustMechManagerTmp> managerTmps = queryNewChangeCustMechManagerTmp(anCustNo);
        if (anChangeIds.length != managerTmps.size()) {
            return false;
        }
        Set<Long> tempSet = new HashSet<>();

        for (CustMechManagerTmp managerTmp : managerTmps) {
            if (anCustNo.equals(managerTmp.getCustNo()) == false) {
                return false;
            }
            
            if (BetterStringUtils.equals(managerTmp.getTmpOperType(), CustomerConstants.TMP_OPER_TYPE_NORMAL) == true) {
                continue;
            }
            Long id = managerTmp.getId();
            for (Long changeId : anChangeIds) {
                if (id.equals(changeId) == true) {
                    tempSet.add(changeId);
                }
            }
        }

        if (tempSet.size() == anChangeIds.length) {
            return true;
        }
        else {
            return false;
        }
    }
}