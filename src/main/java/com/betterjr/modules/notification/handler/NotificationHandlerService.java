package com.betterjr.modules.notification.handler;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.betterjr.common.entity.BetterjrEntity;
import com.betterjr.common.mq.annotation.RocketMQListener;
import com.betterjr.common.mq.codec.MQCodecType;
import com.betterjr.common.mq.core.RocketMQProducer;
import com.betterjr.common.mq.message.MQMessage;
import com.betterjr.common.notification.NotificationConstants;
import com.betterjr.common.notification.NotificationModel;
import com.betterjr.common.service.FreemarkerService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterDateUtils;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.account.service.CustOperatorService;
import com.betterjr.modules.notification.entity.Notification;
import com.betterjr.modules.notification.entity.NotificationChannelProfile;
import com.betterjr.modules.notification.entity.NotificationCustomer;
import com.betterjr.modules.notification.entity.NotificationProfile;
import com.betterjr.modules.notification.entity.NotificationProfileVariable;
import com.betterjr.modules.notification.service.NotificationChannelProfileService;
import com.betterjr.modules.notification.service.NotificationCustomerService;
import com.betterjr.modules.notification.service.NotificationProfileService;
import com.betterjr.modules.notification.service.NotificationProfileVariableService;
import com.betterjr.modules.notification.service.NotificationService;

/**
 * 消息处理
 * 
 * @author liuwl
 *
 */
@Service
public class NotificationHandlerService {
    private final static Logger logger = LoggerFactory.getLogger(NotificationHandlerService.class);

    @Resource
    private NotificationProfileService profileService;

    @Resource
    private NotificationChannelProfileService channelProfileService;

    @Resource
    private NotificationProfileVariableService profileVariableService;

    @Resource
    private FreemarkerService freemarkerService;

    @Resource
    private NotificationService notificationService;

    @Resource
    private NotificationCustomerService notificationCustomerService;

    @Resource
    private CustAccountService accountService;

    @Resource
    private CustOperatorService operatorService;

    @Resource(name = "notificationProducer")
    private RocketMQProducer producer;

    @RocketMQListener(topic = "NOTIFICATION_TOPIC", consumer = "notificationConsumer")
    public void processNotification(final Object anMessage) {
        final MQMessage message = (MQMessage) anMessage;
        NotificationModel notificationModel = (NotificationModel) message.getObject();

        String profileName = notificationModel.getProfileName();

        Long custNo = notificationModel.getSendCustomer();

        NotificationProfile profile = profileService.findProfileByProfileNameAndCustNo(profileName, custNo);

        BTAssert.notNull(profile);

        if (BetterStringUtils.equals(NotificationConstants.PROFILE_STATUS_ENABLED, profile.getBusinStatus()) == true) {
            List<NotificationChannelProfile> channelProfiles = channelProfileService.queryChannelProfileByParentProfileId(profile.getId());

            processNotification(profile, channelProfiles, notificationModel);
        }
        else {
            logger.info(profileName + " 消息模板已经禁用!");
        }
    }

