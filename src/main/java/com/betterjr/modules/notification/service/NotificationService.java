package com.betterjr.modules.notification.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.data.NotificationAttachment;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.mapper.pagehelper.PageHelper;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.document.entity.CustFileItem;
import com.betterjr.modules.document.service.CustFileItemService;
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

    /**
     * 添加
     */
    public Notification addNotification(final Notification anNotification, final CustOperatorInfo anOperator, final CustInfo anCustomer) {
        BTAssert.notNull(anNotification, "通知内容不允许为空!");

        anNotification.initAddValue(anOperator, anCustomer);
        this.insert(anNotification);

        return anNotification;
    }

    /**
     * 查询未读消息
     */
    public Page<Notification> queryUnreadNotification(final Map<String, Object> anParam, final int anFlag, final int anPageNum, final int anPageSize) {
        final CustOperatorInfo operator = UserUtils.getOperatorInfo();

        PageHelper.startPage(anPageNum, anPageSize, anFlag == 1);
        // 处理 LIKEsubject
        final String LIKEsubject = (String) anParam.get("LIKEsubject");
        if (BetterStringUtils.isNotBlank(LIKEsubject)) {
            anParam.put("LIKEsubject", "%" + LIKEsubject + "%");
        }
        return this.mapper.selectNotificationByCondition(operator.getId(), NotificationConstants.IS_READ_FALSE, anParam);
    }

    /**
     * 查询已读消息
     */
    public Page<Notification> queryReadNotification(final Map<String, Object> anParam, final int anFlag, final int anPageNum, final int anPageSize) {
        final CustOperatorInfo operator = UserUtils.getOperatorInfo();

        PageHelper.startPage(anPageNum, anPageSize, anFlag == 1);
        return this.mapper.selectNotificationByCondition(operator.getId(), NotificationConstants.IS_READ_TRUE, anParam);
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
    public Notification findNotification(final Long anId) {
        BTAssert.notNull(anId, "消息编号不允许为空!");

        final CustOperatorInfo operator = UserUtils.getOperatorInfo();
        // 检查此消息有没被此人接收
        checkNotificationCustomer(anId, operator.getId(), NotificationConstants.CHANNEL_INBOX);

        return this.selectByPrimaryKey(anId);
    }

    /**
     * 消息详情
     */
    public Notification findNotificationById(final Long anId) {
        BTAssert.notNull(anId, "消息编号不允许为空!");

        return this.selectByPrimaryKey(anId);
    }

    /**
     * 消息详情
     */
    public Notification findNotification(final Long anId, final Long anOperId, final String anChannel) {
        BTAssert.notNull(anId, "消息编号不允许为空!");

        // 检查此消息有没被此人接收
        checkNotificationCustomer(anId, anOperId, anChannel);

        return this.selectByPrimaryKey(anId);
    }
    /**
     * 设置消息已读
     */
    public int saveSetReadNotification(final Long anId) {
        BTAssert.notNull(anId, "消息编号不允许为空!");

        final CustOperatorInfo operator = UserUtils.getOperatorInfo();
        // 检查此消息有没被此人接收
        checkNotificationCustomer(anId, operator.getId(), NotificationConstants.CHANNEL_INBOX);

        return notificationCustomerService.saveSetReadNotification(anId, operator.getId());
    }

    /**
     *
     */
    private NotificationCustomer checkNotificationCustomer(final Long anId, final Long anOperId, final String anChannel) {
        BTAssert.notNull(anId, "编号不允许为空!");
        BTAssert.notNull(anOperId, "操作员编号不允许为空!");

        final NotificationCustomer notificationCustomer = notificationCustomerService.findNotifiCustomerByNotifiIdAndOperId(anId, anOperId, anChannel);
        BTAssert.notNull(notificationCustomer, "没有找到相应的站内消息接收记录!");
        return notificationCustomer;
    }

    /**
     *
     */
    public List<Notification> queryUnsendSmsNotification() {
        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("channel", NotificationConstants.CHANNEL_SMS);
        conditionMap.put("NEimmediate", NotificationConstants.IMMEDIATE_TRUE); // immediate 不为 1
        conditionMap.put("businStatus", new String[] { NotificationConstants.SEND_STATUS_FAIL, NotificationConstants.SEND_STATUS_NORMAL });
        return this.selectPropertyByPage(conditionMap, 1, 50, false);
    }

    /**
     *
     * @param anId
     * @param anBusinStatus
     * @return
     */
    public Notification saveNotificationStatus(final Long anId, final String anBusinStatus) {
        BTAssert.notNull(anId, "编号不允许为空");
        BTAssert.notNull(anBusinStatus, "状态不允许为空！");
        final Notification notification = findNotificationById(anId);
        if (null == notification) {
            return null;
        }
        notification.setBusinStatus(anBusinStatus);
        this.updateByPrimaryKeySelective(notification);
        return notification;
    }

    /**
     * 组织附件
     */
    public List<NotificationAttachment> buildAttachments(final Long anBatchNo) {
        final List<CustFileItem> fileItems = fileItemService.findCustFiles(anBatchNo);
        if (Collections3.isEmpty(fileItems) == false) {
            final List<NotificationAttachment> attachments = new ArrayList<>();

            for (final CustFileItem fileItem : fileItems) {
                attachments.add(new NotificationAttachment(fileItem.getFileName(), fileItem.getFilePath()));
            }

            return attachments;
        }
        return null;
    }
}
