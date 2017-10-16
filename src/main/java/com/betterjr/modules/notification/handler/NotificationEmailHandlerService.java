package com.betterjr.modules.notification.handler;

import java.util.List;

import javax.annotation.Resource;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.data.NotificationAttachment;
import com.betterjr.common.mq.annotation.RocketMQListener;
import com.betterjr.common.mq.message.MQMessage;
import com.betterjr.common.utils.MailUtils;
import com.betterjr.modules.notification.constants.NotificationConstants;
import com.betterjr.modules.notification.entity.Notification;
import com.betterjr.modules.notification.entity.NotificationCustomer;
import com.betterjr.modules.notification.service.NotificationCustomerService;
import com.betterjr.modules.notification.service.NotificationService;

@Service
public class NotificationEmailHandlerService {
    private final static Logger logger = LoggerFactory.getLogger(NotificationEmailHandlerService.class);

    @Resource
    private NotificationService notificationService;

    @Resource
    private NotificationCustomerService notificationCustomerService;

    /**
     * 处理发送邮件队列
     */
    @RocketMQListener(topic = "NOTIFICATION_EMAIL_TOPIC", consumer = "betterConsumer")
    public void processNotification(final Object anMessage) {
        final MQMessage message = (MQMessage) anMessage;
        final Notification notification = (Notification) message.getObject();

        logger.info("NOTIFICATION_EMAIL_TOPIC: subject=" + notification.getSubject());
        final Session session = MailUtils.createSession();
        final Long batchNo = notification.getBatchNo();

        final List<NotificationAttachment> attachments = notificationService.buildAttachments(batchNo);

        final MimeMessage mimeMessage = MailUtils.createMessage(session, notification.getSubject(),
                notification.getContent(), attachments);

        final List<NotificationCustomer> notificationCustomers = notificationCustomerService
                .queryNotifiCustomerByNotifiId(notification.getId());

        notificationCustomers.forEach(notificationCustomer -> {
            sendMail(session, mimeMessage, notificationCustomer);
        });

        notificationService.saveNotificationStatus(notification.getId(), NotificationConstants.SEND_STATUS_SUCCESS);
    }

    /**
     * 发送邮件
     */
    private void sendMail(final Session anSession, final MimeMessage anMimeMessage,
            final NotificationCustomer anNotificationCustomer) {
        final String email = anNotificationCustomer.getSendNo();
        if (StringUtils.isNotBlank(email) == true) {
            if (MailUtils.sendMail(anSession, anMimeMessage, email) == true) {
                notificationCustomerService.saveNotificationCustomerStatus(anNotificationCustomer.getId(),
                        NotificationConstants.SEND_STATUS_SUCCESS);
            } else {
                notificationCustomerService.saveNotificationCustomerStatus(anNotificationCustomer.getId(),
                        NotificationConstants.SEND_STATUS_FAIL);
            }
        }
    }

}
