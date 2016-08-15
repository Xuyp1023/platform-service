package com.betterjr.modules.notification.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import com.betterjr.common.annotation.BetterjrMapper;
import com.betterjr.mapper.common.Mapper;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.notification.entity.Notification;

@BetterjrMapper
public interface NotificationMapper extends Mapper<Notification> {
    @Select("SELECT sn.* FROM t_sys_notifi sn, t_sys_notifi_cust snc WHERE sn.ID = snc.L_NOTIFICATION_ID AND snc.L_OPERID = #{operId} AND snc.C_IS_READ = #{isRead} AND snc.C_IS_DELETED = '0' AND sn.C_CHANNEL='0'")
    @ResultType(Notification.class)
    public Page<Notification> selectNotificationByCondition(@Param("operId") Long operId, @Param("isRead") Boolean isRead);
    
    @Select("SELECT COUNT(sn.ID) FROM t_sys_notifi sn, t_sys_notifi_cust snc WHERE sn.ID = snc.L_NOTIFICATION_ID AND snc.L_OPERID = #{operId} AND snc.C_IS_READ = '0' AND snc.C_IS_DELETED = '0' AND sn.C_CHANNEL='0'")
    @ResultType(Long.class)
    public Long selectCountUnreadNotification(@Param("operId") Long operId);
}