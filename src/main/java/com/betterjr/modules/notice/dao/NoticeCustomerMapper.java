package com.betterjr.modules.notice.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import com.betterjr.common.annotation.BetterjrMapper;
import com.betterjr.mapper.common.Mapper;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.notice.entity.NoticeCustomer;

@BetterjrMapper
public interface NoticeCustomerMapper extends Mapper<NoticeCustomer> {
    
    @Select("SELECT distinct(snc.L_CUSTNO), snc.* FROM t_sys_notice_cust snc WHERE snc.L_NOTICE_ID #{noticeId}")
    @ResultType(NoticeCustomer.class)
    public Page<NoticeCustomer> selectNoticeCustomerByNoticeId(@Param("noticeId") Long noticeId);
}