    /**
     * 处理消息
     */
    private void processNotification(NotificationProfile anProfile, List<NotificationChannelProfile> anChannelProfiles,
            NotificationModel anNotificationModel) {
        Long custNo = anNotificationModel.getSendCustomer();
        Long operId = anNotificationModel.getSendOperator();

        CustInfo customer = accountService.findCustInfo(custNo);
        CustOperatorInfo operator = operatorService.findCustOperatorInfo(operId);

        Map<String, Object> param = anNotificationModel.getParam();
        BetterjrEntity entity = anNotificationModel.getEntity();

        param.put("entity", entity);
        param.put("sendCustNo", custNo);
        param.put("sendCustName", customer.getCustName());
        param.put("sendCustomer", customer);
        param.put("sendOperator", operator);

        for (NotificationChannelProfile channelProfile : anChannelProfiles) {
            if (BetterStringUtils.equals(NotificationConstants.PROFILE_STATUS_ENABLED, channelProfile.getBusinStatus()) == true) {

                Notification notification = addNotification(anProfile, channelProfile, param, operator, customer);
                BTAssert.notNull(notification);

                Collection<Pair<CustOperatorInfo, CustInfo>> operators = queryOperatorInfo(anNotificationModel);

                for (Pair<CustOperatorInfo, CustInfo> tempOperator : operators) {
                    addNotificationCustomer(notification, tempOperator.getLeft(), tempOperator.getRight(), operator, customer);
                }
                switch (notification.getChannel()) {
                case NotificationConstants.CHANNEL_INBOX:
                    processInbox(notification, operator, customer);
                    break;
                case NotificationConstants.CHANNEL_EMAIL:
                    processEmail(notification, operator, customer);
                    break;
                case NotificationConstants.CHANNEL_SMS:
                    processSms(notification, operator, customer);
                    break;
                case NotificationConstants.CHANNEL_WECHAT:
                    processWechat(notification, operator, customer);
                    break;
                default:
                }
            }
            else {
                String channel = "";
                switch (channelProfile.getChannel()) {
                case NotificationConstants.CHANNEL_INBOX:
                    channel = "站内消息";
                    break;
                case NotificationConstants.CHANNEL_EMAIL:
                    channel = "电子邮件";
                    break;
                case NotificationConstants.CHANNEL_SMS:
                    channel = "短信";
                    break;
                case NotificationConstants.CHANNEL_WECHAT:
                    break;
                default:
                }
                logger.info(anProfile.getProfileName() + " 消息模板, " + channel + " 通道已经禁用!");
            }
        }
    }

    /**
     * 处理email消息
     */
    private void processEmail(Notification anNotification, CustOperatorInfo anSendOperator, CustInfo anSendCustomer) {
        final MQMessage message = new MQMessage(NotificationConstants.NOTIFICATION_EMAIL_TOPIC, MQCodecType.FST);
        message.setObject(anNotification);
        message.addHead("sendOperator", anSendOperator);
        message.addHead("sendCustomer", anSendCustomer);
        try {
            final SendResult sendResult = producer.sendMessage(message);

            if (sendResult.getSendStatus().equals(SendStatus.SEND_OK) == false) {
                logger.warn("消息通知发送失败 SendResult=" + sendResult.toString());
            }
        }
        catch (Exception e) {
            logger.error("消息通知发送错误", e);
        }
    }

    /**
     * 处理sms消息
     */
    private void processSms(Notification anNotification, CustOperatorInfo anSendOperator, CustInfo anSendCustomer) {
        final MQMessage message = new MQMessage(NotificationConstants.NOTIFICATION_SMS_TOPIC, MQCodecType.FST);
        message.setObject(anNotification);
        message.addHead("sendOperator", anSendOperator);
        message.addHead("sendCustomer", anSendCustomer);
        try {
            final SendResult sendResult = producer.sendMessage(message);

            if (sendResult.getSendStatus().equals(SendStatus.SEND_OK) == false) {
                logger.warn("消息通知发送失败 SendResult=" + sendResult.toString());
            }
        }
        catch (Exception e) {
            logger.error("消息通知发送错误", e);
        }
    }

    private void processInbox(Notification anNotification, CustOperatorInfo anOperator, CustInfo anCustomer) {
        logger.info("INBOX");
    }

    private void processWechat(Notification anNotification, CustOperatorInfo anOperator, CustInfo anCustomer) {
        logger.info("WECHAT");
    }

    private NotificationCustomer addNotificationCustomer(Notification anNotification, CustOperatorInfo anReceiveOperator, CustInfo anReceiveCustomer,
            CustOperatorInfo anSendOperator, CustInfo anSendCustomer) {

        NotificationCustomer tempNotificationCustomer = new NotificationCustomer();
        tempNotificationCustomer.setOperId(anReceiveOperator.getId());
        tempNotificationCustomer.setOperName(anReceiveOperator.getName());
        tempNotificationCustomer.setChannel(anNotification.getChannel());

        if (anReceiveCustomer != null) {
            tempNotificationCustomer.setCustNo(anReceiveCustomer.getCustNo());
            tempNotificationCustomer.setCustName(anReceiveCustomer.getCustName());
        }

        tempNotificationCustomer.setNotificationId(anNotification.getId());
        switch (anNotification.getChannel()) {
        case NotificationConstants.CHANNEL_INBOX:
            tempNotificationCustomer.setSendNo(String.valueOf(anReceiveOperator.getId()));
            break;
        case NotificationConstants.CHANNEL_EMAIL:
            tempNotificationCustomer.setSendNo(anReceiveOperator.getEmail());
            break;
        case NotificationConstants.CHANNEL_SMS:
            tempNotificationCustomer.setSendNo(anReceiveOperator.getMobileNo());
            break;
        case NotificationConstants.CHANNEL_WECHAT:
            break;
        default:
        }

        NotificationCustomer notificationCustomer = notificationCustomerService.addNotificationCustomer(tempNotificationCustomer, anSendOperator,
                anSendCustomer);
        return notificationCustomer;
    }

