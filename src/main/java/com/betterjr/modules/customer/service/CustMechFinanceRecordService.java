package com.betterjr.modules.customer.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.customer.dao.CustMechFinanceRecordMapper;
import com.betterjr.modules.customer.entity.CustMechFinanceRecord;
import com.betterjr.modules.document.ICustFileService;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechFinanceRecordService extends BaseService<CustMechFinanceRecordMapper, CustMechFinanceRecord> {

    @Reference(interfaceClass = ICustFileService.class)
    private ICustFileService custFileService;
    
    /**
     * 财务上传记录列表
     * @param anCustNo
     * @return
     */
    public Page<CustMechFinanceRecord> queryCustMechFinanceRecord(Long anCustNo, String anFlag, int anPageNum, int anPageSize) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("custNo", anCustNo);
        Page<CustMechFinanceRecord> financeRecordList = selectPropertyByPage(anMap, anPageNum, anPageSize, "1".equals(anFlag));
        //补充文件信息
        for(CustMechFinanceRecord record : financeRecordList) {
            record.setFileList(custFileService.findCustFiles(record.getBatchNo()));
        }
        return financeRecordList;
    }
    
    /**
     * 查询财务上传记录信息
     */
    public CustMechFinanceRecord findCustMechFinanceRecord(Long anId) {
        BTAssert.notNull(anId, "财务上传记录编号不允许为空！");
        
        return this.selectByPrimaryKey(anId);
    }
    
    /**
     * 添加财务上传记录信息
     * @param anCustMechFinanceRecord
     * @return
     */
    public CustMechFinanceRecord addCustMechFinanceRecord(CustMechFinanceRecord anCustMechFinanceRecord) {
        BTAssert.notNull(anCustMechFinanceRecord, "财务上传记录信息不允许为空！");
        
        anCustMechFinanceRecord.initAddValue();
        this.insert(anCustMechFinanceRecord);
        return anCustMechFinanceRecord;
    }
    
    /**
     * 保存财务上传记录信息
     * @param anCustMechFinanceRecord
     * @param anId
     * @return
     */
    public CustMechFinanceRecord saveCustMechFinanceRecord(CustMechFinanceRecord anCustMechFinanceRecord, Long anId) {
        BTAssert.notNull(anId, "财务上传记录编号不允许为空！");
        BTAssert.notNull(anCustMechFinanceRecord, "财务上传记录信息不允许为空！");
        
        final CustMechFinanceRecord tempCustMechFinanceRecord = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustMechFinanceRecord, "对应的财务上传记录信息没有找到！");
        
        tempCustMechFinanceRecord.initModifyValue(anCustMechFinanceRecord);
        this.updateByPrimaryKeySelective(tempCustMechFinanceRecord);
        return tempCustMechFinanceRecord;
    }
    
    /**
     * 删除财务上传信息
     */
    public int saveDeleteCustMechFinanceRecord(Long anId) {
        BTAssert.notNull(anId, "财务上传记录编号不允许为空！");
        return this.deleteByPrimaryKey(anId);
    }
}