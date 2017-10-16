package com.betterjr.modules.notice.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import com.betterjr.common.annotation.BetterjrMapper;
import com.betterjr.mapper.common.Mapper;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.notice.entity.Notice;

@BetterjrMapper
public interface NoticeMapper extends Mapper<Notice> {
    @SelectProvider(type = NoticeProvider.class, method = "selectUnreadNoticeSql")
    @ResultType(Notice.class)
    public Page<Notice> selectUnreadNotice(@Param("operId") Long operId, @Param("param") Map<String, Object> param);

    @SelectProvider(type = NoticeProvider.class, method = "selectReadNoticeSql")
    @ResultType(Notice.class)
    public Page<Notice> selectReadNotice(@Param("operId") Long operId, @Param("param") Map<String, Object> param);

    @Select("SELECT COUNT(sn.ID) " + "FROM t_sys_notice sn "
            + "LEFT JOIN t_sys_notice_cust snc ON sn.ID = snc.L_NOTICE_ID  "
            + "LEFT JOIN t_sys_notice_oper sno ON sn.ID = sno.L_NOTICE_ID AND (sno.L_OPERID = #{operId} AND sno.L_CUSTNO = snc.L_CUSTNO) "
            + "WHERE sn.C_BUSIN_STATUS = '1' AND EXISTS (SELECT cor.* FROM t_cust_operator_relation cor WHERE cor.L_OPERNO = #{operId} AND snc.L_CUSTNO = cor.L_CUSTNO) AND sno.ID IS NULL")
    @ResultType(Long.class)
    public Long selectCountUnreadNotice(@Param("operId") Long operId);

    @Select("SELECT COUNT(sn.ID) " + "FROM t_sys_notice sn "
            + "LEFT JOIN t_sys_notice_cust snc ON sn.ID = snc.L_NOTICE_ID "
            + "WHERE sn.C_BUSIN_STATUS = '1' AND EXISTS (SELECT cor.* FROM t_cust_operator_relation cor WHERE cor.L_OPERNO = #{operId} AND snc.L_CUSTNO = cor.L_CUSTNO) AND sn.ID = #{noticeId}")
    @ResultType(Long.class)
    public Long selectCountReceiveNotice(@Param("operId") Long operId, @Param("noticeId") Long noticeId);
}