package com.betterjr.modules.notification.job;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.betterjr.common.data.NotificationAttachment;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.MailUtils;
import com.betterjr.modules.notification.constants.NotificationConstants;
import com.betterjr.modules.notification.entity.Notification;
import com.betterjr.modules.notification.entity.NotificationCustomer;
import com.betterjr.modules.notification.service.NotificationCustomerService;
import com.betterjr.modules.notification.service.NotificationService;
import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;

/**
 * 重新发送状态为发送失败的邮件
 * @author liuwl
 *
 */
@Service
public class NotificationEmailJob extends AbstractSimpleElasticJob {
    private final static Logger logger = LoggerFactory.getLogger(NotificationSmsJob.class);

    @Resource
    private NotificationService notificationService;

    @Resource
    private NotificationCustomerService notificationCustomerService;

    @Value("${mail.retry}")
    private Integer mailRetry;

    @Override
    public void process(final JobExecutionMultipleShardingContext anShardingContext) {
        logger.info("邮件定时处理重发 : " + new Date());

        while (true) {
            final List<NotificationCustomer> customers = notificationCustomerService
                    .queryUnsendEmailNotificationCustomer(mailRetry);
            if (Collections3.isEmpty(customers) == true) {
                break;
            }

            for (final NotificationCustomer customer : customers) {
                // 单条发送逻辑
                if (sendMail(customer) == true) {
                    notificationCustomerService.saveNotificationCustomerStatus(customer.getId(),
                            NotificationConstants.SEND_STATUS_SUCCESS);
                } else {
                    notificationCustomerService.saveNotificationCustomerAddRetry(customer.getId());
                }
            }
        }
    }

    /**
     * 单条发送
     */
    private boolean sendMail(final NotificationCustomer anCustomer) {
        final Notification notification = notificationService.findNotification(anCustomer.getNotificationId());

        final Long batchNo = notification.getBatchNo();

        final List<NotificationAttachment> attachments = notificationService.buildAttachments(batchNo);

        return MailUtils.sendMail(anCustomer.getSendNo(), notification.getSubject(), notification.getContent(),
                attachments);
    }

}
