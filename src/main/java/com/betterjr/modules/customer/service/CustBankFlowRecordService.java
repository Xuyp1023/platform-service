package com.betterjr.modules.customer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.customer.dao.CustBankFlowRecordMapper;
import com.betterjr.modules.customer.entity.CustBankFlowRecord;
import com.betterjr.modules.document.service.CustFileItemService;

/**
 * 银行流水上传记录
 * 
 * @author liuwl
 *
 */
@Service
public class CustBankFlowRecordService extends BaseService<CustBankFlowRecordMapper, CustBankFlowRecord> {

    @Autowired
    private CustFileItemService custFileItemService;
    
    /**
     * 查询银行流水上传记录列表
     * 
     * @param anCustNo
     * @return
     */
    public Page<CustBankFlowRecord> queryCustBankFlowRecord(Long anCustNo, String anFlag, int anPageNum, int anPageSize) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        
        return this.selectPropertyByPage("custNo", anCustNo, anPageNum, anPageSize, "1".equals(anFlag));
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
     * 
     * @param anCustBankFlowRecord
     * @return
     */
    public CustBankFlowRecord addCustBankFlowRecord(CustBankFlowRecord anCustBankFlowRecord, String anFileList) {
        BTAssert.notNull(anCustBankFlowRecord, "银行流水上传记录信息不允许为空！");
        BTAssert.notNull(anFileList, "银行流水上传文件不允许为空！");
        anCustBankFlowRecord.initAddValue();
        anCustBankFlowRecord.setBatchNo(custFileItemService.updateCustFileItemInfo(anFileList, anCustBankFlowRecord.getBatchNo()));
        this.insert(anCustBankFlowRecord);
        return anCustBankFlowRecord;
    }
    
    /**
     * 删除银行流水上传记录信息
     */
    public int saveDeleteBankFlowRecord(Long anId) {
        BTAssert.notNull(anId, "银行流水上传记录编号不允许为空！");
        return this.deleteByPrimaryKey(anId);
    }
    
}