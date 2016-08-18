package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.customer.ICustBankFlowService;
import com.betterjr.modules.customer.entity.CustBankFlowRecord;
import com.betterjr.modules.customer.service.CustBankFlowRecordService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

/**
 * 银行流水
 * @author liuwl
 *
 */
@Service(interfaceClass = ICustBankFlowService.class)
public class CustBankFlowDubboService implements ICustBankFlowService {

    @Autowired
    private CustBankFlowRecordService custBankFlowRecordService;
    
    @Override
    public String webAddBankFlowRecord(Map<String, Object> anParam, String anFileList) {
        CustBankFlowRecord anCustBankFlowRecord = (CustBankFlowRecord) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("添加银行流水记录成功", custBankFlowRecordService.addCustBankFlowRecord(anCustBankFlowRecord, anFileList)).toJson();
    }

    @Override
    public String webQueryBankFlowRecordList(Long anCustNo, String anFlag, int anPageNum, int anPageSize) {
        return AjaxObject.newOkWithPage("查询银行流水记录成功", custBankFlowRecordService.queryCustBankFlowRecord(anCustNo, anFlag, anPageNum, anPageSize)).toJson();
    }

    @Override
    public String webSaveDeleteBankFlowRecord(Long anId) {
        return AjaxObject.newOk("银行流水记录删除成功", custBankFlowRecordService.saveDeleteBankFlowRecord(anId)).toJson();
    }

}
