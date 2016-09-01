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
    public String webQuerySubscribeByCustNo(Long anCustNo, int anFlag, int anPageNum, int anPageSize) {
        return AjaxObject.newOkWithPage("查询订阅列表成功!", subscribeService.queryProfileSubscribe(anCustNo, anFlag, anPageNum, anPageSize)).toJson();
    }

    @Override
    public String webCancelSubscribe(Long anCustNo, Long anChannelProfileId) {
        subscribeService.saveCancelSubscribe(anCustNo, anChannelProfileId);
        return AjaxObject.newOk("取消订阅成功!").toJson();
    }

    @Override
    public String webConfirmSubscribe(Long anCustNo, Long anChannelProfileId) {
        subscribeService.saveConfirmSubscribe(anCustNo, anChannelProfileId);
        return AjaxObject.newOk("订阅成功!").toJson();
    }


}
