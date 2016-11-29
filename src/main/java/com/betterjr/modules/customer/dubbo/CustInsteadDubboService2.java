package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.customer.ICustInsteadService2;
import com.betterjr.modules.customer.service.CustInstead2Service;

@Service(interfaceClass = ICustInsteadService2.class)
public class CustInsteadDubboService2 implements ICustInsteadService2 {
    @Resource
    private CustInstead2Service insteadService;
    
    @Override
    public String webAddInsteadApply(final String anCustName, final Long anOperId, final String anFileList) {
        return AjaxObject.newOk("申请代录成功", insteadService.addInsteadApply(anCustName, anOperId, anFileList)).toJson();
    }
    
    @Override
    public String webWechatAddInsteadApply(final Map<String, Object> anMap, final Long anId, final String anFileList) {
        return AjaxObject.newOk("申请代录成功", insteadService.wechatAddInsteadApply(anMap, anId, anFileList)).toJson();
    }
    
    @Override
    public String webFindInsteadApplyByAccountTmpId(final Long anId) {
        return AjaxObject.newOk("查询代录申请成功", insteadService.findInsteadApplyByAccountTmpId(anId)).toJson();
    }
}
