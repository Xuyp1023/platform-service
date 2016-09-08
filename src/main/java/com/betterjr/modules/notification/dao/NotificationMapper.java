package com.betterjr.modules.notification.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import com.betterjr.common.annotation.BetterjrMapper;
import com.betterjr.mapper.common.Mapper;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.notification.entity.Notification;

@BetterjrMapper
public interface NotificationMapper extends Mapper<Notification> {
    @SelectProvider(type = NotificationProvider.class, method = "selectNotificationByConditionSql")
    @ResultType(Notification.class)
    public Page<Notification> selectNotificationByCondition(@Param("operId") Long operId, @Param("isRead") Boolean isRead,  @Param("param") Map<String, Object> param);
    
    @Select("SELECT COUNT(sn.ID) FROM t_sys_notifi sn, t_sys_notifi_cust snc WHERE sn.ID = snc.L_NOTIFICATION_ID AND snc.L_OPERID = #{operId} AND snc.C_IS_READ = '0' AND snc.C_IS_DELETED = '0' AND sn.C_CHANNEL='0'")
    @ResultType(Long.class)
    public Long selectCountUnreadNotification(@Param("operId") Long operId);
}