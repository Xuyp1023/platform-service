package com.betterjr.modules.customer.handler;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.betterjr.common.mq.annotation.RocketMQListener;
import com.betterjr.common.mq.message.MQMessage;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.DictUtils;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.customer.entity.CustOpenAccountTmp;
import com.betterjr.modules.notification.INotificationService;
import com.betterjr.modules.notification.NotificationModel;
import com.betterjr.modules.notification.NotificationModel.Builder;

@Service
public class OpenAccountHandlerService {
    private final static Logger logger = LoggerFactory.getLogger(OpenAccountHandlerService.class);

    @Resource
    public CustAccountService accountService;

    @Reference(interfaceClass = INotificationService.class)
    public INotificationService notificationService;

    /**
     * 开户消息
     */
    @RocketMQListener(topic = "CUSTOMER_OPENACCOUNT_TOPIC", consumer = "betterConsumer")
    public void processNotification(final Object anMessage) {
        try {
            logger.debug("进入开户Handler");
            final MQMessage message = (MQMessage) anMessage;
            String type = (String) message.getHead("type"); // 取类型 1开户审核通过 0开户审核驳回
            CustOperatorInfo operator = (CustOperatorInfo) message.getHead("operator");// 审核操作员
            CustOpenAccountTmp openAccountTmp = (CustOpenAccountTmp) message.getObject();// 开户实体

            logger.debug("当前开户用户：" + openAccountTmp.getCustName());
            Long platformCustNo = Long.valueOf(Collections3.getFirst(DictUtils.getDictList("PlatformGroup")).getItemValue());
            CustInfo customer = accountService.findCustInfo(platformCustNo);

            if (BetterStringUtils.equals("1", type)) { // 开户审核通过通知
                Builder builder = NotificationModel.newBuilder("开户审核通过通知", customer, operator);
                builder.setEntity(openAccountTmp);
                builder.addReceiveEmail(openAccountTmp.getOperEmail());
                builder.addReceiveMobile(openAccountTmp.getOperMobile());
                notificationService.sendNotification(builder.build());
            }
            else if (BetterStringUtils.equals("0", type)) { // 开户审核驳回通知
                Builder builder = NotificationModel.newBuilder("开户审核驳回通知", customer, operator);
                builder.setEntity(openAccountTmp);
                builder.addReceiveEmail(openAccountTmp.getOperEmail());
                builder.addReceiveMobile(openAccountTmp.getOperMobile());
                builder.addParam("auditOpinion", message.getHead("auditOpinion"));
                notificationService.sendNotification(builder.build());
            }
            else {
                logger.error("消息类型不正确！");
            }
        }
        catch (Exception e) {
            logger.error("开户消息消费失败！", e);
        }
    }
}
