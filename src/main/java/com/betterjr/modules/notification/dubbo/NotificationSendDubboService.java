// Copyright (c) 2014-2016 Betty. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.0 : 2016年9月13日, liuwl, creation
// ============================================================================
package com.betterjr.modules.notification.dubbo;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.betterjr.common.mq.codec.MQCodecType;
import com.betterjr.common.mq.core.RocketMQProducer;
import com.betterjr.common.mq.message.MQMessage;
import com.betterjr.modules.notification.INotificationSendService;
import com.betterjr.modules.notification.NotificationModel;
import com.betterjr.modules.notification.constants.NotificationConstants;

/**
 * @author liuwl
 *
 */
@Service(interfaceClass = INotificationSendService.class)
public class NotificationSendDubboService implements INotificationSendService {
    protected final Logger logger = LoggerFactory.getLogger(NotificationSendDubboService.class);

    @Resource(name = "betterProducer")
    private RocketMQProducer betterProducer;

    @Override
    /**
     * 发送消息通知 NotificationModel
     */
    public boolean sendNotification(final NotificationModel anNotificationModel) {
        final MQMessage message = new MQMessage(NotificationConstants.NOTIFICATION_TOPIC, MQCodecType.FST);
        message.setObject(anNotificationModel);

        try {
            final SendResult sendResult = betterProducer.sendMessage(message);

            if (sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
                return true;
            } else {
                logger.warn("消息通知发送失败 SendResult=" + sendResult.toString());
                return false;
            }
        }
        catch (final Exception e) {
            logger.error("消息通知发送错误", e);
            return false;
        }
    }
}
