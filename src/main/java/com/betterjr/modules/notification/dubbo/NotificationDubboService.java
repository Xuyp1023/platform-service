package com.betterjr.modules.notification.dubbo;

import static com.betterjr.common.web.AjaxObject.newOk;
import static com.betterjr.common.web.AjaxObject.newOkWithPage;

import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.modules.notification.INotificationService;
import com.betterjr.modules.notification.service.NotificationService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

@Service(interfaceClass = INotificationService.class)
public class NotificationDubboService implements INotificationService {
    @Resource
    private NotificationService notificationService;

    @Override
    public String webQueryUnreadNotification(final Map<String, Object> anParam, final int anFlag, final int anPageNum,
            final int anPageSize) {
        final Map<String, Object> param = RuleServiceDubboFilterInvoker.getInputObj();
        return newOkWithPage("未读消息-列表查询 成功",
                notificationService.queryUnreadNotification(param, anFlag, anPageNum, anPageSize)).toJson();
    }

    @Override
    public String webQueryReadNotification(final Map<String, Object> anParam, final int anFlag, final int anPageNum,
            final int anPageSize) {
        final Map<String, Object> param = RuleServiceDubboFilterInvoker.getInputObj();
        return newOkWithPage("已读消息-列表查询 成功",
                notificationService.queryReadNotification(param, anFlag, anPageNum, anPageSize)).toJson();
    }

    @Override
    public String webCountUnreadNotification() {
        return newOk("未读消息-数量查询 成功", notificationService.queryCountUnreadNotification()).toJson();
    }

    @Override
    public String webFindNotification(final Long anId) {
        return newOk("消息详情-查询 成功", notificationService.findNotification(anId)).toJson();
    }

    @Override
    public String webSetReadNotificationStatus(final Long anId) {
        return newOk("消息设置已读状态 成功", notificationService.saveSetReadNotification(anId)).toJson();
    }

}
