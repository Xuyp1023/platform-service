package com.betterjr.modules.customer.service;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.customer.dao.CustMechTradeRecordMapper;
import com.betterjr.modules.customer.entity.CustMechTradeRecord;
import com.betterjr.modules.document.service.CustFileItemService;

/**
 * 贸易记录上传记录
 * @author liuwl
 *
 */
@Service
public class CustMechTradeRecordService extends BaseService<CustMechTradeRecordMapper, CustMechTradeRecord> {

    private CustFileItemService custFileItemService;
    
    /**
     * 查询贸易记录上传记录列表
     * @param anCustNo
     * @return
     */
    public Page<CustMechTradeRecord> queryCustMechTradeRecord(Long anCustNo, String anFlag, int anPageNum, int anPageSize) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        
        return this.selectPropertyByPage("custNo", anCustNo, anPageNum, anPageSize, "1".equals(anFlag));
    }
    
    /**
     * 查询贸易记录上传记录信息
     */
    public CustMechTradeRecord findCustMechTradeRecord(Long anId) {
        BTAssert.notNull(anId, "贸易记录上传记录编号不允许为空！");
        
        return this.selectByPrimaryKey(anId);
    }
    
    /**
     * 添加贸易记录上传记录信息
     * @param anCustMechTradeRecord
     * @return
     */
    public CustMechTradeRecord addCustMechTradeRecord(CustMechTradeRecord anCustMechTradeRecord, String anFileList) {
        BTAssert.notNull(anCustMechTradeRecord, "贸易记录上传记录信息不允许为空！");
        
        anCustMechTradeRecord.initAddValue();
        anCustMechTradeRecord.setBatchNo(custFileItemService.updateCustFileItemInfo(anFileList, anCustMechTradeRecord.getBatchNo()));
        this.insert(anCustMechTradeRecord);
        return anCustMechTradeRecord;
    }
    
    /**
     * 保存贸易记录上传记录信息
     * @param anCustMechTradeRecord
     * @param anId
     * @return
     */
    public CustMechTradeRecord saveCustMechTradeRecord(CustMechTradeRecord anCustMechTradeRecord, Long anId) {
        BTAssert.notNull(anId, "贸易记录上传记录编号不允许为空！");
        BTAssert.notNull(anCustMechTradeRecord, "贸易记录上传记录信息不允许为空！");
        
        final CustMechTradeRecord tempCustMechTradeRecord = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustMechTradeRecord, "对应的贸易记录上传记录信息没有找到！");
        
        tempCustMechTradeRecord.initModifyValue(anCustMechTradeRecord);
        this.updateByPrimaryKeySelective(tempCustMechTradeRecord);
        return tempCustMechTradeRecord;
    }
    
    /**
     * 删除贸易记录上传信息
     */
    public int saveDeleteCustMechTradeRecord(Long anId) {
        BTAssert.notNull(anId, "贸易记录上传记录编号不允许为空！");
        return this.deleteByPrimaryKey(anId);
    }
}