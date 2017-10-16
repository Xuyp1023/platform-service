package com.betterjr.modules.notification.dubbo;

import static com.betterjr.common.web.AjaxObject.newOk;
import static com.betterjr.common.web.AjaxObject.newOkWithPage;

import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.modules.notification.INotificationProfileService;
import com.betterjr.modules.notification.constants.NotificationConstants;
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
    public String webQueryNotificationProfile(final Long anCustNo, final int anFlag, final int anPageNum,
            final int anPageSize) {
        return newOkWithPage("消息模板-列表查询 成功",
                profileService.queryNotificationProfile(anCustNo, anFlag, anPageNum, anPageSize)).toJson();
    }

    @Override
    public String webSetEnabledNotificationProfile(final Long anProfileId, final Long anCustNo) {
        return newOk("消息模板-设置启用 成功", profileService.saveSetNotificationProfileStatus(anProfileId, anCustNo,
                NotificationConstants.PROFILE_STATUS_ENABLED)).toJson();
    }

    @Override
    public String webSetDisabledNotificationProfile(final Long anProfileId, final Long anCustNo) {
        return newOk("消息模板-设置禁用 成功", profileService.saveSetNotificationProfileStatus(anProfileId, anCustNo,
                NotificationConstants.PROFILE_STATUS_DISABLED)).toJson();
    }

    @Override
    public String webQueryNotificationChannelProfile(final Long anProfileId, final Long anCustNo) {
        return newOk("消息通道模板-列表查询 成功", channelProfileService.queryChannelProfileByProfileId(anProfileId, anCustNo))
                .toJson();
    }

    @Override
    public String webSaveNotificationChannelProfile(final Map<String, Object> anParam, final Long anChannelProfileId,
            final Long anCustNo) {
        final NotificationChannelProfile channelProfile = RuleServiceDubboFilterInvoker.getInputObj();
        final String contentText = (String) anParam.get("contentText");
        return newOk("消息通道模板-保存 成功",
                channelProfileService.saveChannelProfile(channelProfile, anChannelProfileId, anCustNo, contentText))
                        .toJson();
    }

    @Override
    public String webQueryNotificationProfileVariable(final Long anChannelProfileId, final Long anCustNo) {
        return newOk("模板预定规则-列表查询 成功", profileVariableService.queryVariableByProfileId(anChannelProfileId)).toJson();
    }

}
