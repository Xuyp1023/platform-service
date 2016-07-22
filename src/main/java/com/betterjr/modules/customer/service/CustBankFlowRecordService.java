package com.betterjr.modules.customer.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.modules.customer.dao.CustBankFlowRecordMapper;
import com.betterjr.modules.customer.entity.CustBankFlowRecord;
import com.betterjr.modules.customer.entity.CustBankFlowRecord;

/**
 * 银行流水上传记录
 * @author liuwl
 *
 */
@Service
public class CustBankFlowRecordService extends BaseService<CustBankFlowRecordMapper, CustBankFlowRecord> {
    private static Logger logger = LoggerFactory.getLogger(CustBankFlowRecordService.class);

    /**
     * 查询银行流水上传记录列表
     * @param anCustNo
     * @return
     */
    public List<CustBankFlowRecord> queryCustBankFlowRecord(Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        
        return this.selectByProperty("custNo", anCustNo);
    }
    
    /**
     * 查询银行流水上传记录信息
     */
    public CustBankFlowRecord findCustBankFlowRecord(Long anId) {
        BTAssert.notNull(anId, "银行流水上传记录编号不允许为空！");
        
        return this.selectByPrimaryKey(anId);
    }
    
    /**
     * 添加银行流水上传记录信息
     * @param anCustBankFlowRecord
     * @return
     */
    public CustBankFlowRecord addCustBankFlowRecord(CustBankFlowRecord anCustBankFlowRecord) {
        BTAssert.notNull(anCustBankFlowRecord, "银行流水上传记录信息不允许为空！");
        
        anCustBankFlowRecord.initAddValue();
        this.insert(anCustBankFlowRecord);
        return anCustBankFlowRecord;
    }
    
    /**
     * 保存银行流水上传记录信息
     * @param anCustBankFlowRecord
     * @param anId
     * @return
     */
    public CustBankFlowRecord saveCustBankFlowRecord(CustBankFlowRecord anCustBankFlowRecord, Long anId) {
        BTAssert.notNull(anId, "银行流水上传记录编号不允许为空！");
        BTAssert.notNull(anCustBankFlowRecord, "银行流水上传记录信息不允许为空！");
        
        final CustBankFlowRecord tempCustBankFlowRecord = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustBankFlowRecord, "对应的银行流水上传记录信息没有找到！");
        
        tempCustBankFlowRecord.initModifyValue(anCustBankFlowRecord);
        this.updateByPrimaryKeySelective(tempCustBankFlowRecord);
        return tempCustBankFlowRecord;
    }
}