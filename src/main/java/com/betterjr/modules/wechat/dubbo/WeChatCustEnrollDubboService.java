package com.betterjr.modules.wechat.dubbo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;
import com.betterjr.modules.wechat.IWeChatCustEnrollService;
import com.betterjr.modules.wechat.entity.CustTempEnrollInfo;
import com.betterjr.modules.wechat.service.WeChatCustEnrollService;

@Service(interfaceClass = IWeChatCustEnrollService.class)
public class WeChatCustEnrollDubboService implements IWeChatCustEnrollService {
    
    @Autowired
    private WeChatCustEnrollService weChatCustEnrollService;

    @Override
    public String webFindCustEnroll(String anOpenId) {
        return AjaxObject.newOk("查询用户开户信息成功", weChatCustEnrollService.findCustEnroll(anOpenId)).toJson();
    }

    @Override
    public String webAddCustEnroll(Map<String, Object> anMap, String anOpenId, String anFileList) {
        
        CustTempEnrollInfo anCustTempEnrollInfo = (CustTempEnrollInfo) RuleServiceDubboFilterInvoker.getInputObj();
        
        return AjaxObject.newOk("开户成功", weChatCustEnrollService.addCustEnroll(anCustTempEnrollInfo, anOpenId, anFileList)).toJson();
    }

}
