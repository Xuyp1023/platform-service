package com.betterjr.modules.customer.service;

import java.util.Arrays;
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
import com.betterjr.modules.customer.dao.CustMechBusinLicenceTmpMapper;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustInsteadApply;
import com.betterjr.modules.customer.entity.CustInsteadRecord;
import com.betterjr.modules.customer.entity.CustMechBusinLicence;
import com.betterjr.modules.customer.entity.CustMechBusinLicenceTmp;
import com.betterjr.modules.customer.helper.IFormalDataService;
import com.betterjr.modules.customer.helper.VersionHelper;
import com.betterjr.modules.document.service.CustFileItemService;

/**
 * 营业执照流水
 *
 * @author liuwl
 *
 */
@Service
public class CustMechBusinLicenceTmpService extends BaseService<CustMechBusinLicenceTmpMapper, CustMechBusinLicenceTmp>
implements IFormalDataService {
    @Resource
    private CustMechBusinLicenceService businLicenceService;

    @Resource
    private CustInsteadRecordService insteadRecordService;

    @Resource
    private CustInsteadApplyService insteadApplyService;

    @Resource
    private CustChangeApplyService changeApplyService;

    @Resource
    private CustAccountService accountService;

    @Autowired
    private CustFileItemService fileItemService;


    public CustMechBusinLicenceTmp findBusinLicenceTmp(final Long anId) {
        BTAssert.notNull(anId, "编号不允许为空！");

        return this.selectByPrimaryKey(anId);
    }

    /**
     * 营业执照流水信息-查询
     */
    public CustMechBusinLicenceTmp findBusinLicenceTmpByInsteadRecord(final Long anInsteadRecordId) {
        BTAssert.notNull(anInsteadRecordId, "代录项目编号不允许为空！");

        final CustInsteadRecord insteadRecord = insteadRecordService.findInsteadRecord(anInsteadRecordId);
        BTAssert.notNull(insteadRecord, "代录项目没有找到!");

        if (BetterStringUtils.equals(insteadRecord.getInsteadItem(), CustomerConstants.ITEM_BUSINLICENCE) == false) {
            throw new BytterException(20120, "代录项目类型不匹配!");
        }

        final Long id = Long.valueOf(insteadRecord.getTmpIds());
        final CustMechBusinLicenceTmp businLicenceTmp = findBusinLicenceTmp(id);
        return businLicenceTmp;
    }

    /**
     * 根据当前流水取上一版可用流水
     */
    public CustMechBusinLicenceTmp findBusinLicenceTmpPrevVersion(final CustMechBusinLicenceTmp anBusinLicenceTmp) {
        final Long refId = anBusinLicenceTmp.getRefId();
        final Long version = anBusinLicenceTmp.getVersion();

        final Long befVersion = this.mapper.selectPrevVersion(refId, version);

        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("version", befVersion);
        conditionMap.put("refId", refId);

        final List<CustMechBusinLicenceTmp> befDatas = this.selectByProperty(conditionMap);
        return Collections3.getFirst(befDatas);
    }

    /**
     * 营业执照流水信息-保存
     */
    public CustMechBusinLicenceTmp saveBusinLicenceTmp(final CustMechBusinLicenceTmp anBusinLicenceTmp, final Long anId, final String anFileList) {
        BTAssert.notNull(anId, "营业执照流水编号不允许为空！");

        final CustMechBusinLicenceTmp tempBusinLicenceTmp = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempBusinLicenceTmp, "没有找到对应的营业执照流水信息！");

        tempBusinLicenceTmp.setBatchNo(fileItemService.updateCustFileItemInfo(anFileList, tempBusinLicenceTmp.getBatchNo()));
        tempBusinLicenceTmp.initModifyValue(anBusinLicenceTmp);
        this.updateByPrimaryKeySelective(tempBusinLicenceTmp);

        return tempBusinLicenceTmp;
    }

    /**
     * 营业执照流水信息-添加
     */
    public CustMechBusinLicenceTmp addBusinLicenceTmp(final CustMechBusinLicenceTmp anBusinLicenceTmp, final String anFileList, final String anTmpType) {
        BTAssert.notNull(anBusinLicenceTmp, "营业执照流水信息不允许为空！");

        final Long custNo = anBusinLicenceTmp.getRefId();
        anBusinLicenceTmp.setCustNo(custNo);

        final String custName = accountService.queryCustName(custNo);
        anBusinLicenceTmp.initAddValue(anTmpType, custNo, custName);

        anBusinLicenceTmp.setBatchNo(fileItemService.updateAndDuplicateConflictFileItemInfo(anFileList, anBusinLicenceTmp.getBatchNo(), UserUtils.getOperatorInfo()));
        anBusinLicenceTmp.setVersion(VersionHelper.generateVersion(this.mapper, custNo));
        this.insert(anBusinLicenceTmp);

        return anBusinLicenceTmp;
    }

    /**
     * 营业执照流水信息-添加 开户初始化数据时建立初始流水
     */
    public CustMechBusinLicenceTmp addBusinLicenceTmp(final CustMechBusinLicence anBusinLicence) {
        BTAssert.notNull(anBusinLicence, "公司营业执照信息 不能为空！");

        final CustMechBusinLicenceTmp businLicenceTmp = new CustMechBusinLicenceTmp();

        businLicenceTmp.initAddValue(anBusinLicence, CustomerConstants.TMP_TYPE_INITDATA, CustomerConstants.TMP_STATUS_USED);
        businLicenceTmp.setVersion(VersionHelper.generateVersion(this.mapper, anBusinLicence.getCustNo()));
        businLicenceTmp.setRefId(anBusinLicence.getCustNo());

        this.insert(businLicenceTmp);

        return businLicenceTmp;
    }

    /**
     * 营业执照流水信息-变更申请
     */
    public CustMechBusinLicenceTmp addChangeApply(final CustMechBusinLicenceTmp anBusinLicenceTmp, final String anFileList) {
        BTAssert.notNull(anBusinLicenceTmp, "基本信息变更申请不能为空");

        final CustMechBusinLicenceTmp businLicenceTmp = addBusinLicenceTmp(anBusinLicenceTmp, anFileList, CustomerConstants.TMP_TYPE_CHANGE);

        final CustChangeApply changeApply = changeApplyService.addChangeApply(anBusinLicenceTmp.getRefId(), CustomerConstants.ITEM_BUSINLICENCE,
                String.valueOf(anBusinLicenceTmp.getId()));

        saveBusinLicenceTmpParentId(businLicenceTmp.getId(), changeApply.getId());

        return businLicenceTmp;
    }

    /**
     * 营业执照流水信息-变更修改/重新提交
     *
     * @param anCustMechLawTmp
     * @return
     */
    public CustMechBusinLicenceTmp saveChangeApply(final CustMechBusinLicenceTmp anBusinLicenceTmp, final Long anApplyId, final String anFileList) {
        final CustChangeApply changeApply = checkChangeApply(anApplyId);

        final Long tmpId = Long.valueOf(changeApply.getTmpIds());
        BTAssert.notNull(anBusinLicenceTmp, "营业执照流水信息 不能为空");

        final CustMechBusinLicenceTmp businLicenceTmp = saveBusinLicenceTmp(anBusinLicenceTmp, tmpId, anFileList);

        changeApplyService.saveChangeApplyStatus(anApplyId, CustomerConstants.CHANGE_APPLY_STATUS_NEW);

        return businLicenceTmp;
    }

    /**
     * 营业执照流水信息-添加代录
     */
    public Object addInsteadRecord(final CustMechBusinLicenceTmp anBusinLicenceTmp, final Long anInsteadRecordId, final String anFileList) {
        checkInsteadRecord(anBusinLicenceTmp, anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW);

        final CustMechBusinLicenceTmp businLicenceTmp = addBusinLicenceTmp(anBusinLicenceTmp, anFileList, CustomerConstants.TMP_TYPE_INSTEAD);

        final CustInsteadRecord insteadRecord = insteadRecordService.saveInsteadRecord(anInsteadRecordId, String.valueOf(businLicenceTmp.getId()));

        saveBusinLicenceTmpParentId(businLicenceTmp.getId(), insteadRecord.getId());

        return businLicenceTmp;
    }

    /**
     * 营业执照流水信息-修改代录
     */
    public Object saveInsteadRecord(final CustMechBusinLicenceTmp anBusinLicenceTmp, final Long anInsteadRecordId, final String anFileList) {
        final CustInsteadRecord insteadRecord = checkInsteadRecord(anBusinLicenceTmp,  anInsteadRecordId,
                CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN,
                CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT,
                CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        final Long tmpId = Long.valueOf(insteadRecord.getTmpIds());

        final CustMechBusinLicenceTmp businLicenceTmp = saveBusinLicenceTmp(anBusinLicenceTmp, tmpId, anFileList);
        insteadRecordService.saveInsteadRecordStatus(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN);

        return businLicenceTmp;
    }

    /**
     * 检查并返回变更申请
     */
    public CustChangeApply checkChangeApply(final Long anApplyId, final String... anBusinStatus) {
        BTAssert.notNull(anApplyId, "变更申请-编号 不能为空");
        final CustChangeApply changeApply = changeApplyService.findChangeApply(anApplyId);
        if (BetterStringUtils.equals(changeApply.getChangeItem(), CustomerConstants.ITEM_BUSINLICENCE) == false) {
            throw new BytterTradeException(20074, "变更申请类型不匹配!");
        }
        return changeApply;
    }

    /**
     * 检查并返回代录记录
     */
    private CustInsteadRecord checkInsteadRecord(final CustMechBusinLicenceTmp anBusinLicenceTmp, final Long anInsteadRecordId, final String... anBusinStatus) {
        BTAssert.notNull(anBusinLicenceTmp, "营业执照信息流水信息不允许为空！");
        BTAssert.notNull(anInsteadRecordId, "代录记录编号不允许为空！");

        final CustInsteadRecord insteadRecord = insteadRecordService.findInsteadRecord(anInsteadRecordId);
        BTAssert.notNull(insteadRecord, "没有找到对应的代录记录");

        final String insteadItem = insteadRecord.getInsteadItem();
        if (BetterStringUtils.equals(insteadItem, CustomerConstants.ITEM_BUSINLICENCE) == false) {
            throw new BytterTradeException(20072, "代录项目不匹配！");
        }

        final List<String> businStatus = Arrays.asList(anBusinStatus);
        if (businStatus.contains(insteadRecord.getBusinStatus()) == false) {
            throw new BytterTradeException(20071, "此代录项目状态不正确！");
        }

        final Long applyId = insteadRecord.getApplyId();
        final CustInsteadApply insteadApply = insteadApplyService.findCustInsteadApply(applyId);

        final List<String> applyAllowStatus = Arrays.asList(new String[] {
                CustomerConstants.INSTEAD_APPLY_STATUS_AUDIT_PASS,
                CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_REJECT,
                CustomerConstants.INSTEAD_APPLY_STATUS_CONFIRM_REJECT });
        if (applyAllowStatus.contains(insteadApply.getBusinStatus()) == false) {
            throw new BytterTradeException(20073, "此代录申请状态不正确！");
        }

        return insteadRecord;
    }

    /**
     * 保存流水信息状态并返回
     */
    private CustMechBusinLicenceTmp saveBusinLicenceTmpStatus(final Long anId, final String anBusinStatus) {
        BTAssert.notNull(anId, "编号不允许为空");
        BTAssert.notNull(anBusinStatus, "状态不允许为空");

        final CustMechBusinLicenceTmp businLicenceTmp = this.selectByPrimaryKey(anId);
        BTAssert.notNull(businLicenceTmp, "没有找到临时流水信息！");

        businLicenceTmp.initModifyValue(anBusinStatus);
        this.updateByPrimaryKeySelective(businLicenceTmp);

        return businLicenceTmp;
    }

    @Override
    public void saveFormalData(final Long anId) {
        BTAssert.notNull(anId, "编号不允许为空！");
        final CustMechBusinLicenceTmp businLicenceTmp = Collections3.getFirst(this.selectByProperty("parentId", anId));

        final CustMechBusinLicenceTmp tempBusinLicenceTmp = saveBusinLicenceTmpStatus(businLicenceTmp.getId(), CustomerConstants.TMP_STATUS_USED);

        businLicenceService.saveBusinLicence(tempBusinLicenceTmp);
    }

    private void saveBusinLicenceTmpParentId(final Long anId, final Long anParentId) {
        final CustMechBusinLicenceTmp businLicenceTmp = this.selectByPrimaryKey(anId);
        BTAssert.notNull(businLicenceTmp, "没有找到临时流水信息！");
        businLicenceTmp.setParentId(anParentId);

        this.updateByPrimaryKeySelective(businLicenceTmp);
    }

    /**
     * 回写作废记录
     *
     * @param anTmpIds
     */
    @Override
    public void saveCancelData(final Long anId) {

    }
}