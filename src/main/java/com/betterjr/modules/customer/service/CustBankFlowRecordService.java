package com.betterjr.modules.customer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.customer.dao.CustBankFlowRecordMapper;
import com.betterjr.modules.customer.entity.CustBankFlowRecord;
import com.betterjr.modules.customer.entity.CustMechTradeRecord;
import com.betterjr.modules.document.ICustFileService;
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
    private CustFileItemService fileItemService;
    
    @Reference(interfaceClass = ICustFileService.class)
    private ICustFileService custFileService;
    
    /**
     * 查询银行流水上传记录列表
     * 
     * @param anCustNo
     * @return
     */
    public Page<CustBankFlowRecord> queryCustBankFlowRecord(Long anCustNo, String anFlag, int anPageNum, int anPageSize) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        
        Page<CustBankFlowRecord> recordList = this.selectPropertyByPage("custNo", anCustNo, anPageNum, anPageSize, "1".equals(anFlag));
        //补充文件信息
        for(CustBankFlowRecord record : recordList) {
            record.setFileList(custFileService.findCustFiles(record.getBatchNo()));
        }
        return recordList;
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
     * @param anBankFlowRecord
     * @return
     */
    public CustBankFlowRecord addCustBankFlowRecord(CustBankFlowRecord anBankFlowRecord, String anFileList) {
        BTAssert.notNull(anBankFlowRecord, "银行流水上传记录信息不允许为空！");
        BTAssert.notNull(anFileList, "银行流水上传文件不允许为空！");
        anBankFlowRecord.initAddValue();
        anBankFlowRecord.setBatchNo(fileItemService.updateCustFileItemInfo(anFileList, anBankFlowRecord.getBatchNo()));
        this.insert(anBankFlowRecord);
        return anBankFlowRecord;
    }
    
    /**
     * 删除银行流水上传记录信息
     */
    public int saveDeleteBankFlowRecord(Long anId) {
        BTAssert.notNull(anId, "银行流水上传记录编号不允许为空！");
        return this.deleteByPrimaryKey(anId);
    }
    
}