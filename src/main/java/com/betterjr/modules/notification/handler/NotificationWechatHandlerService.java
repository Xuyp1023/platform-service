package com.betterjr.modules.notification.handler;

import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.mq.annotation.RocketMQListener;
import com.betterjr.common.mq.message.MQMessage;
import com.betterjr.modules.notification.constants.NotificationConstants;
import com.betterjr.modules.notification.entity.Notification;
import com.betterjr.modules.notification.entity.NotificationCustomer;
import com.betterjr.modules.notification.service.NotificationCustomerService;
import com.betterjr.modules.notification.service.NotificationService;
import com.betterjr.modules.wechat.service.CustWeChatService;

@Service
public class NotificationWechatHandlerService {
    private final static Logger logger = LoggerFactory.getLogger(NotificationWechatHandlerService.class);

    @Resource
    private NotificationService notificationService;

    @Resource
    private NotificationCustomerService notificationCustomerService;

    @Inject
    private CustWeChatService wechatService;

    @RocketMQListener(topic = "NOTIFICATION_WECHAT_TOPIC", consumer = "betterConsumer")
    public void processNotification(final Object anMessage) {
        final MQMessage message = (MQMessage) anMessage;
        final Notification notification = (Notification) message.getObject();
        logger.info("NOTIFICATION_WECHAT_TOPIC: subject=" + notification.getSubject());

        final List<NotificationCustomer> notificationCustomers = notificationCustomerService.queryNotifiCustomerByNotifiId(notification.getId());

        for(final NotificationCustomer notificationCustomer: notificationCustomers) {
            sendWechat(notification, notificationCustomer);
        }

        notificationService.saveNotificationStatus(notification.getId(), NotificationConstants.SEND_STATUS_SUCCESS);
    }

    /**
     * @param anNotification
     * @param anNotificationCustomer
     */
    private void sendWechat(final Notification anNotification, final NotificationCustomer anNotificationCustomer) {
        wechatService.sendWechatMessage(anNotificationCustomer.getOperId(), anNotification.getContent());
        notificationCustomerService.saveNotificationCustomerStatus(anNotificationCustomer.getId(), NotificationConstants.SEND_STATUS_SUCCESS);
    }
}
