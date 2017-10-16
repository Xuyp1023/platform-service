package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.customer.ICustMechTradeService;
import com.betterjr.modules.customer.entity.CustMechTradeRecord;
import com.betterjr.modules.customer.service.CustMechTradeRecordService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

/**
 * 贸易信息
 * 
 * @author liuwl
 *
 */
@Service(interfaceClass = ICustMechTradeService.class)
public class CustMechTradeDubboService implements ICustMechTradeService {

    @Autowired
    private CustMechTradeRecordService custMechTradeRecordService;

    @Override
    public String webAddTradeRecord(Map<String, Object> anParam, String anFileList) {
        CustMechTradeRecord anCustMechTradeRecord = (CustMechTradeRecord) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject
                .newOk("新增贸易记录成功", custMechTradeRecordService.addCustMechTradeRecord(anCustMechTradeRecord, anFileList))
                .toJson();
    }

    @Override
    public String webQueryTradeRecordList(Long anCustNo, String anFlag, int anPageNum, int anPageSize) {
        return AjaxObject
                .newOkWithPage("查询贸易记录成功",
                        custMechTradeRecordService.queryCustMechTradeRecord(anCustNo, anFlag, anPageNum, anPageSize))
                .toJson();
    }

    @Override
    public String webSaveDeleteTradeRecord(Long anId) {
        return AjaxObject.newOk("删除贸易记录成功", custMechTradeRecordService.saveDeleteCustMechTradeRecord(anId)).toJson();
    }

}
