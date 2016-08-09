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
import com.betterjr.modules.customer.dao.CustMechLawTmpMapper;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustInsteadApply;
import com.betterjr.modules.customer.entity.CustInsteadRecord;
import com.betterjr.modules.customer.entity.CustMechLaw;
import com.betterjr.modules.customer.entity.CustMechLawTmp;
import com.betterjr.modules.customer.helper.IFormalDataService;
import com.betterjr.modules.customer.helper.VersionHelper;

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
     * @param anCustMechLawTmp
     * @return
     */
    public CustMechLawTmp findCustMechLawTmpPrevVersion(CustMechLawTmp anCustMechLawTmp) {
        Long refId = anCustMechLawTmp.getRefId();
        Long version = anCustMechLawTmp.getVersion();
        
        Long befVersion = this.mapper.selectPrevVersion(refId, version);
        
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("version", befVersion);
        conditionMap.put("refId", refId);
        
        List<CustMechLawTmp> befDatas =  this.selectByProperty(conditionMap);
        return Collections3.getFirst(befDatas);
    }
    
    /**
     * 法人信息-流水信息-详情
     */
    public CustMechLawTmp findCustMechLawTmpByInsteadRecord(Long anInsteadRecordId) {
        BTAssert.notNull(anInsteadRecordId, "代录项目编号不允许为空！");

        CustInsteadRecord insteadRecord = insteadRecordService.findCustInsteadRecord(anInsteadRecordId);
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
    public CustMechLawTmp addCustMechLawTmp(CustMechLawTmp anCustMechLawTmp, String anTmpType) {
        BTAssert.notNull(anCustMechLawTmp, "法人信息-流水信息  不能为空！");
        BTAssert.notNull(anTmpType, "流水类型  不能为空！");

        final Long custNo = anCustMechLawTmp.getRefId();
        anCustMechLawTmp.setCustNo(anCustMechLawTmp.getRefId());
        final String custName = custAccountService.queryCustName(custNo);
        anCustMechLawTmp.initAddValue(anTmpType, custNo, custName);

        anCustMechLawTmp.setVersion(VersionHelper.generateVersion(this.mapper, custNo));
        this.insert(anCustMechLawTmp);

        return anCustMechLawTmp;
    }

    /**
     * 法人信息-流水信息-添加 从法人信息建立流水
     */
    public CustMechLawTmp addCustMechLawTmp(CustMechLaw anCustMechLaw) {
        BTAssert.notNull(anCustMechLaw, "公司法人信息 不能为空！");

        final CustMechLawTmp custMechLawTmp = new CustMechLawTmp();

        // 初始数据一开始为 已使用
        custMechLawTmp.initAddValue(anCustMechLaw, CustomerConstants.TMP_TYPE_INITDATA, CustomerConstants.TMP_STATUS_USED);
        custMechLawTmp.setVersion(VersionHelper.generateVersion(this.mapper, anCustMechLaw.getCustNo()));
        custMechLawTmp.setRefId(anCustMechLaw.getCustNo());

        this.insert(custMechLawTmp);

        return custMechLawTmp;
    }

    /**
     * 法人信息-流水信息-修改
     */
    public CustMechLawTmp saveCustMechLawTmp(CustMechLawTmp anCustMechLawTmp, Long anId) {
        BTAssert.notNull(anId, "法人信息-流水信息 编号不允许为空！");

        final CustMechLawTmp tempCustMechLawTmp = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustMechLawTmp, "没有找到对应的公司法人流水信息！");

        tempCustMechLawTmp.initModifyValue(anCustMechLawTmp);
        this.updateByPrimaryKey(tempCustMechLawTmp);

        return tempCustMechLawTmp;
    }

    /**
     * 法人信息-流水信息-变更申请
     */
    public CustMechLawTmp addChangeApply(CustMechLawTmp anCustMechLawTmp, String anFileList) {
        BTAssert.notNull(anCustMechLawTmp, "基本信息变更申请不能为空");

        // TODO 文件上传
        // 添加变更流水记录
        CustMechLawTmp custMechLawTmp = addCustMechLawTmp(anCustMechLawTmp, CustomerConstants.TMP_TYPE_CHANGE);

        // 发起变更申请
        changeApplyService.addChangeApply(custMechLawTmp.getRefId(), CustomerConstants.ITEM_LAW, String.valueOf(custMechLawTmp.getId()));

        return custMechLawTmp;
    }

    /**
     * 法人信息-流水信息-变更修改/重新提交
     */
    public CustMechLawTmp saveChangeApply(CustMechLawTmp anCustMechLawTmp, Long anApplyId, String anFileList) {
        // 检查并获取
        CustChangeApply changeApply = checkChangeApply(anApplyId);

        Long tmpId = Long.valueOf(changeApply.getTmpIds());
        BTAssert.notNull(anCustMechLawTmp, "公司基本信息-变更修改 不能为空");

        // TODO 文件上传
        CustMechLawTmp custMechLawTmp = saveCustMechLawTmp(anCustMechLawTmp, tmpId);

        // 重新发起变更申请
        changeApplyService.saveChangeApplyStatus(anApplyId, CustomerConstants.CHANGE_APPLY_STATUS_NEW);

        return custMechLawTmp;
    }

    /**
     * 法人流水信息-添加代录
     */
    public CustMechLawTmp addInsteadRecord(CustMechLawTmp anCustMechLawTmp, Long anInsteadRecordId, String anFileList) {
        // 校验参数
        checkInsteadRecord(anCustMechLawTmp, anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW);

        // TODO 处理上传
        // 添加代录流水信息
        final CustMechLawTmp custMechLawTmp = addCustMechLawTmp(anCustMechLawTmp, CustomerConstants.TMP_TYPE_INSTEAD);

        // 回写代录记录
        insteadRecordService.saveCustInsteadRecord(anInsteadRecordId, String.valueOf(custMechLawTmp.getId()));

        return custMechLawTmp;
    }

    /**
     * 法人流水信息-修改代录
     * 
     * @param anCustMechBaseTmp
     * @return
     */
    public CustMechLawTmp saveInsteadRecord(CustMechLawTmp anCustMechLawTmp, Long anInsteadRecordId, String anFileList) {
        // 校验参数 并获取代录记录 在驳回的状态下可以修改
        CustInsteadRecord insteadRecord = checkInsteadRecord(anCustMechLawTmp, anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN,
                CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT, CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        Long tmpId = Long.valueOf(insteadRecord.getTmpIds());

        // TODO 处理上传
        // 修改代录流水信息
        final CustMechLawTmp custMechLawTmp = saveCustMechLawTmp(anCustMechLawTmp, tmpId);

        insteadRecordService.saveCustInsteadRecordStatus(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN);

        return custMechLawTmp;
    }


    /**
     * 检查并返回变更申请
     * 
     * @param anApplyId
     * @return
     */
    public CustChangeApply checkChangeApply(Long anApplyId, String... anBusinStatus) {
        BTAssert.notNull(anApplyId, "变更申请-编号 不能为空");
        // 查询 变更申请
        CustChangeApply changeApply = changeApplyService.findChangeApply(anApplyId);
        if (BetterStringUtils.equals(changeApply.getChangeItem(), CustomerConstants.ITEM_LAW) == false) {
            throw new BytterTradeException(20074, "");
        }
        return changeApply;
    }

    /**
     * 检查并返回代录记录
     * 
     * @param anCustMechBaseTmp
     * @param anInsteadRecordId
     * @param anBusinStatus
     * @return
     */
    private CustInsteadRecord checkInsteadRecord(CustMechLawTmp anCustMechLawTmp, Long anInsteadRecordId, String... anBusinStatus) {
        BTAssert.notNull(anCustMechLawTmp, "法人信息流水信息不允许为空！");
        BTAssert.notNull(anInsteadRecordId, "代录记录编号不允许为空！");

        CustInsteadRecord insteadRecord = insteadRecordService.findCustInsteadRecord(anInsteadRecordId);
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
     * 
     * @param anCustMechBaseTmp
     * @param anBusinStatus
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

    
    /**
     * 保存数据至正式表
     */
    @Override
    public void saveFormalData(String... anTmpIds) {
        BTAssert.notEmpty(anTmpIds, "临时流水编号不允许为空！");

        if (anTmpIds.length != 1) {
            throw new BytterTradeException(20021, "临时流水编号只能有一位！");
        }

        Long tmpId = Long.valueOf(anTmpIds[0]);

        final CustMechLawTmp custMechLawTmp = saveCustMechLawTmpStatus(tmpId, CustomerConstants.TMP_STATUS_USED);

        // 根据 类型区别保存数据方式
        lawService.saveCustMechLaw(custMechLawTmp);
    }
    
    /**
     * 回写作废记录
     * @param anTmpIds
     */
    @Override
    public void saveCancelData(String... anTmpIds) {
        
    }
}
