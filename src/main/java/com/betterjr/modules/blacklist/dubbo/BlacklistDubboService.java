package com.betterjr.modules.blacklist.dubbo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.blacklist.IBlacklistService;
import com.betterjr.modules.blacklist.entity.Blacklist;
import com.betterjr.modules.blacklist.service.BlacklistService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

@Service(interfaceClass = IBlacklistService.class)
public class BlacklistDubboService implements IBlacklistService {

    @Autowired
    private BlacklistService scfBlacklistService;

    @Override
    public String webQueryBlacklist(Map<String, Object> anMap, String anFlag, int anPageNum, int anPageSize) {

        Map<String, Object> anQueryConditionMap = (Map<String, Object>) RuleServiceDubboFilterInvoker.getInputObj();

        return AjaxObject
                .newOkWithPage("黑名单信息查询成功",
                        scfBlacklistService.queryBlacklist(anQueryConditionMap, anFlag, anPageNum, anPageSize))
                .toJson();
    }

    @Override
    public String webAddBlacklist(Map<String, Object> anMap) {

        Blacklist anBlacklist = (Blacklist) RuleServiceDubboFilterInvoker.getInputObj();

        return AjaxObject.newOk("黑名单录入成功", scfBlacklistService.addBlacklist(anBlacklist)).toJson();
    }

    @Override
    public String webSaveModifyBlacklist(Map<String, Object> anMap, Long anId) {

        Blacklist anBlacklist = (Blacklist) RuleServiceDubboFilterInvoker.getInputObj();

        return AjaxObject.newOk("黑名单修改成功", scfBlacklistService.saveModifyBlacklist(anBlacklist, anId)).toJson();
    }

    @Override
    public String webSaveActivateBlacklist(Long anId) {

        return AjaxObject.newOk("黑名单激活成功", scfBlacklistService.saveActivateBlacklist(anId)).toJson();
    }

    @Override
    public String webSaveCancelBlacklist(Long anId) {

        return AjaxObject.newOk("黑名单注销成功", scfBlacklistService.saveCancelBlacklist(anId)).toJson();
    }

    @Override
    public int webSaveDeleteBlacklist(Long anId) {

        return scfBlacklistService.saveDeleteBlacklist(anId);
    }

    @Override
    public String webCheckBlacklistExists(String anName, String anIdentNo, String anLawName) {

        return AjaxObject.newOk("检查是否存在黑名单成功", scfBlacklistService.checkBlacklistExists(anName, anIdentNo, anLawName))
                .toJson();
    }

}
