package com.betterjr.modules.notification.handler;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
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
import com.betterjr.common.service.FreemarkerService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterDateUtils;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.account.service.CustOperatorService;
import com.betterjr.modules.document.service.CustFileItemService;
import com.betterjr.modules.notification.NotificationModel;
import com.betterjr.modules.notification.NotificationModel.CustOperPair;
import com.betterjr.modules.notification.constants.NotificationConstants;
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
import com.betterjr.modules.notification.service.NotificationSubscribeService;

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
    private NotificationSubscribeService subscribeService;

    @Resource
    private CustAccountService accountService;

    @Resource
    private FreemarkerService freemarkerService;

    @Resource
    private CustOperatorService custOperatorService;

    @Resource
    private CustFileItemService fileItemService;

    @Resource(name = "betterProducer")
    private RocketMQProducer betterProducer;

    public NotificationHandlerService() {

    }

    @RocketMQListener(topic = "NOTIFICATION_TOPIC", consumer = "betterConsumer")
    public void processNotification(final Object anMessage) {
        final MQMessage message = (MQMessage) anMessage;
        final NotificationModel notificationModel = (NotificationModel) message.getObject();

        final String profileName = notificationModel.getProfileName();
        logger.info("NOTIFICATION_TOPIC: profileName=" + profileName);

        final Long custNo = notificationModel.getSendCustomer().getCustNo();
        final NotificationProfile profile = profileService.findProfileByProfileNameAndCustNo(profileName, custNo);

        if (profile == null) {
            logger.error("CustNo:" + custNo + " ProfileName:" + profileName + " 消息模板没有找到!");
            return;
        }

        if (StringUtils.equals(NotificationConstants.PROFILE_STATUS_ENABLED, profile.getBusinStatus()) == true) {
            final List<NotificationChannelProfile> channelProfiles = channelProfileService.queryChannelProfileByProfileId(profile.getId());

            try {
                processNotification(profile, channelProfiles, notificationModel);
            }
            catch (final Exception e) {
                logger.error("消息发送失败!" + profile, e);
            }
        }
        else {
            logger.info(profileName + " 消息模板已经禁用!");
        }
    }

    /**
     * 处理消息
     */
    private void processNotification(final NotificationProfile anProfile, final List<NotificationChannelProfile> anChannelProfiles,
            final NotificationModel anNotificationModel) throws Exception {

        final CustInfo sendCustomer = anNotificationModel.getSendCustomer();
        final CustOperatorInfo sendOperator = anNotificationModel.getSendOperator();

        final Map<String, Object> param = anNotificationModel.getParam();
        final BetterjrEntity entity = anNotificationModel.getEntity();

        param.put("entity", entity);
        param.put("sendCustNo", sendCustomer.getCustNo());
        param.put("sendCustName", sendCustomer.getCustName());
        param.put("sendCustomer", sendCustomer);
        param.put("sendOperId", sendOperator.getId());
        param.put("sendOperName", sendOperator.getName());
        param.put("sendOperator", sendOperator);

        final CustOperatorInfo operator = anNotificationModel.getSendOperator();
        final List<Long> fileItems = anNotificationModel.getFileItems();
        final Long batchNo = fileItemService.updateAndDuplicateConflictFileItemInfo(fileItems, null, operator);

        for (final NotificationChannelProfile channelProfile : anChannelProfiles) {
            if (StringUtils.equals(NotificationConstants.PROFILE_STATUS_ENABLED, channelProfile.getBusinStatus()) == true) {
                final Notification notification = addNotification(anProfile, channelProfile, param, sendOperator, sendCustomer, batchNo);
                BTAssert.notNull(notification);

                processNotificationCustomer(notification, anNotificationModel, sendOperator, sendCustomer);
            }
            else {
                final String channelName = getChannelName(channelProfile.getChannel());
                logger.info(anProfile.getProfileName() + " 消息模板, " + channelName + " 通道已经禁用!");
            }
        }
    }

    private void processNotificationCustomer(final Notification anNotification, final NotificationModel anNotificationModel, final CustOperatorInfo anSendOperator,
            final CustInfo anSendCustomer) {
        final Collection<Pair<CustOperatorInfo, CustInfo>> operators = queryOperatorInfo(anNotificationModel);
        if (Collections3.isEmpty(operators) == false) {
            for (final Pair<CustOperatorInfo, CustInfo> tempOperator : operators) {
                // 未订阅不产生数据
                /*final boolean subscribeFlag = subscribeService.checkSubscribe(anNotification.getProfileId(),
                        anNotification.getChannelProfileId(),
                        tempOperator.getRight(),
                        tempOperator.getLeft(),
                        anSendCustomer.getCustNo());

                if (subscribeFlag) {*/
                addNotificationCustomer(anNotification,
                        getSendNo(anNotification, tempOperator.getLeft()),
                        tempOperator.getLeft(),
                        tempOperator.getRight(),
                        anSendOperator);
                //}
            }
        }

        final Collection<String> emails = anNotificationModel.getReceiveEmails();
        if (Collections3.isEmpty(emails) == false
                && StringUtils.equals(NotificationConstants.CHANNEL_EMAIL, anNotification.getChannel())) {
            for (final String email : emails) {
                addNotificationCustomer(anNotification, email, null, null, anSendOperator);
            }
        }

        final Collection<String> mobiles = anNotificationModel.getReceiveMobiles();
        if (Collections3.isEmpty(mobiles) == false
                && StringUtils.equals(NotificationConstants.CHANNEL_SMS, anNotification.getChannel())) {
            for (final String mobile : mobiles) {
                addNotificationCustomer(anNotification, mobile, null, null, anSendOperator);
            }
        }

        if (BetterStringUtils.equals(NotificationConstants.CHANNEL_EMAIL, anNotification.getChannel())) {
            processEmail(anNotification, anSendOperator, anSendCustomer);
        }

        if (BetterStringUtils.equals(NotificationConstants.CHANNEL_WECHAT, anNotification.getChannel())) {
            processWechat(anNotification, anSendOperator, anSendCustomer);
        }

        // 需要即时发送的短信消息
        if (BetterStringUtils.equals(NotificationConstants.CHANNEL_SMS, anNotification.getChannel())
                && BetterStringUtils.equals(anNotification.getImmediate(), NotificationConstants.IMMEDIATE_TRUE)) {
            processSms(anNotification, anSendOperator, anSendCustomer);
        }
    }

    /**
     * 添加消息客户关系
     */
    private NotificationCustomer addNotificationCustomer(final Notification anNotification,
            final String anSendNo,
            final CustOperatorInfo anOperator,
            final CustInfo anCustInfo,
            final CustOperatorInfo anSendOperator) {
        final NotificationCustomer tempNotificationCustomer = new NotificationCustomer();
        tempNotificationCustomer.setChannel(anNotification.getChannel());
        tempNotificationCustomer.setNotificationId(anNotification.getId());

        if (anOperator != null) {
            tempNotificationCustomer.setOperId(anOperator.getId());
            tempNotificationCustomer.setOperName(anOperator.getName());
            tempNotificationCustomer.setOperOrg(anOperator.getOperOrg());
        }

        if (anCustInfo != null) {
            tempNotificationCustomer.setCustNo(anCustInfo.getCustNo());
            tempNotificationCustomer.setCustName(anCustInfo.getCustName());
        }

        tempNotificationCustomer.setSendNo(anSendNo);

        final NotificationCustomer notificationCustomer = notificationCustomerService.addNotificationCustomer(tempNotificationCustomer,
                anSendOperator);
        return notificationCustomer;
    }

    /**
     * 处理email消息
     */
    private void processEmail(final Notification anNotification, final CustOperatorInfo anSendOperator, final CustInfo anSendCustomer) {
        final MQMessage message = new MQMessage(NotificationConstants.NOTIFICATION_EMAIL_TOPIC, MQCodecType.FST);
        message.setObject(anNotification);
        message.addHead("sendOperator", anSendOperator);
        message.addHead("sendCustomer", anSendCustomer);
        try {
            final SendResult sendResult = betterProducer.sendMessage(message);

            if (sendResult.getSendStatus().equals(SendStatus.SEND_OK) == false) {
                logger.warn("消息通知发送失败 SendResult=" + sendResult.toString());
            }
        }
        catch (final Exception e) {
            logger.error("消息通知发送错误", e);
        }
    }

    /**
     * 处理wechat消息
     */
    private void processWechat(final Notification anNotification, final CustOperatorInfo anSendOperator, final CustInfo anSendCustomer) {
        final MQMessage message = new MQMessage(NotificationConstants.NOTIFICATION_WECHAT_TOPIC, MQCodecType.FST);
        message.setObject(anNotification);
        message.addHead("sendOperator", anSendOperator);
        message.addHead("sendCustomer", anSendCustomer);
        try {
            final SendResult sendResult = betterProducer.sendMessage(message);

            if (sendResult.getSendStatus().equals(SendStatus.SEND_OK) == false) {
                logger.warn("消息通知发送失败 SendResult=" + sendResult.toString());
            }
        }
        catch (final Exception e) {
            logger.error("消息通知发送错误", e);
        }
    }

    /**
     * 处理sms消息
     */
    private void processSms(final Notification anNotification, final CustOperatorInfo anSendOperator, final CustInfo anSendCustomer) {
        final MQMessage message = new MQMessage(NotificationConstants.NOTIFICATION_SMS_TOPIC, MQCodecType.FST);
        message.setObject(anNotification);
        message.addHead("sendOperator", anSendOperator);
        message.addHead("sendCustomer", anSendCustomer);
        try {
            final SendResult sendResult = betterProducer.sendMessage(message);

            if (sendResult.getSendStatus().equals(SendStatus.SEND_OK) == false) {
                logger.warn("消息通知发送失败 SendResult=" + sendResult.toString());
            }
        }
        catch (final Exception e) {
            logger.error("消息通知发送错误", e);
        }
    }

    /**
     * 添加消息
     */
    private Notification addNotification(final NotificationProfile anProfile,
            final NotificationChannelProfile anChannelProfile,
            final Map<String, Object> anParam,
            final CustOperatorInfo anOperator,
            final CustInfo anCustomer,
            final Long anBatchNo) throws UnsupportedEncodingException {
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
        tempNotification.setImmediate(BetterStringUtils.isBlank(anProfile.getImmediate()) ? NotificationConstants.IMMEDIATE_FALSE : NotificationConstants.IMMEDIATE_TRUE);
        tempNotification.setBatchNo(anBatchNo);

        final Notification notification = notificationService.addNotification(tempNotification, anOperator, anCustomer);
        return notification;
    }

    /**
     * 查询操作员与机构
     */
    private Collection<Pair<CustOperatorInfo, CustInfo>> queryOperatorInfo(final NotificationModel anNotificationModel) {
        final List<CustOperPair> receivers = anNotificationModel.getReceivers();

        final Set<Pair<CustOperatorInfo, CustInfo>> operators = new HashSet<>();

        receivers.forEach(custOper -> {
            if (custOper.getOperator() != null && custOper.getCustomer() != null) {
                final CustOperatorInfo operator = findOperatorById(custOper.getOperator());
                final CustInfo custInfo = accountService.findCustInfo(custOper.getCustomer());

                operators.add(new ImmutablePair<CustOperatorInfo, CustInfo>(operator, custInfo));
            } else if (custOper.getOperator() != null && custOper.getCustomer() == null) {
                final CustOperatorInfo operator = findOperatorById(custOper.getOperator());

                operators.add(new ImmutablePair<CustOperatorInfo, CustInfo>(operator, null));
            } else {
                final Long custNo = custOper.getCustomer();
                final Collection<CustOperatorInfo> tempOperators = queryOperatorByCustNo(custNo);
                final CustInfo tempCustomer = accountService.findCustInfo(custNo);

                tempOperators.forEach(operator -> {
                    operators.add(new ImmutablePair<CustOperatorInfo, CustInfo>(operator, tempCustomer));
                });
            }

        });


        return operators;
    }

    /**
     * 解析模板内容
     */
    private String resolveTemplateContent(final String anTemplateContent, final List<NotificationProfileVariable> anProfileVariables, final Map<String, Object> anParam)
            throws UnsupportedEncodingException {

        final String templateContent = preproccessTemplateContent(anTemplateContent, anProfileVariables);

        final StringBuffer sb = freemarkerService.processTemplateByContents(templateContent, anParam);
        return sb.toString();
    }

    /**
     * 模板预处理
     */
    private String preproccessTemplateContent(final String anTemplateContent, final List<NotificationProfileVariable> anProfileVariables) {
        String templateContent = anTemplateContent;
        for (final NotificationProfileVariable profileVariable : anProfileVariables) {
            templateContent = replaceVariable(templateContent, profileVariable);
        }
        return templateContent;
    }

    /**
     * 模板变量替换
     */
    private String replaceVariable(String anTemplateContent, final NotificationProfileVariable anProfileVariable) {
        anTemplateContent = anTemplateContent.replace(anProfileVariable.getVariableName(), anProfileVariable.getVariableValue());
        return anTemplateContent;
    }

    private Collection<CustOperatorInfo> queryOperatorByCustNo(final Long anCustNo) {
        return custOperatorService.queryOperatorInfoByCustNo(anCustNo);
    }

    private CustOperatorInfo findOperatorById(final Long anOperId) {
        return custOperatorService.findCustOperatorInfo(anOperId);
    }

    private String getChannelName(final String anChannel) {
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
            channel = "微信";
            break;
        default:
        }
        return channel;
    }

    private String getSendNo(final Notification anNotification, final CustOperatorInfo anReceiveOperator) {
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
            sendNo = String.valueOf(anReceiveOperator.getId());
            break;
        default:
        }
        return sendNo;
    }
}
