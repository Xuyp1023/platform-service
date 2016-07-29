package com.betterjr.modules.customer.service;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterException;
import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.dao.CustMechBaseTmpMapper;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustInsteadApply;
import com.betterjr.modules.customer.entity.CustInsteadRecord;
import com.betterjr.modules.customer.entity.CustMechBase;
import com.betterjr.modules.customer.entity.CustMechBaseTmp;
import com.betterjr.modules.customer.helper.IFormalDataService;

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

    /**
     * 公司基本信息-流水信息-详情
     * 
     * @param anId
     * @param anCustNo
     * @return
     */
    public CustMechBaseTmp findCustMechBaseTmp(Long anInsteadRecordId) {
        BTAssert.notNull(anInsteadRecordId, "代录项目编号不允许为空！");

        CustInsteadRecord insteadRecord = insteadRecordService.findCustInsteadRecord(anInsteadRecordId);
        BTAssert.notNull(insteadRecord, "代录项目没有找到!");

        if (BetterStringUtils.equals(insteadRecord.getInsteadItem(), CustomerConstants.ITEM_BASE) == false) {
            throw new BytterException(20120, "代录项目类型不匹配!");
        }

        Long tmpId = Long.valueOf(insteadRecord.getTmpIds());

        CustMechBaseTmp custMechBaseTmp = this.selectByPrimaryKey(tmpId);
        BTAssert.notNull(custMechBaseTmp, "没有找到对应的流水信息!");

        return custMechBaseTmp;
    }

    /**
     * 公司基本信息-流水信息-添加
     * 
     * @param anCustMechBaseTmp
     * @return
     */
    public CustMechBaseTmp addCustMechBaseTmp(CustMechBaseTmp anCustMechBaseTmp, String anTmpType) {
        BTAssert.notNull(anCustMechBaseTmp, "公司基本信息-流水信息 不能为空！");
        BTAssert.notNull(anTmpType, "流水类型  不能为空！");

        anCustMechBaseTmp.setCustNo(anCustMechBaseTmp.getRefId()); //处理custNo
        anCustMechBaseTmp.initAddValue(anTmpType);
        this.insert(anCustMechBaseTmp);

        return anCustMechBaseTmp;
    }

    /**
     * 公司基本信息-流水信息-修改
     * 
     * @param anCustMechBaseTmp
     * @return
     */
    public CustMechBaseTmp saveCustMechBaseTmp(CustMechBaseTmp anCustMechBaseTmp, Long anId) {
        BTAssert.notNull(anId, "公司基本信息-流水信息 编号不允许为空！");

        final CustMechBaseTmp tempCustMechBaseTmp = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustMechBaseTmp, "公司基本信息-流水信息 没有找到！");

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

        // TODO 处理上传
        // 添加变更流水记录
        CustMechBaseTmp custMechBaseTmp = addCustMechBaseTmp(anCustMechBaseTmp, CustomerConstants.TMP_TYPE_CHANGE);

        // 发起变更申请
        changeApplyService.addChangeApply(custMechBaseTmp.getRefId(), CustomerConstants.ITEM_BASE, String.valueOf(custMechBaseTmp.getId()));

        return custMechBaseTmp;
    }

    /**
     * 公司基本信息-变更修改/重新提交
     * 
     */
    public CustMechBaseTmp saveChangeApply(CustMechBaseTmp anCustMechBaseTmp, Long anApplyId, String anFileList) {
        // 检查并获取
        CustChangeApply changeApply = checkChangeApply(anApplyId);

        Long tmpId = Long.valueOf(changeApply.getTmpIds());

        BTAssert.notNull(anCustMechBaseTmp, "公司基本信息-变更修改 不能为空");

        // TODO 处理上传
        CustMechBaseTmp custMechBaseTmp = saveCustMechBaseTmp(anCustMechBaseTmp, tmpId);

        // 重新发起变更申请
        changeApplyService.saveChangeApplyStatus(anApplyId, CustomerConstants.CHANGE_APPLY_STATUS_NEW);

        return custMechBaseTmp;
    }

    /**
     * 公司基本信息-添加代录
     * 
     * @param anCustMechBaseTmp
     * @return
     */
    public CustMechBaseTmp addInsteadRecord(CustMechBaseTmp anCustMechBaseTmp, Long anInsteadRecordId, String anFileList) {
        // 校验参数
        checkInsteadRecord(anCustMechBaseTmp, anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_NEW);

        // TODO 处理上传
        // 添加代录流水信息
        final CustMechBaseTmp custMechBaseTmp = addCustMechBaseTmp(anCustMechBaseTmp, CustomerConstants.TMP_TYPE_INSTEAD);

        // 回写代录记录
        insteadRecordService.saveCustInsteadRecord(anInsteadRecordId, String.valueOf(custMechBaseTmp.getId()));

        return custMechBaseTmp;
    }

    /**
     * 公司基本信息-修改代录
     * 
     * @param anCustMechBaseTmp
     * @return
     */
    public CustMechBaseTmp saveInsteadRecord(CustMechBaseTmp anCustMechBaseTmp, Long anInsteadRecordId, String anFileList) {
        // 校验参数 并获取代录记录 在驳回的状态下可以修改
        CustInsteadRecord insteadRecord = checkInsteadRecord(anCustMechBaseTmp, anInsteadRecordId,
                CustomerConstants.INSTEAD_RECORD_STATUS_REVIEW_REJECT, CustomerConstants.INSTEAD_RECORD_STATUS_CONFIRM_REJECT);

        Long tmpId = Long.valueOf(insteadRecord.getTmpIds());

        // TODO 处理上传
        // 修改代录流水信息
        final CustMechBaseTmp custMechBaseTmp = saveCustMechBaseTmp(anCustMechBaseTmp, tmpId);
        insteadRecordService.saveCustInsteadRecordStatus(anInsteadRecordId, CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN);

        return custMechBaseTmp;
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

        // 修改临时表记录为已使用
        final CustMechBaseTmp custMechBaseTmp = saveCustMechBaseTmpStatus(tmpId, CustomerConstants.TMP_STATUS_USED);

        // 回写正式表记录
        baseService.saveCustMechBase(custMechBaseTmp);
    }

    /**
     * 检查并返回变更申请
     * 
     * @param anApplyId
     * @return
     */
    public CustChangeApply checkChangeApply(Long anApplyId) {
        BTAssert.notNull(anApplyId, "变更申请-编号 不能为空");
        // 查询 变更申请
        CustChangeApply changeApply = changeApplyService.findChangeApply(anApplyId);
        if (BetterStringUtils.equals(changeApply.getChangeItem(), CustomerConstants.ITEM_BASE) == false) {
            throw new BytterTradeException(20074, "");
        }
        changeApply.getBusinStatus();
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
    private CustInsteadRecord checkInsteadRecord(CustMechBaseTmp anCustMechBaseTmp, Long anInsteadRecordId, String... anBusinStatus) {
        BTAssert.notNull(anCustMechBaseTmp, "客户基本信息流水信息不允许为空！");
        BTAssert.notNull(anInsteadRecordId, "代录记录编号不允许为空！");

        CustInsteadRecord insteadRecord = insteadRecordService.findCustInsteadRecord(anInsteadRecordId);
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
     * 
     * @param anCustMechBaseTmp
     * @param anBusinStatus
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

}