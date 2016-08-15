package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.customer.ICustMechFinanceService;
import com.betterjr.modules.customer.entity.CustMechFinanceRecord;
import com.betterjr.modules.customer.service.CustMechFinanceRecordService;
import com.betterjr.modules.document.service.CustFileItemService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

/**
 * 财务上传记录
 * 
 * @author liuwl
 *
 */
@Service(interfaceClass = ICustMechFinanceService.class)
public class CustMechFinanceDubboService implements ICustMechFinanceService {

    @Autowired
    private CustMechFinanceRecordService custMechFinanceRecordService;
    
    @Autowired
    private CustFileItemService custFileItemService;
    
    @Override
    public String webAddFinanceInfo(Map<String, Object> anParam, Long anCustNo, String anFileList) {
        CustMechFinanceRecord anCustMechFinanceRecord = (CustMechFinanceRecord) RuleServiceDubboFilterInvoker.getInputObj();
        anCustMechFinanceRecord.setBatchNo(custFileItemService.updateCustFileItemInfo(anFileList, anCustMechFinanceRecord.getBatchNo()));
        return AjaxObject.newOk("财务信息添加成功", custMechFinanceRecordService.addCustMechFinanceRecord(anCustMechFinanceRecord)).toJson();
    }

    @Override
    public String webFindFinanceInfo(Long anCustNo, Long anId) {
        return AjaxObject.newOk("财务信息查询成功", custMechFinanceRecordService.findCustMechFinanceRecord(anId)).toJson();
    }

    @Override
    public String webQueryFinanceList(Long anCustNo, String anFlag, int anPageNum, int anPageSize) {
        return AjaxObject.newOk("财务上传记录查询成功", custMechFinanceRecordService.queryCustMechFinanceRecord(anCustNo, anFlag, anPageNum, anPageSize)).toJson(); 
    }

    @Override
    public String webSaveFinanceInfo(Map<String, Object> anParam, Long anCustNo, Long anId, String anFileList) {
        CustMechFinanceRecord anCustMechFinanceRecord = (CustMechFinanceRecord) RuleServiceDubboFilterInvoker.getInputObj();
        anCustMechFinanceRecord.setBatchNo(custFileItemService.updateCustFileItemInfo(anFileList, anCustMechFinanceRecord.getBatchNo()));
        return AjaxObject.newOk("财务上传记录保存成功", custMechFinanceRecordService.saveCustMechFinanceRecord(anCustMechFinanceRecord, anId)).toJson();
    }

}