    private Notification addNotification(NotificationProfile anProfile, NotificationChannelProfile anChannelProfile, Map<String, Object> anParam,
            CustOperatorInfo anOperator, CustInfo anCustomer) {
        final Notification tempNotification = new Notification();
        final List<NotificationProfileVariable> profileVariables = profileVariableService.queryVariableByProfileId(anChannelProfile.getId());

        tempNotification.setProfileId(anProfile.getId());
        tempNotification.setChannelProfileId(anChannelProfile.getId());
        tempNotification.setChannel(anChannelProfile.getChannel());
        tempNotification.setSubject(resolveTemplateContent(anChannelProfile.getSubject(), profileVariables, anParam));
        tempNotification.setContent(resolveTemplateContent(anChannelProfile.getContent(), profileVariables, anParam));
        tempNotification.setReference(resolveTemplateContent(anChannelProfile.getReference(), profileVariables, anParam));
        tempNotification.setSentDate(BetterDateUtils.getNumDate());
        tempNotification.setSentTime(BetterDateUtils.getNumTime());

        Notification notification = notificationService.addNotification(tempNotification, anOperator, anCustomer);
        return notification;
    }

    private Collection<Pair<CustOperatorInfo, CustInfo>> queryOperatorInfo(NotificationModel anNotificationModel) {
        List<Long> receiveOperators = anNotificationModel.getReceiveOperators();
        List<Long> receiveCustomers = anNotificationModel.getReceiveCustomers();

        Set<Pair<CustOperatorInfo, CustInfo>> operators = new HashSet<>();

        receiveOperators.forEach(operId -> {
            CustOperatorInfo operator = findOperatorById(operId);
            if (operator != null) {
                operators.add(new ImmutablePair<CustOperatorInfo, CustInfo>(operator, null));
            }
        });

        receiveCustomers.forEach(custNo -> {
            Collection<CustOperatorInfo> tempOperators = queryOperatorByCustNo(custNo);
            CustInfo tempCustomer = accountService.findCustInfo(custNo);

            tempOperators.forEach(operator -> {
                operators.add(new ImmutablePair<CustOperatorInfo, CustInfo>(operator, tempCustomer));
            });
        });

        return operators;
    }

    private String resolveTemplateContent(String anTemplateContent, List<NotificationProfileVariable> anProfileVariables,
            Map<String, Object> anParam) {
        final String templateContent = preproccessTemplateContent(anTemplateContent, anProfileVariables);

        StringBuffer sb = freemarkerService.processTemplateByContents(templateContent, anParam);
        return sb.toString();
    }

    private String preproccessTemplateContent(String anTemplateContent, List<NotificationProfileVariable> anProfileVariables) {
        String templateContent = anTemplateContent;
        for (NotificationProfileVariable profileVariable : anProfileVariables) {
            templateContent = replaceVariable(templateContent, profileVariable);
        }
        return templateContent;
    }

    private String replaceVariable(String anTemplateContent, NotificationProfileVariable anProfileVariable) {
        anTemplateContent = anTemplateContent.replace(anProfileVariable.getVariableName(), anProfileVariable.getVariableValue());
        return anTemplateContent;
    }

    private Collection<CustOperatorInfo> queryOperatorByCustNo(Long anCustNo) {
        return operatorService.queryOperatorInfoByCustNo(anCustNo);
    }

    private CustOperatorInfo findOperatorById(Long anOperId) {
        return operatorService.findCustOperatorInfo(anOperId);
    }

}
