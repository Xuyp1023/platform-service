package com.betterjr.modules.notification.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import com.betterjr.common.annotation.BetterjrMapper;
import com.betterjr.mapper.common.Mapper;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.notification.entity.NotificationSubscribe;
import com.betterjr.modules.notification.model.ChannelSubscribeModel;
import com.betterjr.modules.notification.model.ProfileSubscribeModel;

@BetterjrMapper
public interface NotificationSubscribeMapper extends Mapper<NotificationSubscribe> {

    @Select("SELECT ID as id, C_PROFILE_NAME as profileName, L_CUSTNO as custNo, C_CUSTNAME as custName " +
            "FROM t_sys_notifi_profile snp " +
            "WHERE (snp.L_CUSTNO IN (" +
            "SELECT cr1.L_RELATE_CUSTNO FROM t_cust_relation cr1 WHERE cr1.L_CUSTNO = #{custNo}) " +
            "OR snp.L_CUSTNO IN (" +
            "SELECT cr2.L_CUSTNO FROM t_cust_relation cr2 WHERE cr2.L_RELATE_CUSTNO = #{custNo} " +
            ") OR snp.L_CUSTNO = 102202018) AND C_SUBSCRIBE_ENABLE = '1'")
    @ResultType(ProfileSubscribeModel.class)
    public Page<ProfileSubscribeModel> selectProfileSubscribe(@Param("custNo") Long custNo);

    @Select("SELECT ncp.ID as id, ncp.L_PROFILE_ID as profileId, ncp.C_CHANNEL as channel, (CASE WHEN ns.ID IS NULL THEN 0 ELSE 1 END) as subscribed " +
            "FROM t_sys_notifi_chan_profile ncp LEFT JOIN t_sys_notifi_sub ns ON (ncp.ID = ns.L_CHANNEL_PROFILE_ID AND ns.L_OPERID = #{operId} AND ns.L_CUSTNO = #{custNo} AND ns.L_SOURCE_CUSTNO = #{sourceCustNo}) " +
            "WHERE ncp.L_PROFILE_ID = #{profileId} AND ncp.C_BUSIN_STATUS = '1'")
    @ResultType(ChannelSubscribeModel.class)
    public List<ChannelSubscribeModel> selectChannelSubscribe(@Param("operId") Long operId,@Param("custNo") Long custNo,@Param("sourceCustNo") Long sourceCustNo,@Param("profileId") Long profileId);
}