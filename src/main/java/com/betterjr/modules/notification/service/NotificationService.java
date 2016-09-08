package com.betterjr.modules.notification.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.betterjr.common.data.NotificationAttachment;
import com.betterjr.common.mq.codec.MQCodecType;
import com.betterjr.common.mq.core.RocketMQProducer;
import com.betterjr.common.mq.message.MQMessage;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.mapper.pagehelper.PageHelper;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.document.entity.CustFileItem;
import com.betterjr.modules.document.service.CustFileItemService;
import com.betterjr.modules.notification.NotificationModel;
import com.betterjr.modules.notification.constants.NotificationConstants;
import com.betterjr.modules.notification.dao.NotificationMapper;
import com.betterjr.modules.notification.entity.Notification;
import com.betterjr.modules.notification.entity.NotificationCustomer;

@Service
public class NotificationService extends BaseService<NotificationMapper, Notification> {

    @Resource
    private CustFileItemService fileItemService;

    @Resource
    private NotificationCustomerService notificationCustomerService;

    @Resource(name = "betterProducer")
    private RocketMQProducer betterProducer;

    /**
     * 添加
     */
    public Notification addNotification(Notification anNotification, CustOperatorInfo anOperator, CustInfo anCustomer) {
        BTAssert.notNull(anNotification, "通知内容不允许为空!");

        anNotification.initAddValue(anOperator, anCustomer);
        this.insert(anNotification);

        return anNotification;
    }

    /**
     * 查询未读消息
     */
    public Page<Notification> queryUnreadNotification(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        CustOperatorInfo operator = UserUtils.getOperatorInfo();

        PageHelper.startPage(anPageNum, anPageSize, anFlag == 1);
        return this.mapper.selectNotificationByCondition(operator.getId(), NotificationConstants.IS_READ_FALSE);
    }

    /**
     * 查询已读消息
     */
    public Page<Notification> queryReadNotification(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        CustOperatorInfo operator = UserUtils.getOperatorInfo();

        PageHelper.startPage(anPageNum, anPageSize, anFlag == 1);
        return this.mapper.selectNotificationByCondition(operator.getId(), NotificationConstants.IS_READ_TRUE);
    }

    /**
     * 查询未读消息数量
     */
    public Long queryCountUnreadNotification() {
        final CustOperatorInfo operator = UserUtils.getOperatorInfo();
        return this.mapper.selectCountUnreadNotification(operator.getId());
    }

    /**
     * 消息详情
     */
    public Notification findNotification(Long anId) {
        BTAssert.notNull(anId, "消息编号不允许为空!");

        CustOperatorInfo operator = UserUtils.getOperatorInfo();
        // 检查此消息有没被此人接收
        checkNotificationCustomer(anId, operator.getId());

        return this.selectByPrimaryKey(anId);
    }

    /**
     * 设置消息已读
     */
    public int saveSetReadNotification(Long anId) {
        BTAssert.notNull(anId, "消息编号不允许为空!");

        CustOperatorInfo operator = UserUtils.getOperatorInfo();
        // 检查此消息有没被此人接收
        checkNotificationCustomer(anId, operator.getId());

        return notificationCustomerService.saveSetReadNotification(anId, operator.getId());
    }

    /**
     * 
     */
    private NotificationCustomer checkNotificationCustomer(Long anId, Long anOperId) {
        BTAssert.notNull(anId, "编号不允许为空!");
        BTAssert.notNull(anOperId, "操作员编号不允许为空!");

        final NotificationCustomer notificationCustomer = notificationCustomerService.findNotifiCustomerByNotifiIdAndOperId(anId, anOperId);
        BTAssert.notNull(notificationCustomer, "没有找到相应的站内消息接收记录!");
        return notificationCustomer;
    }

    /**
     * 
     */
    public List<Notification> queryUnsendSmsNotification() {
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("channel", NotificationConstants.CHANNEL_SMS);
        conditionMap.put("businStatus", new String[] { NotificationConstants.SEND_STATUS_FAIL, NotificationConstants.SEND_STATUS_NORMAL });
        return this.selectPropertyByPage(conditionMap, 1, 50, false);
    }

    /**
     * 
     * @param anId
     * @param anBusinStatus
     * @return
     */
    public Notification saveNotificationStatus(Long anId, String anBusinStatus) {
        BTAssert.notNull(anId, "编号不允许为空");
        BTAssert.notNull(anBusinStatus, "状态不允许为空！");
        Notification notification = this.selectByPrimaryKey(anId);
        notification.setBusinStatus(anBusinStatus);
        this.updateByPrimaryKeySelective(notification);
        return notification;
    }

    /**
     * 组织附件
     */
    public List<NotificationAttachment> buildAttachments(Long anBatchNo) {
        List<CustFileItem> fileItems = fileItemService.findCustFiles(anBatchNo);
        if (Collections3.isEmpty(fileItems) == false) {
            List<NotificationAttachment> attachments = new ArrayList<>();

            for (CustFileItem fileItem : fileItems) {
                attachments.add(new NotificationAttachment(fileItem.getFileName(), fileItem.getFilePath()));
            }

            return attachments;
        }
        return null;
    }

    /**
     * 发送消息通知 NotificationModel
     */
    public boolean sendNotification(NotificationModel anNotificationModel) {
        final MQMessage message = new MQMessage(NotificationConstants.NOTIFICATION_TOPIC, MQCodecType.FST);
        message.setObject(anNotificationModel);

        try {
            final SendResult sendResult = betterProducer.sendMessage(message);

            if (sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
                return true;
            }
            else {
                logger.warn("消息通知发送失败 SendResult=" + sendResult.toString());
                return false;
            }
        }
        catch (Exception e) {
            logger.error("消息通知发送错误", e);
            return false;
        }
    }
}
