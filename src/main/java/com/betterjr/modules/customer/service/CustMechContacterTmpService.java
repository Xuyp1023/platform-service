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
import com.betterjr.modules.customer.dao.CustMechContacterTmpMapper;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustInsteadRecord;
import com.betterjr.modules.customer.entity.CustMechContacter;
import com.betterjr.modules.customer.entity.CustMechContacterTmp;
import com.betterjr.modules.customer.helper.IFormalDataService;
import com.betterjr.modules.customer.helper.VersionHelper;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechContacterTmpService extends BaseService<CustMechContacterTmpMapper, CustMechContacterTmp> implements IFormalDataService {
    @Resource
    private CustMechContacterService contacterService;

    @Resource
    private CustInsteadRecordService insteadRecordService;

    @Resource
    private CustChangeApplyService changeApplyService;

    /**
     * 查询公司联系人流水信息
     */
    public CustMechContacterTmp findCustMechContacterTmp(Long anId) {
        BTAssert.notNull(anId, "公司联系人流水信息编号不允许为空！");

        final CustMechContacterTmp contacterTmp = this.selectByPrimaryKey(anId);

        return contacterTmp;
    }

    /**
     * 取上一版
     */
    public CustMechContacterTmp findCustMechContacterTmpPrevVersion(CustMechContacterTmp anContacterTmp) {
        Long custNo = anContacterTmp.getCustNo();
        Long refId = anContacterTmp.getRefId();
        Long version = anContacterTmp.getVersion();

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
    public CustMechContacterTmp addChangeContacterTmp(CustMechContacterTmp anCustMechContacterTmp) {
        BTAssert.notNull(anCustMechContacterTmp, "公司联系人流水信息不允许为空！");

        final Long refId = anCustMechContacterTmp.getRefId();
        BTAssert.isNull(refId, "引用编号不能有值!");

        anCustMechContacterTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW);
        anCustMechContacterTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_ADD);

        return addContacterTmp(anCustMechContacterTmp, CustomerConstants.TMP_TYPE_CHANGE);
    }

    /**
     * 添加修改变更记录
     */
    public CustMechContacterTmp saveSaveChangeContacterTmp(CustMechContacterTmp anCustMechContacterTmp) {
        BTAssert.notNull(anCustMechContacterTmp, "公司联系人流水信息不允许为空！");

        final Long refId = anCustMechContacterTmp.getRefId();
        BTAssert.notNull(refId, "引用编号不允许为空!");

        CustMechContacter contacter = contacterService.findCustMechContacter(refId);
        BTAssert.notNull(contacter, "没有找到引用的记录!");

        if (contacter.getCustNo().equals(anCustMechContacterTmp.getCustNo()) == false) {
            throw new BytterTradeException("客户编号不匹配!");
        }

        CustMechContacterTmp tempContacterTmp = findContacterTmpByRefId(refId, CustomerConstants.TMP_TYPE_CHANGE);
        if (tempContacterTmp == null) {
            anCustMechContacterTmp.setBusinStatus(CustomerConstants.TMP_STATUS_NEW);
            anCustMechContacterTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            return addContacterTmp(anCustMechContacterTmp, CustomerConstants.TMP_TYPE_CHANGE);
        }
        else {
            tempContacterTmp.initModifyValue(anCustMechContacterTmp);
            tempContacterTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            return saveContacterTmp(tempContacterTmp, tempContacterTmp.getId());
        }
    }

    /**
     * 添加删除变更记录
     */
    public CustMechContacterTmp saveDeleteChangeContacterTmp(Long anRefId) {
        BTAssert.notNull(anRefId, "公司联系人号不允许为空！");

        CustMechContacter contacter = contacterService.findCustMechContacter(anRefId);
        BTAssert.notNull(contacter, "没有找到引用的记录!");

        CustMechContacterTmp contacterTmp = findContacterTmpByRefId(anRefId, CustomerConstants.TMP_TYPE_CHANGE);
        if (contacterTmp == null) {
            contacterTmp = new CustMechContacterTmp();
            contacterTmp.initAddValue(contacter, CustomerConstants.TMP_STATUS_NEW);
            contacterTmp.setRefId(anRefId);
            contacterTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return addContacterTmp(contacterTmp, CustomerConstants.TMP_TYPE_CHANGE);
        }
        else {
            contacterTmp.initModifyValue(contacter, CustomerConstants.TMP_STATUS_NEW);
            contacterTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return saveContacterTmp(contacterTmp, contacterTmp.getId());
        }
    }

    /**
     * 撤销变更记录
     */
    public int saveCancelChangeContacterTmp(Long anId) {
        CustMechContacterTmp contacterTmp = this.findCustMechContacterTmp(anId);

        Long tmpVersion = contacterTmp.getVersion();
        Long maxVersion = VersionHelper.generateVersion(this.mapper, contacterTmp.getCustNo());

        if (tmpVersion.equals(maxVersion) == false) {
            throw new BytterTradeException("流水信息不正确,不可撤销.");
        }

        if (BetterStringUtils.equals(contacterTmp.getTmpType(), CustomerConstants.TMP_TYPE_CHANGE) == false) {
            throw new BytterTradeException("流水信息类型不正确,不可撤销.");
        }

        if (BetterStringUtils.equals(contacterTmp.getBusinStatus(), CustomerConstants.TMP_STATUS_NEW) == false) {
            throw new BytterTradeException("流水信息状态不正确,不可撤销.");
        }

        return this.deleteByPrimaryKey(anId);
    }

    /**
     * 加载变更列表中的流水列表
     */
    public Collection<CustMechContacterTmp> queryCustMechContacterTmpByChangeApply(Long anApplyId) {
        BTAssert.notNull(anApplyId, "公司编号不允许为空！");
        CustChangeApply changeApply = changeApplyService.findChangeApply(anApplyId);

        long[] tmpIds = Pattern.compile(",").splitAsStream(changeApply.getTmpIds()).mapToLong(Long::valueOf).toArray();

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put(CustomerConstants.ID, tmpIds);

        return this.selectByProperty(conditionMap);
    }

    /**
     * 
     */
    private CustMechContacterTmp findContacterTmpByRefId(Long anRefId, String anTmpType) {
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

        List<Long> tmpIds = Pattern.compile(",").splitAsStream(tempTmpIds).map(Long::valueOf).collect(Collectors.toList());

        if (checkMatchNewChange(tmpIds, anCustNo) == false) {
            throw new BytterTradeException("代录编号列表不正确,请检查.");
        }
        CustChangeApply changeApply = changeApplyService.addChangeApply(anCustNo, CustomerConstants.ITEM_CONTACTER, tempTmpIds);

        for (Long id : tmpIds) {
            saveContacterTmpParentIdAndStatus(id, changeApply.getId(), CustomerConstants.TMP_STATUS_USEING);
        }

        Collection<CustMechContacter> contacters = contacterService.queryCustMechContacter(anCustNo);
        Collection<CustMechContacterTmp> contacterTmps = this.selectByProperty("parentId", changeApply.getId());

        saveNormalContacters(contacters, contacterTmps, changeApply, CustomerConstants.TMP_TYPE_CHANGE);

        return changeApply;
    }

    /**
     * 存储未修改的记录
     */
    private void saveNormalContacters(Collection<CustMechContacter> anContacters, Collection<CustMechContacterTmp> anContacterTmps,
            CustChangeApply anChangeApply, String anTmpType) {
        Long parentId = anChangeApply.getId();
        for (CustMechContacter contacter : anContacters) {
            if (checkIsNormalContacter(contacter, anContacterTmps) == true) {
                CustMechContacterTmp contacterTmp = new CustMechContacterTmp();
                contacterTmp.initAddValue(contacter, CustomerConstants.TMP_STATUS_USEING);
                contacterTmp.setRefId(contacter.getId());
                contacterTmp.setParentId(parentId);
                contacterTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_NORMAL);
                addContacterTmp(contacterTmp, anTmpType);
            }
        }
    }

    /**
     * 检查是否是未改变联系人
     */
    private boolean checkIsNormalContacter(CustMechContacter anContacter, Collection<CustMechContacterTmp> anContacterTmps) {
        boolean flag = true;
        Long id = anContacter.getId();
        for (CustMechContacterTmp contacterTmp : anContacterTmps) {
            Long refId = contacterTmp.getRefId();
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

        Pattern.compile(",").splitAsStream(tempTmpIds).map(Long::valueOf)
                .forEach(tmpId -> saveContacterTmpParentIdAndStatus(tmpId, anApplyId, CustomerConstants.TMP_STATUS_USEING));

        changeApplyService.saveChangeApply(anApplyId, tempTmpIds);

        Collection<CustMechContacter> contacters = contacterService.queryCustMechContacter(custNo);
        Collection<CustMechContacterTmp> contacterTmps = this.selectByProperty("parentId", anApplyId);
        saveNormalContacters(contacters, contacterTmps, changeApply, CustomerConstants.TMP_TYPE_CHANGE);

        return changeApply;
    }

    /**
     * 加载未提交的变更流水列表
     */
    public Collection<CustMechContacterTmp> queryNewChangeCustMechContacterTmp(Long anCustNo) {
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
    public Collection<CustMechContacterTmp> queryChangeCustMechContacterTmp(Long anApplyId) {
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
    public Collection<CustMechContacterTmp> queryInsteadContacterTmp(Long anInsteadRecordId) {
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
     * 添加公司联系人流水信息
     */
    public CustMechContacterTmp addContacterTmp(CustMechContacterTmp anCustMechContacterTmp, String anTmpType) {
        BTAssert.notNull(anCustMechContacterTmp, "公司联系人流水信息不允许为空！");
        Long custNo = anCustMechContacterTmp.getCustNo();
        Long version = VersionHelper.generateVersion(this.mapper, custNo);

        anCustMechContacterTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW, anTmpType, version);
        this.insert(anCustMechContacterTmp);
        return anCustMechContacterTmp;
    }

    /**
     * 修改公司联系人流水信息
     */
    public CustMechContacterTmp saveContacterTmp(CustMechContacterTmp anCustMechContacterTmp, Long anId) {
        BTAssert.notNull(anCustMechContacterTmp, "公司联系人流水信息不允许为空！");
        BTAssert.notNull(anId, "公司联系人流水编号不允许为空！");

        CustMechContacterTmp tempCustMechContacterTmp = this.selectByPrimaryKey(anId);

        tempCustMechContacterTmp.initModifyValue(anCustMechContacterTmp);

        this.updateByPrimaryKeySelective(tempCustMechContacterTmp);

        return tempCustMechContacterTmp;
    }

    /**
     * 保存 parentId 和 状态
     */
    public CustMechContacterTmp saveContacterTmpParentIdAndStatus(Long anId, Long anParentId, String anBusinStatus) {
        CustMechContacterTmp contacterTmp = this.selectByPrimaryKey(anId);

        if (anParentId != null) {
            contacterTmp.setParentId(anParentId);
        }

        contacterTmp.setBusinStatus(anBusinStatus);

        this.updateByPrimaryKeySelective(contacterTmp);

        return contacterTmp;
    }

    /**
     * 添加代录流水
     * 
     * @param anInsteadRecordId
     */
    public CustMechContacterTmp addInsteadContacterTmp(CustMechContacterTmp anCustMechContacterTmp, Long anInsteadRecordId) {
        BTAssert.notNull(anCustMechContacterTmp, "公司联系人流水信息不允许为空！");

        checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN,
                CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT, CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        final Long refId = anCustMechContacterTmp.getRefId();

        CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId);
        BTAssert.isNull(refId, "引用编号需要为空!");

        if (insteadRecord.getCustNo().equals(anCustMechContacterTmp.getCustNo()) == false) {
            throw new BytterTradeException("客户编号不匹配!");
        }

        anCustMechContacterTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW);
        anCustMechContacterTmp.setParentId(anInsteadRecordId);
        anCustMechContacterTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_ADD);

        return addContacterTmp(anCustMechContacterTmp, CustomerConstants.TMP_TYPE_INSTEAD);
    }

    /**
     * 添加修改代录流水
     */
    public CustMechContacterTmp saveSaveInsteadContacterTmp(CustMechContacterTmp anCustMechContacterTmp, Long anInsteadRecordId) {
        BTAssert.notNull(anCustMechContacterTmp, "公司联系人流水信息不允许为空！");

        CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW,
                CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN, CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT,
                CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        final Long refId = anCustMechContacterTmp.getRefId();
        BTAssert.notNull(refId, "引用编号不允许为空!");

        CustMechContacter contacter = contacterService.findCustMechContacter(refId);
        BTAssert.notNull(contacter, "没有找到引用的记录!");

        if (insteadRecord.getCustNo().equals(anCustMechContacterTmp.getCustNo()) == false) {
            throw new BytterTradeException("客户编号不匹配!");
        }

        CustMechContacterTmp tempContacterTmp = findContacterTmpByRefId(refId, CustomerConstants.TMP_TYPE_INSTEAD);
        if (tempContacterTmp == null) {
            anCustMechContacterTmp.setParentId(anInsteadRecordId);
            anCustMechContacterTmp.setBusinStatus(CustomerConstants.TMP_STATUS_NEW);
            anCustMechContacterTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            return addContacterTmp(anCustMechContacterTmp, CustomerConstants.TMP_TYPE_INSTEAD);
        }
        else {
            tempContacterTmp.initModifyValue(anCustMechContacterTmp);
            tempContacterTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            return saveContacterTmp(tempContacterTmp, tempContacterTmp.getId());
        }
    }

    /**
     * 添加删除代录流水
     * 
     * @param anInsteadRecordId
     */
    public CustMechContacterTmp saveDeleteInsteadContacterTmp(Long anRefId, Long anInsteadRecordId) {
        BTAssert.notNull(anRefId, "公司联系人号不允许为空！");

        CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW,
                CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN, CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT,
                CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        CustMechContacter contacter = contacterService.findCustMechContacter(anRefId);
        BTAssert.notNull(contacter, "没有找到引用的记录!");

        if (insteadRecord.getCustNo().equals(contacter.getCustNo()) == false) {
            throw new BytterTradeException("客户编号不匹配");
        }

        CustMechContacterTmp contacterTmp = findContacterTmpByRefId(anRefId, CustomerConstants.TMP_TYPE_INSTEAD);
        if (contacterTmp == null) {
            contacterTmp = new CustMechContacterTmp();
            contacterTmp.initAddValue(contacter, CustomerConstants.TMP_STATUS_NEW);
            contacterTmp.setRefId(anRefId);
            contacterTmp.setParentId(anInsteadRecordId);
            contacterTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return addContacterTmp(contacterTmp, CustomerConstants.TMP_TYPE_INSTEAD);
        }
        else {
            contacterTmp.initModifyValue(contacter, CustomerConstants.TMP_STATUS_NEW);
            contacterTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return saveContacterTmp(contacterTmp, contacterTmp.getId());
        }
    }

    /**
     * 撤销代录流水
     * 
     * @param anInsteadRecordId
     */
    public int saveCancelInsteadContacterTmp(Long anId, Long anInsteadRecordId) {
        CustMechContacterTmp contacterTmp = this.findCustMechContacterTmp(anId);

        checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN,
                CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT, CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        Long tmpVersion = contacterTmp.getVersion();
        Long maxVersion = VersionHelper.generateVersion(this.mapper, contacterTmp.getCustNo());

        if (tmpVersion.equals(maxVersion) == false) {
            throw new BytterTradeException("流水信息不正确,不可撤销.");
        }

        if (BetterStringUtils.equals(contacterTmp.getTmpType(), CustomerConstants.TMP_TYPE_INSTEAD) == false) {
            throw new BytterTradeException("流水信息类型不正确,不可撤销.");
        }

        if (BetterStringUtils.equals(contacterTmp.getBusinStatus(), CustomerConstants.TMP_STATUS_NEW) == false) {
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

        Pattern.compile(",").splitAsStream(tempTmpIds).map(Long::valueOf)
                .forEach(tmpId -> saveContacterTmpParentIdAndStatus(tmpId, insteadRecord.getId(), CustomerConstants.TMP_STATUS_USEING));

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

        Pattern.compile(",").splitAsStream(tempTmpIds).map(Long::valueOf)
                .forEach(tmpId -> saveContacterTmpParentIdAndStatus(tmpId, insteadRecord.getId(), CustomerConstants.TMP_STATUS_USEING));

        insteadRecordService.saveCustInsteadRecord(anInsteadRecordId, tempTmpIds);

        return insteadRecord;
    }

    /**
     * 回写正式数据
     */
    @Override
    public void saveFormalData(Long anId) {

        Collection<CustMechContacterTmp> contacterTmps = this.selectByProperty("parentId", anId);

        for (CustMechContacterTmp contacterTmp : contacterTmps) {
            String tmpOperType = contacterTmp.getTmpOperType();
            switch (tmpOperType) {
            case CustomerConstants.TMP_OPER_TYPE_ADD:
                CustMechContacter contacter = contacterService.addCustMechContacter(contacterTmp);
                contacterTmp.setRefId(contacter.getId());
                this.updateByPrimaryKey(contacterTmp);
                break;
            case CustomerConstants.TMP_OPER_TYPE_DELETE:
                contacterService.deleteByPrimaryKey(contacterTmp.getRefId());
                break;
            case CustomerConstants.TMP_OPER_TYPE_MODIFY:
                contacterService.saveCustMechContacter(contacterTmp);
                break;
            case CustomerConstants.TMP_OPER_TYPE_NORMAL: // 不作处理
            default:
                break;
            }

            saveContacterTmpParentIdAndStatus(contacterTmp.getId(), null, CustomerConstants.TMP_STATUS_USED);
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
        if (BetterStringUtils.equals(insteadItem, CustomerConstants.ITEM_CONTACTER) == false) {
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
        if (BetterStringUtils.equals(changeItem, CustomerConstants.ITEM_CONTACTER) == false) {
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
        Collection<CustMechContacterTmp> contacterTmps = queryNewChangeCustMechContacterTmp(anCustNo);
        if (anChangeIds.size() != contacterTmps.size()) {
            return false;
        }
        Set<Long> tempSet = new HashSet<>();

        for (CustMechContacterTmp contacterTmp : contacterTmps) {
            if (anCustNo.equals(contacterTmp.getCustNo()) == false) {
                return false;
            }

            if (BetterStringUtils.equals(contacterTmp.getTmpOperType(), CustomerConstants.TMP_OPER_TYPE_NORMAL) == true) {
                continue;
            }
            Long id = contacterTmp.getId();
            for (Long changeId : anChangeIds) {
                if (id.equals(changeId) == true) {
                    tempSet.add(changeId);
                }
            }
        }

        if (tempSet.size() == anChangeIds.size()) {
            return true;
        }
        else {
            return false;
        }
    }
}