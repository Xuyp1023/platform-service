package com.betterjr.modules.customer.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.modules.customer.dao.CustMechTradeRecordMapper;
import com.betterjr.modules.customer.entity.CustMechTradeRecord;
import com.betterjr.modules.customer.entity.CustMechTradeRecord;

/**
 * 贸易记录上传记录
 * @author liuwl
 *
 */
@Service
public class CustMechTradeRecordService extends BaseService<CustMechTradeRecordMapper, CustMechTradeRecord> {
    private static Logger logger = LoggerFactory.getLogger(CustMechTradeRecordService.class);

    /**
     * 贸易记录上传记录列表
     * @param anCustNo
     * @return
     */
    public List<CustMechTradeRecord> queryCustMechTradeRecord(Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        
        return this.selectByProperty("custNo", anCustNo);
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
    public CustMechTradeRecord addCustMechTradeRecord(CustMechTradeRecord anCustMechTradeRecord) {
        BTAssert.notNull(anCustMechTradeRecord, "贸易记录上传记录信息不允许为空！");
        
        anCustMechTradeRecord.initAddValue();
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
}