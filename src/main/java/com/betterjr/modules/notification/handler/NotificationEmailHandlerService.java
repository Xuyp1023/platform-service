package com.betterjr.modules.notification.handler;

import java.util.List;

import javax.annotation.Resource;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.mq.annotation.RocketMQListener;
import com.betterjr.common.mq.message.MQMessage;
import com.betterjr.common.notification.NotificationConstants;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.MailUtils;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.notification.entity.Notification;
import com.betterjr.modules.notification.entity.NotificationCustomer;
import com.betterjr.modules.notification.service.NotificationCustomerService;

@Service
public class NotificationEmailHandlerService {
    private final static Logger logger = LoggerFactory.getLogger(NotificationEmailHandlerService.class);

    @Resource
    private NotificationCustomerService notificationCustomerService;

    /**
     * 发送邮件 
     */
    @RocketMQListener(topic = "NOTIFICATION_EMAIL_TOPIC", consumer = "notificationConsumer")
    public void processNotification(final Object anMessage) {
        final MQMessage message = (MQMessage) anMessage;
        Notification notification = (Notification) message.getObject();

        Session session = MailUtils.createSession();
        // TODO 需要处理附件
        MimeMessage mimeMessage = MailUtils.createMessage(session, notification.getSubject(), notification.getContent(), null);

        List<NotificationCustomer> notifiCustomers = notificationCustomerService.queryNotifiCustomerByNotifiId(notification.getId());

        notifiCustomers.forEach(notificationCustomer -> {
            sendMail(session, mimeMessage, notificationCustomer);
        });
    }

    private void sendMail(Session anSession, MimeMessage anMimeMessage, NotificationCustomer anNotificationCustomer) {
        String email = anNotificationCustomer.getSendNo();
        if (BetterStringUtils.isNotBlank(email) == true) {
            boolean status = MailUtils.sendMail(anSession, anMimeMessage, email);
            if (status == true) {
                anNotificationCustomer.initModifyValue(NotificationConstants.SEND_STATUS_SUCCESS);
            }
            else {
                anNotificationCustomer.initModifyValue(NotificationConstants.SEND_STATUS_FAIL);
            }
            notificationCustomerService.updateByPrimaryKeySelective(anNotificationCustomer);
        }
    }
}
