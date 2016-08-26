package com.betterjr.modules.notification.handler;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import com.betterjr.common.utils.Base64Coder;
import com.betterjr.common.utils.BetterDateUtils;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
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
    private NotificationService notificationService;

    @Resource
    private NotificationProfileService profileService;

    @Resource
    private NotificationCustomerService notificationCustomerService;

    @Resource
    private NotificationChannelProfileService channelProfileService;

    @Resource
    private NotificationProfileVariableService profileVariableService;

    @Resource
    private CustAccountService accountService;

    @Resource
    private FreemarkerService freemarkerService;

    @Resource
    private CustOperatorService custOperatorService;

    @Resource(name = "notificationProducer")
    private RocketMQProducer producer;

    @RocketMQListener(topic = "NOTIFICATION_TOPIC", consumer = "notificationConsumer")
    public void processNotification(final Object anMessage) {
        final MQMessage message = (MQMessage) anMessage;
        NotificationModel notificationModel = (NotificationModel) message.getObject();

        String profileName = notificationModel.getProfileName();

        Long custNo = notificationModel.getSendCustomer();

        NotificationProfile profile = profileService.findProfileByProfileNameAndCustNo(profileName, custNo);

        if (profile == null) {
            logger.error("CustNo:" + custNo + " ProfileName:" + profileName + " 消息模板没有找到!");
            return;
        }

        if (BetterStringUtils.equals(NotificationConstants.PROFILE_STATUS_ENABLED, profile.getBusinStatus()) == true) {
            List<NotificationChannelProfile> channelProfiles = channelProfileService.queryChannelProfileByProfileId(profile.getId());

            try {
                processNotification(profile, channelProfiles, notificationModel);
            }
            catch (UnsupportedEncodingException e) {
                logger.error("消息发送失败!", e);
            }
        }
        else {
            logger.info(profileName + " 消息模板已经禁用!");
        }
    }

    /**
     * 处理消息
     */
    private void processNotification(NotificationProfile anProfile, List<NotificationChannelProfile> anChannelProfiles,
            NotificationModel anNotificationModel) throws UnsupportedEncodingException {
        Long sendCustNo = anNotificationModel.getSendCustomer();
        Long sendOperId = anNotificationModel.getSendOperator();

        CustInfo sendCustomer = accountService.findCustInfo(sendCustNo);
        CustOperatorInfo sendOperator = custOperatorService.findCustOperatorInfo(sendOperId);

        Map<String, Object> param = anNotificationModel.getParam();
        BetterjrEntity entity = anNotificationModel.getEntity();

        param.put("entity", entity);
        param.put("sendCustNo", sendCustNo);
        param.put("sendCustName", sendCustomer.getCustName());
        param.put("sendCustomer", sendCustomer);
        param.put("sendOperator", sendOperator);
        
        // TODO @@@@@@@@@ batchNo
        for (NotificationChannelProfile channelProfile : anChannelProfiles) {
            if (BetterStringUtils.equals(NotificationConstants.PROFILE_STATUS_ENABLED, channelProfile.getBusinStatus()) == true) {
                Notification notification = addNotification(anProfile, channelProfile, param, sendOperator, sendCustomer);
                BTAssert.notNull(notification);

                processNotificationCustomer(notification, anNotificationModel, sendOperator, sendCustomer);
            }
            else {
                String channelName = getChannelName(channelProfile.getChannel());
                logger.info(anProfile.getProfileName() + " 消息模板, " + channelName + " 通道已经禁用!");
            }
        }
    }

    private void processNotificationCustomer(Notification anNotification, NotificationModel anNotificationModel, CustOperatorInfo anSendOperator,
            CustInfo anSendCustomer) {
        Collection<Pair<CustOperatorInfo, CustInfo>> operators = queryOperatorInfo(anNotificationModel);
        if (Collections3.isEmpty(operators) == false) { 
            for (Pair<CustOperatorInfo, CustInfo> tempOperator : operators) {
                // TODO @@@@@@@@@@@@@ 处理订阅关系
                addNotificationCustomer(anNotification, 
                        getSendNo(anNotification, tempOperator.getLeft()), 
                        tempOperator.getLeft().getId(),
                        tempOperator.getLeft().getName(), 
                        tempOperator.getRight().getCustNo(), 
                        tempOperator.getRight().getCustName(), 
                        anSendOperator,
                        anSendCustomer);
            }
        }

        Collection<String> emails = anNotificationModel.getReceiveEmails();
        if (Collections3.isEmpty(emails) == false 
                && BetterStringUtils.equals(NotificationConstants.CHANNEL_EMAIL, anNotification.getChannel())) {
            for (String email : emails) {
                addNotificationCustomer(anNotification, email, null, null, null, null, anSendOperator, anSendCustomer);
            }
        }

        Collection<String> mobiles = anNotificationModel.getReceiveMobiles();
        if (Collections3.isEmpty(mobiles) == false 
                && BetterStringUtils.equals(NotificationConstants.CHANNEL_SMS, anNotification.getChannel())) {
            for (String mobile : mobiles) {
                addNotificationCustomer(anNotification, mobile, null, null, null, null, anSendOperator, anSendCustomer);
            }
        }
        
        if (BetterStringUtils.equals(NotificationConstants.CHANNEL_EMAIL, anNotification.getChannel())) {
            processEmail(anNotification, anSendOperator, anSendCustomer);
        }
    }

    /**
     * 添加消息客户关系
     */
    private NotificationCustomer addNotificationCustomer(Notification anNotification, 
            String anSendNo, 
            Long anOperId, 
            String anOperName,
            Long anCustNo, 
            String anCustName, 
            CustOperatorInfo anSendOperator, 
            CustInfo anSendCustomer) {
        final NotificationCustomer tempNotificationCustomer = new NotificationCustomer();
        tempNotificationCustomer.setOperId(anOperId);
        tempNotificationCustomer.setOperName(anOperName);
        tempNotificationCustomer.setChannel(anNotification.getChannel());
        tempNotificationCustomer.setNotificationId(anNotification.getId());

        tempNotificationCustomer.setCustNo(anCustNo);
        tempNotificationCustomer.setCustName(anCustName);

        final NotificationCustomer notificationCustomer = notificationCustomerService.addNotificationCustomer(tempNotificationCustomer,
                anSendOperator, anSendCustomer);
        return notificationCustomer;
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
        logger.debug("INBOX");
    }

    private void processWechat(Notification anNotification, CustOperatorInfo anOperator, CustInfo anCustomer) {
        logger.debug("WECHAT");
    }

    /**
     * 添加消息
     */
    private Notification addNotification(NotificationProfile anProfile,
            NotificationChannelProfile anChannelProfile, 
            Map<String, Object> anParam,
            CustOperatorInfo anOperator, CustInfo anCustomer) throws UnsupportedEncodingException {
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
        //tempNotification.setBatchNo(batchNo);

        Notification notification = notificationService.addNotification(tempNotification, anOperator, anCustomer);
        return notification;
    }

    /**
     * 查询操作员与机构
     */
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

    /**
     * 解析模板内容
     */
    private String resolveTemplateContent(String anTemplateContent, List<NotificationProfileVariable> anProfileVariables, Map<String, Object> anParam)
            throws UnsupportedEncodingException {

        String decodeStr = Base64Coder.decodeString(anTemplateContent);
        String originStr = URLDecoder.decode(decodeStr, "UTF-8");
        final String templateContent = preproccessTemplateContent(originStr, anProfileVariables);

        StringBuffer sb = freemarkerService.processTemplateByContents(templateContent, anParam);
        return sb.toString();
    }

    /**
     * 模板预处理
     */
    private String preproccessTemplateContent(String anTemplateContent, List<NotificationProfileVariable> anProfileVariables) {
        String templateContent = anTemplateContent;
        for (NotificationProfileVariable profileVariable : anProfileVariables) {
            templateContent = replaceVariable(templateContent, profileVariable);
        }
        return templateContent;
    }

    /**
     * 模板变量替换
     */
    private String replaceVariable(String anTemplateContent, NotificationProfileVariable anProfileVariable) {
        anTemplateContent = anTemplateContent.replace(anProfileVariable.getVariableName(), anProfileVariable.getVariableValue());
        return anTemplateContent;
    }

    private Collection<CustOperatorInfo> queryOperatorByCustNo(Long anCustNo) {
        return custOperatorService.queryOperatorInfoByCustNo(anCustNo);
    }

    private CustOperatorInfo findOperatorById(Long anOperId) {
        return custOperatorService.findCustOperatorInfo(anOperId);
    }

    private String getChannelName(String anChannel) {
        String channel = null;
        switch (anChannel) {
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
        return channel;
    }

    private String getSendNo(Notification anNotification, CustOperatorInfo anReceiveOperator) {
        String sendNo = "";
        switch (anNotification.getChannel()) {
        case NotificationConstants.CHANNEL_INBOX:
            sendNo = String.valueOf(anReceiveOperator.getId());
            break;
        case NotificationConstants.CHANNEL_EMAIL:
            sendNo = anReceiveOperator.getEmail();
            break;
        case NotificationConstants.CHANNEL_SMS:
            sendNo = anReceiveOperator.getMobileNo();
            break;
        case NotificationConstants.CHANNEL_WECHAT:
            break;
        default:
        }
        return sendNo;
    }
}
