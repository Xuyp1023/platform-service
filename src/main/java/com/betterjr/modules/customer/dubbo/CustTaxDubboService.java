package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.customer.ICustTaxService;
import com.betterjr.modules.customer.entity.CustTaxRecord;
import com.betterjr.modules.customer.service.CustTaxRecordService;
import com.betterjr.modules.document.ICustFileService;
import com.betterjr.modules.document.service.CustFileItemService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

/**
 * 纳税服务
 * 
 * @author liuwl
 *
 */
@Service(interfaceClass = ICustTaxService.class)
public class CustTaxDubboService implements ICustTaxService {
    
    @Autowired
    private CustTaxRecordService custTaxRecordService;
    
    @Autowired
    private CustFileItemService custFileItemService;

    @Override
    public String webAddTaxRecord(Map<String, Object> anParam, String anFileList) {
        CustTaxRecord anCustTaxRecord = (CustTaxRecord) RuleServiceDubboFilterInvoker.getInputObj();
        anCustTaxRecord.setBatchNo(custFileItemService.updateCustFileItemInfo(anFileList, anCustTaxRecord.getBatchNo()));
        return AjaxObject.newOk("纳税信息录入成功", custTaxRecordService.addCustTaxRecord(anCustTaxRecord)).toJson();
    }

    @Override
    public String webFindTaxRecord(Long anId) {
        return AjaxObject.newOk("查询纳税信息成功", custTaxRecordService.findCustTaxRecord(anId)).toJson();
    }

    @Override
    public String webQueryTaxRecordList(Long anCustNo, String anFlag, int anPageNum, int anPageSize) {
        return AjaxObject.newOkWithPage("纳税信息查询成功", custTaxRecordService.queryCustTaxRecordList(anCustNo, anFlag, anPageNum, anPageSize)).toJson();
    }

    @Override
    public String webSaveTaxRecord(Map<String, Object> anParam, Long anId, String anFileList) {
        CustTaxRecord anCustTaxRecord = (CustTaxRecord) RuleServiceDubboFilterInvoker.getInputObj();
        anCustTaxRecord.setBatchNo(custFileItemService.updateCustFileItemInfo(anFileList, anCustTaxRecord.getBatchNo()));
        return AjaxObject.newOk(custTaxRecordService.saveCustTaxRecord(anCustTaxRecord, anId)).toJson();
    }

}
