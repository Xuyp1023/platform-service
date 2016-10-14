package com.betterjr.modules.notification.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import com.betterjr.common.annotation.BetterjrMapper;
import com.betterjr.mapper.common.Mapper;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.notification.entity.NotificationSubscribe;
import com.betterjr.modules.notification.model.ChannelSubscribeModel;
import com.betterjr.modules.notification.model.ProfileSubscribeModel;

@BetterjrMapper
public interface NotificationSubscribeMapper extends Mapper<NotificationSubscribe> {

    @SelectProvider(type = NotificationProvider.class, method = "selectNotificationProfileByCustsSql")
    @ResultType(ProfileSubscribeModel.class)
    public Page<ProfileSubscribeModel> selectProfileSubscribe(Map<String, Object> param);

    @Select("SELECT ncp.ID as id, ns.L_CUSTNO AS custNo, ns.L_SOURCE_CUSTNO AS sourceCustNo, np.C_PROFILE_NAME AS profileName, ncp.C_CHANNEL AS channel, (CASE WHEN ns.ID IS NULL THEN 1 ELSE 0 END) AS subscribed " +
            "FROM t_sys_notifi_chan_profile ncp LEFT JOIN t_sys_notifi_profile np ON ncp.L_PROFILE_ID = np.ID AND np.ID < 0 " +
            "LEFT JOIN t_sys_notifi_sub ns ON (np.C_PROFILE_NAME = ns.C_PROFILE_NAME AND ncp.C_CHANNEL = ns.C_CHANNEL AND ns.L_CUSTNO = #{custNo} AND ns.L_SOURCE_CUSTNO = #{sourceCustNo}) " +
            "WHERE np.C_PROFILE_NAME = #{profileName} AND ncp.C_BUSIN_STATUS = '1' ")
    @ResultType(ChannelSubscribeModel.class)
    public List<ChannelSubscribeModel> selectChannelSubscribe(@Param("custNo") Long custNo,@Param("sourceCustNo") Long sourceCustNo,@Param("profileName") String profileName);
}