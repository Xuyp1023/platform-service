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

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.modules.customer.constants.CustomerConstants;
import com.betterjr.modules.customer.dao.CustMechContacterTmpMapper;
import com.betterjr.modules.customer.data.ICustAuditEntityFace;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustInsteadRecord;
import com.betterjr.modules.customer.entity.CustMechContacter;
import com.betterjr.modules.customer.entity.CustMechContacterTmp;
import com.betterjr.modules.customer.helper.IFormalDataService;
import com.betterjr.modules.customer.helper.VersionHelper;
import com.betterjr.modules.document.service.CustFileItemService;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechContacterTmpService extends BaseService<CustMechContacterTmpMapper, CustMechContacterTmp>
        implements IFormalDataService {
    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    @Resource
    private CustMechContacterService contacterService;

    @Resource
    private CustInsteadRecordService insteadRecordService;

    @Resource
    private CustChangeApplyService changeApplyService;

    @Autowired
    private CustFileItemService fileItemService;

    /**
     * 查询公司联系人流水信息
     */
    public CustMechContacterTmp findContacterTmp(final Long anId) {
        BTAssert.notNull(anId, "公司联系人流水信息编号不允许为空！");

        final CustMechContacterTmp contacterTmp = this.selectByPrimaryKey(anId);

        return contacterTmp;
    }

    /**
     * 取上一版
     */
    public CustMechContacterTmp findContacterTmpPrevVersion(final CustMechContacterTmp anContacterTmp) {
        final Long custNo = anContacterTmp.getCustNo();
        final Long refId = anContacterTmp.getRefId();
        final Long version = anContacterTmp.getVersion();

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
    public CustMechContacterTmp addChangeContacterTmp(final CustMechContacterTmp anContacterTmp,
            final String anFileList) {
        BTAssert.notNull(anContacterTmp, "公司联系人流水信息不允许为空！");

        final Long refId = anContacterTmp.getRefId();
        BTAssert.isNull(refId, "引用编号不能有值!");

        anContacterTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW, CustomerConstants.TMP_TYPE_CHANGE, null);
        anContacterTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_ADD);
        anContacterTmp.setBatchNo(fileItemService.updateCustFileItemInfo(anFileList, anContacterTmp.getBatchNo()));

        return addContacterTmp(anContacterTmp);
    }

    /**
     * 添加修改变更记录
     */
    public CustMechContacterTmp saveSaveChangeContacterTmp(final CustMechContacterTmp anContacterTmp,
            final String anFileList) {
        BTAssert.notNull(anContacterTmp, "公司联系人流水信息不允许为空！");

        final Long refId = anContacterTmp.getRefId();
        BTAssert.notNull(refId, "引用编号不允许为空!");

        final CustMechContacter contacter = contacterService.findContacter(refId);
        BTAssert.notNull(contacter, "没有找到引用的记录!");

        if (contacter.getCustNo().equals(anContacterTmp.getCustNo()) == false) {
            throw new BytterTradeException("客户编号不匹配!");
        }

        final CustMechContacterTmp tempContacterTmp = findContacterTmpByRefId(refId, CustomerConstants.TMP_TYPE_CHANGE);
        if (tempContacterTmp == null) {
            anContacterTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW, CustomerConstants.TMP_TYPE_CHANGE, null);
            anContacterTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            anContacterTmp.setBatchNo(fileItemService.updateAndDuplicateConflictFileItemInfo(anFileList,
                    anContacterTmp.getBatchNo(), UserUtils.getOperatorInfo()));
            return addContacterTmp(anContacterTmp);
        } else {
            tempContacterTmp.initModifyValue(anContacterTmp);
            tempContacterTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            tempContacterTmp.setBatchNo(fileItemService.updateAndDuplicateConflictFileItemInfo(anFileList,
                    tempContacterTmp.getBatchNo(), UserUtils.getOperatorInfo()));
            return saveContacterTmp(tempContacterTmp);
        }
    }

    /**
     * 添加删除变更记录
     */
    public CustMechContacterTmp saveDeleteChangeContacterTmp(final Long anRefId) {
        BTAssert.notNull(anRefId, "公司联系人号不允许为空！");

        final CustMechContacter contacter = contacterService.findContacter(anRefId);
        BTAssert.notNull(contacter, "没有找到引用的记录!");

        CustMechContacterTmp contacterTmp = findContacterTmpByRefId(anRefId, CustomerConstants.TMP_TYPE_CHANGE);
        if (contacterTmp == null) {
            contacterTmp = new CustMechContacterTmp();
            contacterTmp.initAddValue(contacter, CustomerConstants.TMP_STATUS_NEW);
            contacterTmp.setRefId(anRefId);
            contacterTmp.setTmpType(CustomerConstants.TMP_TYPE_CHANGE);
            contacterTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return addContacterTmp(contacterTmp);
        } else {
            contacterTmp.initModifyValue(contacter, CustomerConstants.TMP_STATUS_NEW);
            contacterTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return saveContacterTmp(contacterTmp);
        }
    }

    /**
     * 撤销变更记录
     */
    public int saveCancelChangeContacterTmp(final Long anId) {
        final CustMechContacterTmp contacterTmp = this.findContacterTmp(anId);

        final Long tmpVersion = contacterTmp.getVersion();
        final Long maxVersion = VersionHelper.generateVersion(this.mapper, contacterTmp.getCustNo());

        if (tmpVersion.equals(maxVersion) == false) {
            throw new BytterTradeException("流水信息不正确,不可撤销.");
        }

        if (StringUtils.equals(contacterTmp.getTmpType(), CustomerConstants.TMP_TYPE_CHANGE) == false) {
            throw new BytterTradeException("流水信息类型不正确,不可撤销.");
        }

        if (StringUtils.equals(contacterTmp.getBusinStatus(), CustomerConstants.TMP_STATUS_NEW) == false) {
            throw new BytterTradeException("流水信息状态不正确,不可撤销.");
        }

        return this.deleteByPrimaryKey(anId);
    }

    /**
     * 加载变更列表中的流水列表
     */
    public Collection<CustMechContacterTmp> queryContacterTmpByChangeApply(final Long anApplyId) {
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
    private CustMechContacterTmp findContacterTmpByRefId(final Long anRefId, final String anTmpType) {
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

        final List<Long> tmpIds = COMMA_PATTERN.splitAsStream(tempTmpIds).map(Long::valueOf)
                .collect(Collectors.toList());

        if (checkMatchNewChange(tmpIds, anCustNo) == false) {
            throw new BytterTradeException("代录编号列表不正确,请检查.");
        }
        final CustChangeApply changeApply = changeApplyService.addChangeApply(anCustNo,
                CustomerConstants.ITEM_CONTACTER, tempTmpIds);

        for (final Long id : tmpIds) {
            saveContacterTmpParentIdAndStatus(id, changeApply.getId(), CustomerConstants.TMP_STATUS_USEING);
        }

        final Collection<CustMechContacter> contacters = contacterService.queryCustMechContacter(anCustNo);
        final Collection<CustMechContacterTmp> contacterTmps = this.selectByProperty("parentId", changeApply.getId());

        saveNormalContacters(contacters, contacterTmps, changeApply, CustomerConstants.TMP_TYPE_CHANGE);

        return changeApply;
    }

    /**
     * 存储未修改的记录
     */
    private void saveNormalContacters(final Collection<CustMechContacter> anContacters,
            final Collection<CustMechContacterTmp> anContacterTmps, final CustChangeApply anChangeApply,
            final String anTmpType) {
        final Long parentId = anChangeApply.getId();
        for (final CustMechContacter contacter : anContacters) {
            if (checkIsNormalContacter(contacter, anContacterTmps) == true) {
                final CustMechContacterTmp contacterTmp = new CustMechContacterTmp();
                contacterTmp.initAddValue(contacter, CustomerConstants.TMP_STATUS_USEING);
                contacterTmp.setRefId(contacter.getId());
                contacterTmp.setParentId(parentId);
                contacterTmp.setTmpType(anTmpType);
                contacterTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_NORMAL);
                addContacterTmp(contacterTmp);
            }
        }
    }

    /**
     * 检查是否是未改变联系人
     */
    private boolean checkIsNormalContacter(final CustMechContacter anContacter,
            final Collection<CustMechContacterTmp> anContacterTmps) {
        boolean flag = true;
        final Long id = anContacter.getId();
        for (final CustMechContacterTmp contacterTmp : anContacterTmps) {
            final Long refId = contacterTmp.getRefId();
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

        COMMA_PATTERN.splitAsStream(tempTmpIds).map(Long::valueOf).forEach(
                tmpId -> saveContacterTmpParentIdAndStatus(tmpId, anApplyId, CustomerConstants.TMP_STATUS_USEING));

        changeApplyService.saveChangeApply(anApplyId, tempTmpIds);

        final Collection<CustMechContacter> contacters = contacterService.queryCustMechContacter(custNo);
        final Collection<CustMechContacterTmp> contacterTmps = this.selectByProperty("parentId", anApplyId);
        saveNormalContacters(contacters, contacterTmps, changeApply, CustomerConstants.TMP_TYPE_CHANGE);

        return changeApply;
    }

    /**
     * 加载未提交的变更流水列表
     */
    public Collection<CustMechContacterTmp> queryNewChangeCustMechContacterTmp(final Long anCustNo) {
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
    public Collection<CustMechContacterTmp> queryChangeCustMechContacterTmp(final Long anApplyId) {
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
    public Collection<CustMechContacterTmp> queryInsteadContacterTmp(final Long anInsteadRecordId) {
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
     * 添加公司联系人流水信息
     */
    public CustMechContacterTmp addContacterTmp(final CustMechContacterTmp anContacterTmp) {
        BTAssert.notNull(anContacterTmp, "公司联系人流水信息不允许为空！");
        final Long custNo = anContacterTmp.getCustNo();
        final Long version = VersionHelper.generateVersion(this.mapper, custNo);

        anContacterTmp.setVersion(version);
        // anContacterTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW, anTmpType, version);
        this.insert(anContacterTmp);
        return anContacterTmp;
    }

    /**
     * 修改公司联系人流水信息
     */
    public CustMechContacterTmp saveContacterTmp(final CustMechContacterTmp anContacterTmp, final Long anId,
            final String anFileList) {
        BTAssert.notNull(anContacterTmp, "公司联系人流水信息不允许为空！");
        BTAssert.notNull(anId, "公司联系人流水编号不允许为空！");

        final CustMechContacterTmp tempContacterTmp = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempContacterTmp, "没有找到相应联系人记录！");
        tempContacterTmp.initModifyValue(anContacterTmp);
        tempContacterTmp.setBatchNo(fileItemService.updateCustFileItemInfo(anFileList, tempContacterTmp.getBatchNo()));

        return saveContacterTmp(tempContacterTmp);
    }

    public CustMechContacterTmp saveContacterTmp(final CustMechContacterTmp anContacterTmp) {
        BTAssert.notNull(anContacterTmp, "公司联系人流水信息不允许为空！");
        this.updateByPrimaryKeySelective(anContacterTmp);
        return anContacterTmp;
    }

    /**
     * 保存 parentId 和 状态
     */
    public CustMechContacterTmp saveContacterTmpParentIdAndStatus(final Long anId, final Long anParentId,
            final String anBusinStatus) {
        final CustMechContacterTmp contacterTmp = this.selectByPrimaryKey(anId);

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
    public CustMechContacterTmp addInsteadContacterTmp(final CustMechContacterTmp anContacterTmp,
            final Long anInsteadRecordId, final String anFileList) {
        BTAssert.notNull(anContacterTmp, "公司联系人流水信息不允许为空！");

        checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW,
                CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN, CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT,
                CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        final Long refId = anContacterTmp.getRefId();

        final CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId);
        BTAssert.isNull(refId, "引用编号需要为空!");

        if (insteadRecord.getCustNo().equals(anContacterTmp.getCustNo()) == false) {
            throw new BytterTradeException("客户编号不匹配!");
        }

        anContacterTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW, CustomerConstants.TMP_TYPE_INSTEAD, null);
        anContacterTmp.setParentId(anInsteadRecordId);
        anContacterTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_ADD);
        anContacterTmp.setBatchNo(fileItemService.updateCustFileItemInfo(anFileList, anContacterTmp.getBatchNo()));
        return addContacterTmp(anContacterTmp);
    }

    /**
     * 添加修改代录流水
     */
    public CustMechContacterTmp saveSaveInsteadContacterTmp(final CustMechContacterTmp anContacterTmp,
            final Long anInsteadRecordId, final String anFileList) {
        BTAssert.notNull(anContacterTmp, "公司联系人流水信息不允许为空！");

        final CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId,
                CustomerConstants.INSTEAD_RECORD_STATUS_NEW, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN,
                CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT,
                CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        final Long refId = anContacterTmp.getRefId();
        BTAssert.notNull(refId, "引用编号不允许为空!");

        final CustMechContacter contacter = contacterService.findContacter(refId);
        BTAssert.notNull(contacter, "没有找到引用的记录!");

        if (insteadRecord.getCustNo().equals(anContacterTmp.getCustNo()) == false) {
            throw new BytterTradeException("客户编号不匹配!");
        }

        final CustMechContacterTmp tempContacterTmp = findContacterTmpByRefId(refId,
                CustomerConstants.TMP_TYPE_INSTEAD);
        if (tempContacterTmp == null) {
            anContacterTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW, CustomerConstants.TMP_TYPE_INSTEAD, null);
            anContacterTmp.setParentId(anInsteadRecordId);
            anContacterTmp.setBusinStatus(CustomerConstants.TMP_STATUS_NEW);
            anContacterTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            anContacterTmp.setBatchNo(fileItemService.updateAndDuplicateConflictFileItemInfo(anFileList,
                    anContacterTmp.getBatchNo(), UserUtils.getOperatorInfo()));
            return addContacterTmp(anContacterTmp);
        } else {
            tempContacterTmp.initModifyValue(anContacterTmp);
            tempContacterTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            tempContacterTmp.setBatchNo(fileItemService.updateAndDuplicateConflictFileItemInfo(anFileList,
                    tempContacterTmp.getBatchNo(), UserUtils.getOperatorInfo()));
            return saveContacterTmp(tempContacterTmp);
        }
    }

    /**
     * 添加删除代录流水
     * 
     * @param anInsteadRecordId
     */
    public CustMechContacterTmp saveDeleteInsteadContacterTmp(final Long anRefId, final Long anInsteadRecordId) {
        BTAssert.notNull(anRefId, "公司联系人号不允许为空！");

        final CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId,
                CustomerConstants.INSTEAD_RECORD_STATUS_NEW, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN,
                CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT,
                CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        final CustMechContacter contacter = contacterService.findContacter(anRefId);
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
            contacterTmp.setTmpType(CustomerConstants.TMP_TYPE_INSTEAD);
            contacterTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return addContacterTmp(contacterTmp);
        } else {
            contacterTmp.initModifyValue(contacter, CustomerConstants.TMP_STATUS_NEW);
            contacterTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return saveContacterTmp(contacterTmp);
        }
    }

    /**
     * 撤销代录流水
     * 
     * @param anInsteadRecordId
     */
    public int saveCancelInsteadContacterTmp(final Long anId, final Long anInsteadRecordId) {
        final CustMechContacterTmp contacterTmp = this.findContacterTmp(anId);

        checkInsteadRecord(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW,
                CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN, CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT,
                CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        final Long tmpVersion = contacterTmp.getVersion();
        final Long maxVersion = VersionHelper.generateVersion(this.mapper, contacterTmp.getCustNo());

        if (tmpVersion.equals(maxVersion) == false) {
            throw new BytterTradeException("流水信息不正确,不可撤销.");
        }

        if (StringUtils.equals(contacterTmp.getTmpType(), CustomerConstants.TMP_TYPE_INSTEAD) == false) {
            throw new BytterTradeException("流水信息类型不正确,不可撤销.");
        }

        if (StringUtils.equals(contacterTmp.getBusinStatus(), CustomerConstants.TMP_STATUS_NEW) == false) {
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

        final CustInsteadRecord insteadRecord = checkInsteadRecord(anInsteadRecordId,
                CustomerConstants.INSTEAD_RECORD_STATUS_NEW);

        final String tempTmpIds = (String) anParam.get("tmpIds");

        COMMA_PATTERN.splitAsStream(tempTmpIds).map(Long::valueOf)
                .forEach(tmpId -> saveContacterTmpParentIdAndStatus(tmpId, insteadRecord.getId(),
                        CustomerConstants.TMP_STATUS_USEING));

        insteadRecordService.saveInsteadRecordStatus(anInsteadRecordId,
                CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN);

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
                .forEach(tmpId -> saveContacterTmpParentIdAndStatus(tmpId, insteadRecord.getId(),
                        CustomerConstants.TMP_STATUS_USEING));

        insteadRecordService.saveInsteadRecord(anInsteadRecordId, tempTmpIds);

        return insteadRecord;
    }

    /**
     * 回写正式数据
     */
    @Override
    public void saveFormalData(final Long anId) {

        final Collection<CustMechContacterTmp> contacterTmps = this.selectByProperty("parentId", anId);

        for (final CustMechContacterTmp contacterTmp : contacterTmps) {
            final String tmpOperType = contacterTmp.getTmpOperType();
            switch (tmpOperType) {
            case CustomerConstants.TMP_OPER_TYPE_ADD:
                final CustMechContacter contacter = contacterService.addCustMechContacter(contacterTmp);
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
        if (StringUtils.equals(insteadItem, CustomerConstants.ITEM_CONTACTER) == false) {
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
        if (StringUtils.equals(changeItem, CustomerConstants.ITEM_CONTACTER) == false) {
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
        final Collection<CustMechContacterTmp> contacterTmps = queryNewChangeCustMechContacterTmp(anCustNo);
        if (anChangeIds.size() != contacterTmps.size()) {
            return false;
        }
        final Set<Long> tempSet = new HashSet<>();

        for (final CustMechContacterTmp contacterTmp : contacterTmps) {
            if (anCustNo.equals(contacterTmp.getCustNo()) == false) {
                return false;
            }

            if (StringUtils.equals(contacterTmp.getTmpOperType(),
                    CustomerConstants.TMP_OPER_TYPE_NORMAL) == true) {
                continue;
            } else {
                final Long id = contacterTmp.getId();
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