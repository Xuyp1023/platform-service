package com.betterjr.modules.notice.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import com.betterjr.common.annotation.BetterjrMapper;
import com.betterjr.mapper.common.Mapper;
import com.betterjr.modules.notice.entity.NoticeCustomer;
import com.betterjr.modules.notice.entity.NoticeOperator;

@BetterjrMapper
public interface NoticeOperatorMapper extends Mapper<NoticeOperator> {

    @Select("SELECT distinct(snc.L_CUSTNO) as custNo, snc.C_CUSTNAME as custName "
            + "FROM t_sys_notice_cust snc "
            + "WHERE snc.L_NOTICE_ID = #{noticeId}")
    @ResultType(NoticeCustomer.class)
    public List<NoticeCustomer> selectNoticeCustomerByNoticeId(@Param("noticeId") Long noticeId);
}
