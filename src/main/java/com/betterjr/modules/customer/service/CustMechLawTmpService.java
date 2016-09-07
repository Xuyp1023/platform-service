package com.betterjr.modules.customer.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterException;
import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.customer.constants.CustomerConstants;
import com.betterjr.modules.customer.dao.CustMechLawTmpMapper;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustInsteadApply;
import com.betterjr.modules.customer.entity.CustInsteadRecord;
import com.betterjr.modules.customer.entity.CustMechLaw;
import com.betterjr.modules.customer.entity.CustMechLawTmp;
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
public class CustMechLawTmpService extends BaseService<CustMechLawTmpMapper, CustMechLawTmp> implements IFormalDataService {
    @Resource
    private CustMechLawService lawService;

    @Resource
    private CustInsteadRecordService insteadRecordService;

    @Resource
    private CustInsteadApplyService insteadApplyService;

    @Resource
    private CustChangeApplyService changeApplyService;

    @Resource
    private CustAccountService custAccountService;

    @Autowired
    private CustFileItemService custFileItemService;

    /**
     * 
     * @param anId
     * @return
     */
    public CustMechLawTmp findCustMechLawTmp(Long anId) {
        BTAssert.notNull(anId, "编号不允许为空！");

        return this.selectByPrimaryKey(anId);
    }

    /**
     * 取上一版本
     */
    public CustMechLawTmp findCustMechLawTmpPrevVersion(CustMechLawTmp anLawTmp) {
        Long refId = anLawTmp.getRefId();
        Long version = anLawTmp.getVersion();

        Long befVersion = this.mapper.selectPrevVersion(refId, version);

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("version", befVersion);
        conditionMap.put("refId", refId);

        List<CustMechLawTmp> befDatas = this.selectByProperty(conditionMap);
        return Collections3.getFirst(befDatas);
    }

    /**
     * 法人信息-流水信息-详情
     */
    public CustMechLawTmp findCustMechLawTmpByInsteadRecord(Long anInsteadRecordId) {
        BTAssert.notNull(anInsteadRecordId, "代录项目编号不允许为空！");

        CustInsteadRecord insteadRecord = insteadRecordService.findInsteadRecord(anInsteadRecordId);
        BTAssert.notNull(insteadRecord, "代录项目没有找到!");

        if (BetterStringUtils.equals(insteadRecord.getInsteadItem(), CustomerConstants.ITEM_LAW) == false) {
            throw new BytterException(20120, "代录项目类型不匹配!");
        }

        Long tmpId = Long.valueOf(insteadRecord.getTmpIds());

        final CustMechLawTmp custMechLawTmp = findCustMechLawTmp(tmpId);
        BTAssert.notNull(custMechLawTmp, "没有找到对应法人流水信息!");

        return custMechLawTmp;
    }

    /**
     * 法人信息-流水信息-添加
     */
    public CustMechLawTmp addCustMechLawTmp(CustMechLawTmp anLawTmp, String anFileList, String anTmpType) {
        BTAssert.notNull(anLawTmp, "法人信息-流水信息  不能为空！");
        BTAssert.notNull(anTmpType, "流水类型  不能为空！");

        final Long custNo = anLawTmp.getRefId();
        anLawTmp.setCustNo(anLawTmp.getRefId());
        final String custName = custAccountService.queryCustName(custNo);
        anLawTmp.initAddValue(anTmpType, custNo, custName);
        anLawTmp.setBatchNo(custFileItemService.updateAndDuplicateConflictFileItemInfo(anFileList, anLawTmp.getBatchNo(), UserUtils.getOperatorInfo()));
        anLawTmp.setVersion(VersionHelper.generateVersion(this.mapper, custNo));
        this.insert(anLawTmp);

        return anLawTmp;
    }

