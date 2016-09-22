package com.betterjr.modules.notification.job;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.notification.constants.NotificationConstants;
import com.betterjr.modules.notification.entity.Notification;
import com.betterjr.modules.notification.entity.NotificationCustomer;
import com.betterjr.modules.notification.provider.SmsUtils;
import com.betterjr.modules.notification.service.NotificationCustomerService;
import com.betterjr.modules.notification.service.NotificationService;
import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;

/**
 * 发送短信
 *
 * @author liuwl
 *
 */
@Service
public class NotificationSmsJob extends AbstractSimpleElasticJob {
    private final static Logger logger = LoggerFactory.getLogger(NotificationSmsJob.class);

    @Resource
    private NotificationService notificationService;

    @Resource
    private NotificationCustomerService notificationCustomerService;

    @Value("${sms.retry}")
    private Integer smsRetry;

    @Override
    public void process(final JobExecutionMultipleShardingContext anParamJobExecutionMultipleShardingContext) {
        logger.debug("定时发送短信 : " + new Date());

        // TODO 处理批量发送
        while(true) {
            final List<Notification> notifications = notificationService.queryUnsendSmsNotification();
            if (Collections3.isEmpty(notifications) == true) {
                break;
            }

            notifications.forEach(notification->{
                final List<NotificationCustomer> customers = notificationCustomerService.queryNotifiCustomerByNotifiId(notification.getId());

                final String sendNos = customers.stream().map(customer->customer.getSendNo()).filter(BetterStringUtils::isMobileNo).collect(Collectors.joining(",")).toString();

                if (BetterStringUtils.isNotBlank(sendNos)) {
                    logger.info("当前发送号码：" + sendNos);
                    final String result = SmsUtils.send(notification.getContent(), sendNos);
                    logger.info("发送结果：" + result);

                    notificationService.saveNotificationStatus(notification.getId(), NotificationConstants.SEND_STATUS_SUCCESS);
                }
            });
        }

        while(true) {
            final List<NotificationCustomer> customers = notificationCustomerService.queryUnsendSmsNotificationCustomer(smsRetry);
            if (Collections3.isEmpty(customers) == true) {
                break;
            }

            for (final NotificationCustomer notificationCustomer: customers) {
                final Long notificationId = notificationCustomer.getNotificationId();
                final Notification notification = notificationService.findNotificationById(notificationId);
                if (notification != null && BetterStringUtils.isMobileNo(notificationCustomer.getSendNo())) {
                    final String result = SmsUtils.send(notification.getContent(), notificationCustomer.getSendNo());
                    logger.info("发送结果：" + result);

                    notificationCustomerService.saveNotificationCustomerStatus(notificationCustomer.getId(),
                            NotificationConstants.SEND_STATUS_SUCCESS);
                }
                else {
                    notificationCustomerService.saveNotificationCustomerAddRetry(notificationCustomer.getId());
                }
            }
        }
        logger.info("定时发送短信完成 : " + new Date());
    }


}
