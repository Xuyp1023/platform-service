package com.betterjr.modules.notification.job;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.betterjr.modules.notification.service.NotificationCustomerService;
import com.betterjr.modules.notification.service.NotificationService;
import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;

public class NotificationSmsJob extends AbstractSimpleElasticJob {
    private final static Logger logger = LoggerFactory.getLogger(NotificationSmsJob.class);
    
    @Resource
    private NotificationService notificationService;
    
    @Resource
    private NotificationCustomerService notificationCustomerService;
    
    @Value("${sms.begin.time}")
    private String beginTime;
    
    @Value("${sms.end.time}")
    private String endTime;
    
    @Override
    public void process(JobExecutionMultipleShardingContext anParamJobExecutionMultipleShardingContext) {
        logger.info("定时任务执行!");
    }

}