    /**
     * 法人信息-流水信息-添加 从法人信息建立流水
     */
    public CustMechLawTmp addCustMechLawTmp(CustMechLaw anCustMechLaw) {
        BTAssert.notNull(anCustMechLaw, "公司法人信息 不能为空！");

        final CustMechLawTmp custMechLawTmp = new CustMechLawTmp();

        custMechLawTmp.initAddValue(anCustMechLaw, CustomerConstants.TMP_TYPE_INITDATA, CustomerConstants.TMP_STATUS_USED);
        custMechLawTmp.setVersion(VersionHelper.generateVersion(this.mapper, anCustMechLaw.getCustNo()));
        custMechLawTmp.setRefId(anCustMechLaw.getCustNo());

        this.insert(custMechLawTmp);

        return custMechLawTmp;
    }

    /**
     * 法人信息-流水信息-修改
     */
    public CustMechLawTmp saveCustMechLawTmp(CustMechLawTmp anLawTmp, Long anId, String anFileList) {
        BTAssert.notNull(anId, "法人信息-流水信息 编号不允许为空！");

        final CustMechLawTmp tempLawTmp = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempLawTmp, "没有找到对应的公司法人流水信息！");
        tempLawTmp.setBatchNo(custFileItemService.updateCustFileItemInfo(anFileList, tempLawTmp.getBatchNo()));
        tempLawTmp.initModifyValue(anLawTmp);
        this.updateByPrimaryKey(tempLawTmp);

