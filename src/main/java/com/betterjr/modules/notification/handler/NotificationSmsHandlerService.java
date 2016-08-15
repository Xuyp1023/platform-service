package com.betterjr.modules.notification.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.mq.annotation.RocketMQListener;
import com.betterjr.common.mq.message.MQMessage;
import com.betterjr.modules.notification.entity.Notification;

@Service
public class NotificationSmsHandlerService {
    private final static Logger logger = LoggerFactory.getLogger(NotificationSmsHandlerService.class);
    
    @RocketMQListener(topic = "NOTIFICATION_SMS_TOPIC", consumer = "notificationConsumer")
    public void processNotification(final Object anMessage) {
        final MQMessage message = (MQMessage) anMessage;
        Notification notification = (Notification) message.getObject();
        
        // 发送短信

        logger.info("发送短信@");   
    }
}
