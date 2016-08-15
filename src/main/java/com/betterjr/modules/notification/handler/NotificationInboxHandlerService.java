package com.betterjr.modules.notification.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.mq.annotation.RocketMQListener;

@Service
public class NotificationInboxHandlerService {
    private final static Logger logger = LoggerFactory.getLogger(NotificationInboxHandlerService.class);

    @RocketMQListener(topic = "NOTIFICATION_INBOX_TOPIC", consumer = "notificationConsumer")
    public void processNotification(final Object anMessage) {
        logger.info("发送站内消息!");
    }
}
