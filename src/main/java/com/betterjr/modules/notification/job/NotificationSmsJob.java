package com.betterjr.modules.notification.job;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.notification.constants.NotificationConstants;
import com.betterjr.modules.notification.entity.Notification;
import com.betterjr.modules.notification.entity.NotificationCustomer;
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
    public void process(JobExecutionMultipleShardingContext anParamJobExecutionMultipleShardingContext) {
        logger.debug("定时发送短信 : " + new Date());
        
        // TODO 处理批量发送
        while(true) {
            List<Notification> notifications = notificationService.queryUnsendSmsNotification();
            if (Collections3.isEmpty(notifications) == true) {
                break;
            }
            
            notifications.forEach(notification->{
                List<NotificationCustomer> customers = notificationCustomerService.queryNotifiCustomerByNotifiId(notification.getId());
                
                String sendNos = customers.stream().map(customer->customer.getSendNo()).filter(sendNo->{return true;}).collect(Collectors.joining(",")).toString();
                
                logger.info("当前发送号码：" + sendNos);
                
                // TODO 处理批量发送
                
                notificationService.saveNotificationStatus(notification.getId(), NotificationConstants.SEND_STATUS_SUCCESS);
            });
        }
        
        // TODO 单条发送逻辑
        while(true) {
            List<NotificationCustomer> customers = notificationCustomerService.queryUnsendSmsNotificationCustomer(smsRetry);
            if (Collections3.isEmpty(customers) == true) {
                break;
            }
            
            if (1 == 1) { //发送成功的
                notificationCustomerService.saveNotificationCustomerStatus(0L, NotificationConstants.SEND_STATUS_SUCCESS);
            } else {
                notificationCustomerService.saveNotificationCustomerAddRetry(0L);
            }
        }
        logger.info("定时发送短信完成 : " + new Date());
    }

    
}
