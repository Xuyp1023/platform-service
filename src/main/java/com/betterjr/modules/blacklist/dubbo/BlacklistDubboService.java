package com.betterjr.modules.blacklist.dubbo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.blacklist.IBlacklistService;
import com.betterjr.modules.blacklist.service.BlacklistService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

@Service(interfaceClass = IBlacklistService.class)
public class BlacklistDubboService implements IBlacklistService {

    @Autowired
    private BlacklistService scfBlacklistService;
    
    @Override
    public String webQueryBlacklist(Map<String, Object> anMap, String anFlag, int anPageNum, int anPageSize) {
        
        Map<String, Object> anQueryConditionMap = (Map<String, Object>) RuleServiceDubboFilterInvoker.getInputObj();
        
        return AjaxObject.newOkWithPage("黑名单信息查询成功", scfBlacklistService.queryBlacklist(anQueryConditionMap, anFlag, anPageNum, anPageSize)).toJson();
    }

}
