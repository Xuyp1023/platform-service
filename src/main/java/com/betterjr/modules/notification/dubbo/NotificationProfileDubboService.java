package com.betterjr.modules.notification.dubbo;

import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.notification.NotificationConstants;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.notification.INotificationProfileService;
import com.betterjr.modules.notification.entity.NotificationChannelProfile;
import com.betterjr.modules.notification.service.NotificationChannelProfileService;
import com.betterjr.modules.notification.service.NotificationProfileService;
import com.betterjr.modules.notification.service.NotificationProfileVariableService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

@Service(interfaceClass = INotificationProfileService.class)
public class NotificationProfileDubboService implements INotificationProfileService {
    @Resource
    private NotificationProfileService profileService;

    @Resource
    private NotificationChannelProfileService channelProfileService;

    @Resource
    private NotificationProfileVariableService profileVariableService;

    @Override
    public String webQueryNotificationProfile(Long anCustNo, int anFlag, int anPageNum, int anPageSize) {
        return AjaxObject.newOkWithPage("消息模板-列表查询 成功", profileService.queryNotificationProfile(anCustNo, anFlag, anPageNum, anPageSize)).toJson();
    }

    @Override
    public String webSetEnabledNotificationProfile(Long anProfileId) {
        return AjaxObject
                .newOk("消息模板-设置启用 成功", profileService.saveSetNotificationProfileStatus(anProfileId, NotificationConstants.PROFILE_STATUS_ENABLED))
                .toJson();
    }

    @Override
    public String webSetDisabledNotificationProfile(Long anProfileId) {
        return AjaxObject
                .newOk("消息模板-设置禁用 成功", profileService.saveSetNotificationProfileStatus(anProfileId, NotificationConstants.PROFILE_STATUS_DISABLED))
                .toJson();
    }

    @Override
    public String webQueryNotificationChannelProfile(Long anProfileId) {
        return AjaxObject.newOk("消息通道模板-列表查询 成功", channelProfileService.queryChannelProfileByParentProfileId(anProfileId)).toJson();
    }

    @Override
    public String webSaveNotificationChannelProfile(Map<String, Object> anParam, Long anChannelProfileId) {
        NotificationChannelProfile channelProfile = RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("消息通道模板-保存 成功", channelProfileService.saveChannelProfile(channelProfile, anChannelProfileId)).toJson();
    }

    @Override
    public String webQueryNotificationProfileVariable(Long anChannelProfileId) {
        return AjaxObject.newOk("模板预定规则-列表查询 成功", profileVariableService.queryVariableByProfileId(anChannelProfileId)).toJson();
    }

}
