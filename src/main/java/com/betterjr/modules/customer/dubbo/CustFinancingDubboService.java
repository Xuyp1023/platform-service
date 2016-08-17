package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.customer.ICustFinancingService;
import com.betterjr.modules.customer.entity.CustFinancing;
import com.betterjr.modules.customer.service.CustFinancingService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

/**
 * 融资情况
 * 
 * @author liuwl
 *
 */
@Service(interfaceClass = ICustFinancingService.class)
public class CustFinancingDubboService implements ICustFinancingService {
    
    @Autowired
    private CustFinancingService custFinancingService;

    @Override
    public String webAddFinancing(Map<String, Object> anParam) {
        CustFinancing anFinanceing = (CustFinancing)RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("融资信息添加成功" , custFinancingService.addCustFinancing(anFinanceing)).toJson();
    }


    @Override
    public String webQueryFinancingList(Long anCustNo, String anFlag, int anPageNum, int anPageSize) {
        return AjaxObject.newOk("融资信息查询成功", custFinancingService.queryCustFinancingByCustNo(anCustNo, anFlag, anPageNum, anPageSize)).toJson();
    }

    @Override
    public String webSaveFinancing(Map<String, Object> anParam, Long anId) {
        CustFinancing anFinancing = (CustFinancing)RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("融资信息保存成功", custFinancingService.saveCustFinancing(anFinancing, anId)).toJson();
    }
    
    @Override
    public String webSaveDeleteFinancing(Long anId) {
        return AjaxObject.newOk("融资信息删除成功", custFinancingService.saveDeleteCustFinancing(anId)).toJson();
    }

}
