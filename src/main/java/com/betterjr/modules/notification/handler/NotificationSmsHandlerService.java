// Copyright (c) 2014-2016 Betty. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.0 : 2016年9月14日, liuwl, creation
// ============================================================================
package com.betterjr.modules.notification.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.mq.annotation.RocketMQListener;
import com.betterjr.common.mq.message.MQMessage;
import com.betterjr.modules.notification.entity.Notification;

/**
 * @author liuwl
 *
 */
@Service
public class NotificationSmsHandlerService {
    private final static Logger logger = LoggerFactory.getLogger(NotificationSmsHandlerService.class);

    /**
     * 处理发送短信队列
     */
    @RocketMQListener(topic = "NOTIFICATION_SMS_TOPIC", consumer = "betterConsumer")
    public void processNotification(final Object anMessage) {
        final MQMessage message = (MQMessage) anMessage;
        final Notification notification = (Notification) message.getObject();

    }
}
