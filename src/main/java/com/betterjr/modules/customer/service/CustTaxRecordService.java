package com.betterjr.modules.customer.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.modules.customer.dao.CustTaxRecordMapper;
import com.betterjr.modules.customer.entity.CustTaxRecord;

/**
 * 纳税记录上传记录
 * @author liuwl
 *
 */
@Service
public class CustTaxRecordService extends BaseService<CustTaxRecordMapper, CustTaxRecord> {
    private static Logger logger = LoggerFactory.getLogger(CustTaxRecordService.class);
    
    /**
     * 纳税记录上传记录列表
     * @param anCustNo
     * @return
     */
    public List<CustTaxRecord> queryCustTaxRecord(Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        
        return this.selectByProperty("custNo", anCustNo);
    }
    
    /**
     * 查询纳税记录上传记录信息
     */
    public CustTaxRecord findCustTaxRecord(Long anId) {
        BTAssert.notNull(anId, "纳税记录上传记录编号不允许为空！");
        
        return this.selectByPrimaryKey(anId);
    }
    
    /**
     * 添加纳税记录上传记录信息
     * @param anCustTaxRecord
     * @return
     */
    public CustTaxRecord addCustTaxRecord(CustTaxRecord anCustTaxRecord) {
        BTAssert.notNull(anCustTaxRecord, "纳税记录上传记录信息不允许为空！");
        
        anCustTaxRecord.initAddValue();
        this.insert(anCustTaxRecord);
        return anCustTaxRecord;
    }
    
    /**
     * 保存纳税记录上传记录信息
     * @param anCustTaxRecord
     * @param anId
     * @return
     */
    public CustTaxRecord saveCustTaxRecord(CustTaxRecord anCustTaxRecord, Long anId) {
        BTAssert.notNull(anId, "纳税记录上传记录编号不允许为空！");
        BTAssert.notNull(anCustTaxRecord, "纳税记录上传记录信息不允许为空！");
        
        final CustTaxRecord tempCustTaxRecord = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustTaxRecord, "对应的纳税记录上传记录信息没有找到！");
        
        tempCustTaxRecord.initModifyValue(anCustTaxRecord);
        this.updateByPrimaryKeySelective(tempCustTaxRecord);
        return tempCustTaxRecord;
    } 
}
