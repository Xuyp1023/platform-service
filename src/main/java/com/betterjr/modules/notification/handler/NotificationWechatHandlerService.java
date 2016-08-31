package com.betterjr.modules.notification.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.mq.annotation.RocketMQListener;

@Service
public class NotificationWechatHandlerService {
    private final static Logger logger = LoggerFactory.getLogger(NotificationWechatHandlerService.class);
    
    //@RocketMQListener(topic = "NOTIFICATION_WECHAT_TOPIC", consumer = "betterConsumer")
    public void processNotification(final Object anMessage) {
        logger.info("微信发送!");
    }
}
