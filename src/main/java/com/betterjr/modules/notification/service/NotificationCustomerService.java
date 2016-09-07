package com.betterjr.modules.notification.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.mapper.pagehelper.PageHelper;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.notification.constants.NotificationConstants;
import com.betterjr.modules.notification.dao.NotificationCustomerMapper;
import com.betterjr.modules.notification.entity.Notification;
import com.betterjr.modules.notification.entity.NotificationCustomer;

@Service
public class NotificationCustomerService extends BaseService<NotificationCustomerMapper, NotificationCustomer> {
    /**
     * 添加 NotificationCustomer
     */
    public NotificationCustomer addNotificationCustomer(NotificationCustomer anNotificationCustomer, CustOperatorInfo anOperator,
            CustInfo anCustomer) {
        BTAssert.notNull(anNotificationCustomer, "通知内容不允许为空!");

        anNotificationCustomer.initAddValue(anOperator, anCustomer);
        this.insert(anNotificationCustomer);

        return anNotificationCustomer;
    }

    /**
     * 通过 Notification Id 查询
     */
    public List<NotificationCustomer> queryNotifiCustomerByNotifiId(Long anNotifiId) {
        BTAssert.notNull(anNotifiId, "通知编号不允许为空!");

        return this.selectByProperty("notificationId", anNotifiId);
    }

    /**
     * 查找操作员接收的消息 通过 消息编号 操作员编号
     */
    public NotificationCustomer findNotifiCustomerByNotifiIdAndOperId(Long anNotifiId, Long anOperId) {
        BTAssert.notNull(anNotifiId, "通知编号不允许为空!");
        BTAssert.notNull(anOperId, "操作员编号不允许为空!");

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("notificationId", anNotifiId);
        conditionMap.put("operId", anOperId);
        conditionMap.put("channel", NotificationConstants.CHANNEL_INBOX);
        return Collections3.getFirst(this.selectByProperty(conditionMap));
    }

    /**
     * 设置消息为已读
     */
    public int saveSetReadNotification(Long anNotifiId, Long anOperId) {
        BTAssert.notNull(anNotifiId, "通知编号不允许为空!");
        BTAssert.notNull(anOperId, "操作员编号不允许为空!");
        
        final NotificationCustomer notificationCustomer = findNotifiCustomerByNotifiIdAndOperId(anNotifiId, anOperId);
        notificationCustomer.initModifyValue(NotificationConstants.IS_READ_TRUE);
        
        return this.updateByPrimaryKeySelective(notificationCustomer);
    }
    
    /**
     * 查询- notification 已经发送成功 但 notificationCustomer 未发送成功的记录 SMS
     */
    public List<NotificationCustomer> queryUnsendSmsNotificationCustomer(Integer anRetry) {
        PageHelper.startPage(1, 50, false);
        return this.mapper.selectUnsendNotificationCustomer(NotificationConstants.CHANNEL_SMS, anRetry);
    }
    
    /**
     * 查询- notification 已经发送成功 但 notificationCustomer 未发送成功的记录 EMAIL
     */
    public List<NotificationCustomer> queryUnsendEmailNotificationCustomer(Integer anRetry) {
        PageHelper.startPage(1, 50, false);
        return this.mapper.selectUnsendNotificationCustomer(NotificationConstants.CHANNEL_EMAIL, anRetry);
    }
    
    /**
     * retry 次数加1
     * @param anId
     * @return
     */
    public NotificationCustomer saveNotificationCustomerAddRetry(Long anId) {
        BTAssert.notNull(anId, "编号不允许为空");
        NotificationCustomer notificationCustomer = this.selectByPrimaryKey(anId);
        notificationCustomer.setRetryCount(notificationCustomer.getRetryCount() + 1);
        this.updateByPrimaryKeySelective(notificationCustomer);
        return notificationCustomer;
    }
    
    /**
     * 修改发送状态
     * @param anId
     * @param anBusinStatus
     * @return
     */
    public NotificationCustomer saveNotificationCustomerStatus(Long anId, String anBusinStatus) {
        BTAssert.notNull(anId, "编号不允许为空");
        BTAssert.notNull(anBusinStatus, "状态不允许为空！");
        NotificationCustomer notificationCustomer = this.selectByPrimaryKey(anId);
        notificationCustomer.setBusinStatus(anBusinStatus);
        this.updateByPrimaryKeySelective(notificationCustomer);
        return notificationCustomer;
    }
}
