package com.betterjr.modules.notification.dubbo;

import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.notification.INotificationService;
import com.betterjr.modules.notification.service.NotificationService;

@Service(interfaceClass = INotificationService.class)
public class NotificationDubboService implements INotificationService {
    @Resource
    private NotificationService notificationService;

    @Override
    public String webQueryUnreadNotification(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        return AjaxObject.newOkWithPage("未读消息-列表查询 成功", notificationService.queryUnreadNotification(anParam, anFlag, anPageNum, anPageSize)).toJson();
    }

    @Override
    public String webQueryReadNotification(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        return AjaxObject.newOkWithPage("已读消息-列表查询 成功", notificationService.queryReadNotification(anParam, anFlag, anPageNum, anPageSize)).toJson();
    }

    @Override
    public String webCountUnreadNotification() {
        return AjaxObject.newOk("未读消息-数量查询 成功", notificationService.queryCountUnreadNotification()).toJson();
    }

    @Override
    public String webFindNotification(Long anId) {
        return AjaxObject.newOk("消息详情-查询 成功", notificationService.findNotification(anId)).toJson();
    }

    @Override
    public String webSetReadNotificationStatus(Long anId) {
        return AjaxObject.newOk("消息设置已读状态 成功", notificationService.saveSetReadNotification(anId)).toJson();
    }

}
