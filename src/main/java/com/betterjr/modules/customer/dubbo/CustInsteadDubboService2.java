package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.customer.ICustInsteadService2;
import com.betterjr.modules.customer.service.CustInstead2Service;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

@Service(interfaceClass = ICustInsteadService2.class)
public class CustInsteadDubboService2 implements ICustInsteadService2 {
    @Resource
    private CustInstead2Service insteadService;
    
    @Override
    public String webAddInsteadApply(final Map<String, Object> anParam, final String anFileList) {
        final Map<String, Object> param = RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("申请代录成功", insteadService.addInsteadApply(param, anFileList)).toJson();
    }
}
