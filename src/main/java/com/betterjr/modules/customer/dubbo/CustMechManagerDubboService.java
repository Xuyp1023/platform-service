package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.customer.ICustMechManagerService;
import com.betterjr.modules.customer.entity.CustMechManager;
import com.betterjr.modules.customer.service.CustChangeService;
import com.betterjr.modules.customer.service.CustInsteadService;
import com.betterjr.modules.customer.service.CustMechManagerService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

/**
 * 高管
 * 
 * @author liuwl
 *
 */
@Service(interfaceClass = ICustMechManagerService.class)
public class CustMechManagerDubboService implements ICustMechManagerService {

    @Autowired
    private CustMechManagerService managerService;

    @Resource
    private CustChangeService changeService;

    @Resource
    private CustInsteadService insteadService;

    @Override
    public String webAddManager(Map<String, Object> anMap) {
        final CustMechManager custMechManager = (CustMechManager) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("查询公司高管信息成功", managerService.addCustMechManager(custMechManager)).toJson();
    }

    @Override
    public String webQueryManager(Long anCustNo) {
        return AjaxObject.newOk("查询公司高管列表成功", managerService.queryCustMechManager(anCustNo)).toJson();
    }

    @Override
    public String webFindManager(Long anId, Long anCustNo) {
        return null;
    }

    @Override
    public String webAddChangeApply(Map<String, Object> anParam, Long anCustNo, Long anId, Long anOperType) {
        return null;
    }

    @Override
    public String webFindChangeApply(Long anCustNo, Long anId) {
        return null;
    }

    @Override
    public String webQueryChangeApply(Long anCustNo) {
        return null;
    }

    @Override
    public String webConfirmChangeApply(Long anChangeId, Long anCustNo) {
        return null;
    }

    @Override
    public String webCancelChangeApply(Long anChangeId, Long anCustNo) {
        return null;
    }

    @Override
    public String webAddInsteadRecord(Map<String, Object> anParam, Long anCustNo, Long anId, Long anOperType) {
        return null;
    }

    @Override
    public String webQueryInsteadRecord(Long anInsteadId, Long anCustNo) {
        return null;
    }

    @Override
    public String webFindInsteadRecord(Long anCustNo, Long anId) {
        return null;
    }

    @Override
    public String webConfirmInsteadRecord(Long anInsteadId, Long anCustNo) {
        return null;
    }

    @Override
    public String webCancelInsteadRecord(Long anInsteadId, Long anCustNo) {
        return null;
    }

}