        return tempLawTmp;
    }

    /**
     * 法人信息-流水信息-变更申请
     */
    public CustMechLawTmp addChangeApply(CustMechLawTmp anLawTmp, String anFileList) {
        BTAssert.notNull(anLawTmp, "基本信息变更申请不能为空");

        CustMechLawTmp lawTmp = addCustMechLawTmp(anLawTmp, anFileList, CustomerConstants.TMP_TYPE_CHANGE);

        CustChangeApply changeApply = changeApplyService.addChangeApply(lawTmp.getRefId(), CustomerConstants.ITEM_LAW,
                String.valueOf(lawTmp.getId()));

        saveCustMechLawTmpParentId(lawTmp.getId(), changeApply.getId());

        return lawTmp;
    }

    /**
     * 法人信息-流水信息-变更修改/重新提交
     */
    public CustMechLawTmp saveChangeApply(CustMechLawTmp anLawTmp, Long anApplyId, String anFileList) {
        CustChangeApply changeApply = checkChangeApply(anApplyId);

        Long tmpId = Long.valueOf(changeApply.getTmpIds());
        BTAssert.notNull(anLawTmp, "公司基本信息-变更修改 不能为空");

        CustMechLawTmp custMechLawTmp = saveCustMechLawTmp(anLawTmp, tmpId, anFileList);

        changeApplyService.saveChangeApplyStatus(anApplyId, CustomerConstants.CHANGE_APPLY_STATUS_NEW);

        return custMechLawTmp;
    }

    /**
     * 法人流水信息-添加代录
     */
    public CustMechLawTmp addInsteadRecord(CustMechLawTmp anLawTmp, Long anInsteadRecordId, String anFileList) {
        checkInsteadRecord(anLawTmp, anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW);

        final CustMechLawTmp lawTmp = addCustMechLawTmp(anLawTmp, anFileList, CustomerConstants.TMP_TYPE_INSTEAD);

        CustInsteadRecord insteadRecord = insteadRecordService.saveInsteadRecord(anInsteadRecordId, String.valueOf(lawTmp.getId()));

        saveCustMechLawTmpParentId(lawTmp.getId(), insteadRecord.getId());

        return lawTmp;
    }

    /**
     * 法人流水信息-修改代录
     * 
     * @param anCustMechBaseTmp
     * @return
     */
    public CustMechLawTmp saveInsteadRecord(CustMechLawTmp anLawTmp, Long anInsteadRecordId, String anFileList) {
        CustInsteadRecord insteadRecord = checkInsteadRecord(anLawTmp, anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN,
                CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT, CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        Long tmpId = Long.valueOf(insteadRecord.getTmpIds());

        final CustMechLawTmp tempLawTmp = saveCustMechLawTmp(anLawTmp, tmpId, anFileList);

        insteadRecordService.saveInsteadRecordStatus(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN);

        return tempLawTmp;
    }

    /**
     * 检查并返回变更申请
     * 
     * @param anApplyId
     * @return
     */
    public CustChangeApply checkChangeApply(Long anApplyId, String... anBusinStatus) {
        BTAssert.notNull(anApplyId, "变更申请-编号 不能为空");

        CustChangeApply changeApply = changeApplyService.findChangeApply(anApplyId);
        if (BetterStringUtils.equals(changeApply.getChangeItem(), CustomerConstants.ITEM_LAW) == false) {
            throw new BytterTradeException(20074, "");
        }

        return changeApply;
    }

    /**
     * 检查并返回代录记录
     */
    private CustInsteadRecord checkInsteadRecord(CustMechLawTmp anCustMechLawTmp, Long anInsteadRecordId, String... anBusinStatus) {
        BTAssert.notNull(anCustMechLawTmp, "法人信息流水信息不允许为空！");
        BTAssert.notNull(anInsteadRecordId, "代录记录编号不允许为空！");

        CustInsteadRecord insteadRecord = insteadRecordService.findInsteadRecord(anInsteadRecordId);
        BTAssert.notNull(insteadRecord, "没有找到对应的代录记录");

        String insteadItem = insteadRecord.getInsteadItem();
        if (BetterStringUtils.equals(insteadItem, CustomerConstants.ITEM_LAW) == false) {
            throw new BytterTradeException(20072, "代录项目不匹配！");
        }

        List<String> businStatus = Arrays.asList(anBusinStatus);
        if (businStatus.contains(insteadRecord.getBusinStatus()) == false) {
            throw new BytterTradeException(20071, "此代录项目状态不正确！");
        }

        Long applyId = insteadRecord.getApplyId();
        CustInsteadApply insteadApply = insteadApplyService.findCustInsteadApply(applyId);

        List<String> applyAllowStatus = Arrays.asList(new String[] { CustomerConstants.INSTEAD_APPLY_STATUS_AUDIT_PASS,
                CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_REJECT, CustomerConstants.INSTEAD_APPLY_STATUS_CONFIRM_REJECT });
        if (applyAllowStatus.contains(insteadApply.getBusinStatus()) == false) {
            throw new BytterTradeException(20073, "此代录申请状态不正确！");
        }

        return insteadRecord;
    }

    /**
     * 保存流水信息状态并返回
     */
    private CustMechLawTmp saveCustMechLawTmpStatus(Long anId, String anBusinStatus) {
        BTAssert.notNull(anId, "编号不允许为空");
        BTAssert.notNull(anBusinStatus, "状态不允许为空");

        final CustMechLawTmp custMechLawTmp = this.selectByPrimaryKey(anId);
        BTAssert.notNull(custMechLawTmp, "没有找到临时流水信息！");

        custMechLawTmp.initModifyValue(anBusinStatus);
        this.updateByPrimaryKeySelective(custMechLawTmp);

        return custMechLawTmp;
    }

    private void saveCustMechLawTmpParentId(Long anId, Long anParentId) {
        final CustMechLawTmp custMechLawTmp = this.selectByPrimaryKey(anId);
        BTAssert.notNull(custMechLawTmp, "没有找到临时流水信息！");

        custMechLawTmp.setParentId(anParentId);
        this.updateByPrimaryKeySelective(custMechLawTmp);
    }

    /**
     * 保存数据至正式表
     */
    @Override
    public void saveFormalData(Long anId) {
        BTAssert.notNull(anId, "编号不允许为空！");
        CustMechLawTmp lawTmp = Collections3.getFirst(this.selectByProperty("parentId", anId));

        final CustMechLawTmp tempLawTmp = saveCustMechLawTmpStatus(lawTmp.getId(), CustomerConstants.TMP_STATUS_USED);

        lawService.saveCustMechLaw(tempLawTmp);
    }

    /**
     * 回写作废记录
     * 
     * @param anTmpIds
     */
    @Override
    public void saveCancelData(Long anId) {

    }
}
