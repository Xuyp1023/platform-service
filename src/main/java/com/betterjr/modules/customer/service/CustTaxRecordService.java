package com.betterjr.modules.customer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.customer.dao.CustTaxRecordMapper;
import com.betterjr.modules.customer.entity.CustMechFinanceRecord;
import com.betterjr.modules.customer.entity.CustTaxRecord;
import com.betterjr.modules.document.ICustFileService;

/**
 * 纳税记录上传记录
 * @author liuwl
 *
 */
@Service
public class CustTaxRecordService extends BaseService<CustTaxRecordMapper, CustTaxRecord> {
    
    @Reference(interfaceClass = ICustFileService.class)
    private ICustFileService custFileService;
    
    /**
     * 纳税记录上传记录列表
     * @param anCustNo
     * @return
     */
    public List<CustTaxRecord> findCustTaxRecordList(Long anCustNo) {
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
     * 根据custNo分页查询上传记录列表
     */
    public Page<CustTaxRecord> queryCustTaxRecordList(Long anCustNo, String anFlag, int anPageNum, int anPageSize) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("custNo", anCustNo);
        Page<CustTaxRecord> recordList = this.selectPropertyByPage(anMap, anPageNum, anPageSize, "1".equals(anFlag));
        //补充文件信息
        for(CustTaxRecord record : recordList) {
            record.setFileList(custFileService.findCustFiles(record.getBatchNo()));
        }
        return recordList;
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
    
    /**
     * 删除纳税信息
     */
    public int saveDeleteCustTaxRecorde (Long anId){
        BTAssert.notNull(anId, "纳税记录上传记录编号不允许为空！");
        return this.deleteByPrimaryKey(anId);
    }
}
