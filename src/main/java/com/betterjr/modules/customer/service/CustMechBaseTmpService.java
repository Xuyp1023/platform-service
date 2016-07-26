package com.betterjr.modules.customer.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.dao.CustMechBaseTmpMapper;
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
    private CustChangeApplyService changeApplyService;

    /**
     * 查询客户基本信息流水信息
     * 
     * @param anId
     * @param anCustNo
     * @return
     */
    public CustMechBaseTmp findCustMechBaseTmp(Long anId) {
        BTAssert.notNull(anId, "客户基本信息流水信息编号不允许为空！");
        final CustMechBaseTmp custMechBaseTmp = this.selectByPrimaryKey(anId);
        BTAssert.notNull(custMechBaseTmp, "没有找到对应的流水记录！");

        return custMechBaseTmp;
    }

    /**
     * 保存客户基本信息流水信息
     * 
     * @param anCustMechBaseTmp
     * @return
     */
    public int saveCustMechBaseTmp(CustMechBaseTmp anCustMechBaseTmp, Long anId) {
        BTAssert.notNull(anId, "客户基本信息流水编号不允许为空！");
        final CustMechBaseTmp tempCustMechBaseTmp = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustMechBaseTmp, "没有找到对应的客户基本信息流水信息！");

        tempCustMechBaseTmp.initModifyValue(anCustMechBaseTmp);
        return this.updateByPrimaryKey(tempCustMechBaseTmp);
    }

    /**
     * 添加客户基本信息流水信息
     * 
     * 在初次添加基本信息时也需要添加
     * 
     * @param anCustMechBaseTmp
     * @return
     */
    public CustMechBaseTmp addCustMechBaseTmpByChange(CustMechBaseTmp anCustMechBaseTmp, String anTmpType) {
        BTAssert.notNull(anCustMechBaseTmp, "客户基本信息流水信息编号不允许为空！");
        // 处理version 查询变更详情的时候使用

        anCustMechBaseTmp.initAddValue(anTmpType);
        this.insert(anCustMechBaseTmp);

        // 回写
        return anCustMechBaseTmp;
    }

    /**
     * 添加客户基本信息流水信息
     * 
     * 在初次添加基本信息时也需要添加
     * 
     * @param anCustMechBaseTmp
     * @return
     */
    public CustMechBaseTmp addCustMechBaseTmpByInstead(CustMechBaseTmp anCustMechBaseTmp, Long anInsteadRecordId, String anTmpType) {
        BTAssert.notNull(anCustMechBaseTmp, "客户基本信息流水信息编号不允许为空！");
        BTAssert.notNull(anInsteadRecordId, "代录记录编号不允许为空！");
        // 处理version 查询变更详情的时候使用

        CustInsteadRecord custInsteadRecord = insteadRecordService.findCustInsteadRecord(anInsteadRecordId);
        BTAssert.notNull(custInsteadRecord, "没有找到对应的代录记录");

        String insteadItem = custInsteadRecord.getInsteadItem();
        if (BetterStringUtils.equals(insteadItem, CustomerConstants.ITEM_BASE) == false) {
            throw new BytterTradeException(20072, "代录项目不匹配！");
        }

        if (BetterStringUtils.equals(custInsteadRecord.getBusinStatus(), CustomerConstants.INSTEAD_RECORD_STATUS_NEW) == false) {
            throw new BytterTradeException(20071, "此代录项目已经代录，不允许重复代录！");
        }

        anCustMechBaseTmp.initAddValue(anTmpType);
        this.insert(anCustMechBaseTmp);

        // 回写代录记录
        insteadRecordService.saveCustInsteadRecord(anInsteadRecordId, String.valueOf(anCustMechBaseTmp.getId()));
        return anCustMechBaseTmp;
    }

    /**
     * 保存数据至正式表
     */
    @Override
    public void saveFormalData(String ... anTmpIds) {
        BTAssert.notEmpty(anTmpIds, "临时流水编号不允许为空！");

        if (anTmpIds.length != 1) {
            throw new BytterTradeException(20021, "临时流水编号只能有一位！");
        }

        Long tmpId = Long.valueOf(anTmpIds[0]);

        CustMechBaseTmp mechBaseTmp = this.selectByPrimaryKey(tmpId);
        BTAssert.notNull(mechBaseTmp, "没有找到临时流水信息！");

        Long custNo = mechBaseTmp.getRefId();
        CustMechBase custMechBase = baseService.findCustMechBase(custNo);
        custMechBase.initModifyValue(mechBaseTmp);
        baseService.saveCustMechBase(custMechBase, custNo);
    }
}