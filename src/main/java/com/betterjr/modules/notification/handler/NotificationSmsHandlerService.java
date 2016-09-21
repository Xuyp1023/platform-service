// Copyright (c) 2014-2016 Betty. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.0 : 2016年9月14日, liuwl, creation
// ============================================================================
package com.betterjr.modules.notification.handler;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.mq.annotation.RocketMQListener;
import com.betterjr.common.mq.message.MQMessage;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.modules.notification.constants.NotificationConstants;
import com.betterjr.modules.notification.entity.Notification;
import com.betterjr.modules.notification.entity.NotificationCustomer;
import com.betterjr.modules.notification.provider.SmsUtils;
import com.betterjr.modules.notification.service.NotificationCustomerService;
import com.betterjr.modules.notification.service.NotificationService;

/**
 * @author liuwl
 *
 */
@Service
public class NotificationSmsHandlerService {
    private final static Logger logger = LoggerFactory.getLogger(NotificationSmsHandlerService.class);

    @Resource
    private NotificationService notificationService;

    @Resource
    private NotificationCustomerService notificationCustomerService;

    /**
     * 处理发送短信队列
     */
    @RocketMQListener(topic = "NOTIFICATION_SMS_TOPIC", consumer = "betterConsumer")
    public void processNotification(final Object anMessage) {
        final MQMessage message = (MQMessage) anMessage;
        final Notification notification = (Notification) message.getObject();

        if (BetterStringUtils.equals(notification.getImmediate(), NotificationConstants.IMMEDIATE_TRUE) == true) {
            final List<NotificationCustomer> notificationCustomers = notificationCustomerService.queryNotifiCustomerByNotifiId(notification.getId());

            final String mobileList = notificationCustomers.stream().map(notificationCustomer -> notificationCustomer.getSendNo()).filter(mobile->BetterStringUtils.isMobileNo(mobile)).collect(Collectors.joining(","));

            final String result = SmsUtils.send(notification.getContent(), mobileList);

            logger.info("短信发送结果：" + result);
        }
    }
}
