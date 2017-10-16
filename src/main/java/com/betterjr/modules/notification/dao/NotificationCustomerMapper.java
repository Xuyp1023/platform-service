package com.betterjr.modules.notification.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import com.betterjr.common.annotation.BetterjrMapper;
import com.betterjr.mapper.common.Mapper;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.notification.entity.NotificationCustomer;

@BetterjrMapper
public interface NotificationCustomerMapper extends Mapper<NotificationCustomer> {
    @Select("SELECT nc.* FROM t_sys_notifi_cust nc, t_sys_notifi n WHERE nc.C_CHANNEL = #{channel} AND nc.N_RETRY_COUNT <= #{retry} AND nc.C_BUSIN_STATUS IN ('0', '2') AND nc.L_NOTIFICATION_ID = n.ID AND n.C_BUSIN_STATUS = '1'")
    @ResultType(NotificationCustomer.class)
    public Page<NotificationCustomer> selectUnsendNotificationCustomer(@Param("channel") String channel,
            @Param("retry") Integer retry);
}