package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.customer.ICustMechCooperationService;
import com.betterjr.modules.customer.entity.CustMechCooperation;
import com.betterjr.modules.customer.service.CustMechCooperationService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

/**
 * 合作企业
 * 
 * @author liuwl
 *
 */
@Service(interfaceClass = ICustMechCooperationService.class)
public class CustMechCooperationDubboService implements ICustMechCooperationService {

    @Autowired
    private CustMechCooperationService custMechCooperationService;

    @Override
    public String webAddCooperation(Map<String, Object> anMap) {
        CustMechCooperation custMechCooperation = (CustMechCooperation) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("合作企业信息添加成功", custMechCooperationService.addCustMechCooperation(custMechCooperation))
                .toJson();
    }

    @Override
    public String webQueryCooperationList(Long anCustNo, String anFlag, int anPageNum, int anPageSize) {

        return AjaxObject.newOkWithPage("合作企业信息查询成功",
                custMechCooperationService.queryCustMechCooperationByCustNo(anCustNo, anFlag, anPageNum, anPageSize))
                .toJson();
    }

    @Override
    public String webSaveCooperation(Map<String, Object> anParam, Long anId) {
        CustMechCooperation custMechCooperation = (CustMechCooperation) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject
                .newOk("合作企业信息保存成功", custMechCooperationService.saveCustMechCooperation(custMechCooperation, anId))
                .toJson();
    }

    @Override
    public String webSaveDeleteCooperation(Long anId) {
        return AjaxObject.newOk("合作企业信息删除成功", custMechCooperationService.saveDeleteCustMechCooperation(anId)).toJson();
    }
}
