package com.betterjr.modules.notification.dubbo;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.notification.INotificationSubscribeService;
import com.betterjr.modules.notification.service.NotificationSubscribeService;

/**
 *
 * @author liuwl
 *
 */
@Service(interfaceClass = INotificationSubscribeService.class)
public class NotificationSubscribeDubboService implements INotificationSubscribeService{
    @Resource
    private NotificationSubscribeService subscribeService;

    @Override
    public String webQuerySubscribeByCustNo(final Long anCustNo, final int anFlag, final int anPageNum, final int anPageSize) {
        return AjaxObject.newOkWithPage("查询订阅列表成功!", subscribeService.queryProfileSubscribe(anCustNo, anFlag, anPageNum, anPageSize)).toJson();
    }

    @Override
    public String webCancelSubscribe(final Long anCustNo, final Long anSourceCustNo, final String anProfileName, final String anChannel) {
        subscribeService.saveCancelSubscribe(anCustNo, anSourceCustNo, anProfileName, anChannel);
        return AjaxObject.newOk("取消订阅成功!").toJson();
    }

    @Override
    public String webConfirmSubscribe(final Long anCustNo, final Long anSourceCustNo, final String anProfileName, final String anChannel) {
        subscribeService.saveConfirmSubscribe(anCustNo, anSourceCustNo, anProfileName, anChannel);
        return AjaxObject.newOk("订阅成功!").toJson();
    }


}
