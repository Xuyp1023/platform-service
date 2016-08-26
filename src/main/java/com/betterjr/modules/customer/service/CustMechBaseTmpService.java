package com.betterjr.modules.customer.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterException;
import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.dao.CustMechBaseTmpMapper;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustInsteadApply;
import com.betterjr.modules.customer.entity.CustInsteadRecord;
import com.betterjr.modules.customer.entity.CustMechBase;
import com.betterjr.modules.customer.entity.CustMechBaseTmp;
import com.betterjr.modules.customer.entity.CustMechLawTmp;
import com.betterjr.modules.customer.helper.IFormalDataService;
import com.betterjr.modules.customer.helper.VersionHelper;
import com.betterjr.modules.document.service.CustFileItemService;

/**
 * 客户基本信息流水信息管理
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechBaseTmpService extends BaseService<CustMechBaseTmpMapper, CustMechBaseTmp> implements IFormalDataService {
    @Resource
    private CustMechBaseService baseService;

    @Resource
    private CustInsteadRecordService insteadRecordService;

    @Resource
    private CustInsteadApplyService insteadApplyService;

    @Resource
    private CustChangeApplyService changeApplyService;

    @Resource
    private CustAccountService custAccountService;

    @Resource
    private CustFileItemService fileItemService;

    /**
     * 公司基本信息-流水信息-详情
     */
    public CustMechBaseTmp findCustMechBaseTmp(Long anId) {
        BTAssert.notNull(anId, "流水编号不允许为空！");

        return this.selectByPrimaryKey(anId);
    }

    /**
     * 根据当前流水,找上一条有效流水
     */
    public CustMechBaseTmp findCustMechBaseTmpPrevVersion(CustMechBaseTmp anCustMechBaseTmp) {
        Long refId = anCustMechBaseTmp.getRefId();
        Long version = anCustMechBaseTmp.getVersion();

        Long befVersion = this.mapper.selectPrevVersion(refId, version);

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("version", befVersion);
        conditionMap.put("refId", refId);

        List<CustMechBaseTmp> befDatas = this.selectByProperty(conditionMap);
        return Collections3.getFirst(befDatas);
    }

    /**
     * 公司基本信息-流水信息-详情
     */
    public CustMechBaseTmp findCustMechBaseTmpByInsteadRecord(Long anInsteadRecordId) {
        BTAssert.notNull(anInsteadRecordId, "代录项目编号不允许为空！");

        CustInsteadRecord insteadRecord = insteadRecordService.findInsteadRecord(anInsteadRecordId);
        BTAssert.notNull(insteadRecord, "代录项目没有找到!");

        if (BetterStringUtils.equals(insteadRecord.getInsteadItem(), CustomerConstants.ITEM_BASE) == false) {
            throw new BytterException(20120, "代录项目类型不匹配!");
        }

        Long tmpId = Long.valueOf(insteadRecord.getTmpIds());

        CustMechBaseTmp custMechBaseTmp = this.findCustMechBaseTmp(tmpId);
        BTAssert.notNull(custMechBaseTmp, "没有找到对应的流水信息!");

        return custMechBaseTmp;
    }

    /**
     * 公司基本信息-流水信息-添加
     */
    public CustMechBaseTmp addCustMechBaseTmp(CustMechBaseTmp anCustMechBaseTmp, String anFileList, String anTmpType) {
        BTAssert.notNull(anCustMechBaseTmp, "公司基本信息-流水信息 不能为空！");
        BTAssert.notNull(anTmpType, "流水类型  不能为空！");

        final Long custNo = anCustMechBaseTmp.getRefId();

        anCustMechBaseTmp.setBatchNo(fileItemService.updateAndDuplicateConflictFileItemInfo(anFileList, anCustMechBaseTmp.getBatchNo()));
        anCustMechBaseTmp.initAddValue(anTmpType, custNo);
        anCustMechBaseTmp.setVersion(VersionHelper.generateVersion(this.mapper, custNo));
        this.insert(anCustMechBaseTmp);

        return anCustMechBaseTmp;
    }

    /**
     * 公司基本信息-流水信息-添加 开户时建立
     */
    public CustMechBaseTmp addCustMechBaseTmp(CustMechBase anCustMechBase) {
        BTAssert.notNull(anCustMechBase, "公司基本信息 不能为空！");

        final CustMechBaseTmp custMechBaseTmp = new CustMechBaseTmp();

        custMechBaseTmp.initAddValue(anCustMechBase, CustomerConstants.TMP_TYPE_INITDATA, CustomerConstants.TMP_STATUS_USED);
        custMechBaseTmp.setVersion(VersionHelper.generateVersion(this.mapper, anCustMechBase.getCustNo()));
        custMechBaseTmp.setRefId(anCustMechBase.getCustNo());

        this.insert(custMechBaseTmp);

        return custMechBaseTmp;
    }

    /**
     * 公司基本信息-流水信息-修改
     */
    public CustMechBaseTmp saveCustMechBaseTmp(CustMechBaseTmp anCustMechBaseTmp, Long anId, String anFileList) {
        BTAssert.notNull(anId, "公司基本信息-流水信息 编号不允许为空！");

        final CustMechBaseTmp tempCustMechBaseTmp = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustMechBaseTmp, "公司基本信息-流水信息 没有找到！");

        tempCustMechBaseTmp.setBatchNo(fileItemService.updateCustFileItemInfo(anFileList, tempCustMechBaseTmp.getBatchNo()));
        tempCustMechBaseTmp.setCustNo(anCustMechBaseTmp.getRefId());
        this.updateByPrimaryKeySelective(tempCustMechBaseTmp);

        return tempCustMechBaseTmp;
    }

    /**
     * 公司基本信息-变更申请
     * 
     */
    public CustMechBaseTmp addChangeApply(CustMechBaseTmp anCustMechBaseTmp, String anFileList) {
        BTAssert.notNull(anCustMechBaseTmp, "公司基本信息-变更申请 不能为空");

        CustMechBaseTmp custMechBaseTmp = addCustMechBaseTmp(anCustMechBaseTmp, anFileList, CustomerConstants.TMP_TYPE_CHANGE);

        CustChangeApply changeApply = changeApplyService.addChangeApply(custMechBaseTmp.getRefId(), CustomerConstants.ITEM_BASE,
                String.valueOf(custMechBaseTmp.getId()));

        saveCustMechBaseTmpParentId(custMechBaseTmp.getId(), changeApply.getId());

        return custMechBaseTmp;
    }

    /**
     * 公司基本信息-变更修改/重新提交
     * 
     */
    public CustMechBaseTmp saveChangeApply(CustMechBaseTmp anCustMechBaseTmp, Long anApplyId, String anFileList) {
        CustChangeApply changeApply = checkChangeApply(anApplyId);

        Long tmpId = Long.valueOf(changeApply.getTmpIds());

        BTAssert.notNull(anCustMechBaseTmp, "公司基本信息-变更修改 不能为空");

        CustMechBaseTmp custMechBaseTmp = saveCustMechBaseTmp(anCustMechBaseTmp, tmpId, anFileList);

        changeApplyService.saveChangeApplyStatus(anApplyId, CustomerConstants.CHANGE_APPLY_STATUS_NEW);

        return custMechBaseTmp;
    }

    /**
     * 公司基本信息-添加代录
     */
    public CustMechBaseTmp addInsteadRecord(CustMechBaseTmp anCustMechBaseTmp, Long anInsteadRecordId, String anFileList) {
        checkInsteadRecord(anCustMechBaseTmp, anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW);

        final CustMechBaseTmp custMechBaseTmp = addCustMechBaseTmp(anCustMechBaseTmp, anFileList, CustomerConstants.TMP_TYPE_INSTEAD);

        CustInsteadRecord insteadRecord = insteadRecordService.saveInsteadRecord(anInsteadRecordId, String.valueOf(custMechBaseTmp.getId()));

        saveCustMechBaseTmpParentId(custMechBaseTmp.getId(), insteadRecord.getId());

        return custMechBaseTmp;
    }

    /**
     * 公司基本信息-修改代录
     */
    public CustMechBaseTmp saveInsteadRecord(CustMechBaseTmp anCustMechBaseTmp, Long anInsteadRecordId, String anFileList) {
        CustInsteadRecord insteadRecord = checkInsteadRecord(anCustMechBaseTmp, anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN,
                CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT, CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        Long tmpId = Long.valueOf(insteadRecord.getTmpIds());
        final CustMechBaseTmp custMechBaseTmp = saveCustMechBaseTmp(anCustMechBaseTmp, tmpId, anFileList);
        
        insteadRecordService.saveInsteadRecordStatus(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN);

        return custMechBaseTmp;
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
        if (BetterStringUtils.equals(changeApply.getChangeItem(), CustomerConstants.ITEM_BASE) == false) {
            throw new BytterTradeException(20074, "变更申请类型不匹配!");
        }

        return changeApply;
    }

    /**
     * 检查并返回代录记录
     */
    private CustInsteadRecord checkInsteadRecord(CustMechBaseTmp anCustMechBaseTmp, Long anInsteadRecordId, String... anBusinStatus) {
        BTAssert.notNull(anCustMechBaseTmp, "客户基本信息流水信息不允许为空！");
        BTAssert.notNull(anInsteadRecordId, "代录记录编号不允许为空！");

        CustInsteadRecord insteadRecord = insteadRecordService.findInsteadRecord(anInsteadRecordId);
        BTAssert.notNull(insteadRecord, "没有找到对应的代录记录");

        String insteadItem = insteadRecord.getInsteadItem();
        if (BetterStringUtils.equals(insteadItem, CustomerConstants.ITEM_BASE) == false) {
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
    private CustMechBaseTmp saveCustMechBaseTmpStatus(Long anId, String anBusinStatus) {
        BTAssert.notNull(anId, "编号不允许为空");
        BTAssert.notNull(anBusinStatus, "状态不允许为空");

        final CustMechBaseTmp custMechBaseTmp = this.selectByPrimaryKey(anId);
        BTAssert.notNull(custMechBaseTmp, "没有找到临时流水信息！");

        custMechBaseTmp.initModifyValue(anBusinStatus);
        this.updateByPrimaryKeySelective(custMechBaseTmp);

        return custMechBaseTmp;
    }

    
    private void saveCustMechBaseTmpParentId(Long anId, Long anParentId) {
        CustMechBaseTmp custMechBaseTmp = findCustMechBaseTmp(anId);
        custMechBaseTmp.setParentId(anParentId);
        this.updateByPrimaryKeySelective(custMechBaseTmp);
    }

    /**
     * 保存数据至正式表
     */
    @Override
    public void saveFormalData(Long anId) {
        BTAssert.notNull(anId, "临时流水编号不允许为空！");
        CustMechBaseTmp baseInfoTmp = Collections3.getFirst(this.selectByProperty("parentId", anId));

        final CustMechBaseTmp tempBaseInfoTmp = saveCustMechBaseTmpStatus(baseInfoTmp.getId(), CustomerConstants.TMP_STATUS_USED);

        baseService.saveCustMechBase(tempBaseInfoTmp);
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