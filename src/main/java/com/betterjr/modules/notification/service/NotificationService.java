package com.betterjr.modules.notification.service;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.notification.NotificationConstants;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.mapper.pagehelper.PageHelper;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.notification.dao.NotificationMapper;
import com.betterjr.modules.notification.entity.Notification;
import com.betterjr.modules.notification.entity.NotificationCustomer;

@Service
public class NotificationService extends BaseService<NotificationMapper, Notification> {
    @Resource
    private NotificationCustomerService notificationCustomerService;

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

}
