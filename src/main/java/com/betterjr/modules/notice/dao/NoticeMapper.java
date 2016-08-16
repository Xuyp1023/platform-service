package com.betterjr.modules.notice.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import com.betterjr.common.annotation.BetterjrMapper;
import com.betterjr.mapper.common.Mapper;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.notice.entity.Notice;

@BetterjrMapper
public interface NoticeMapper extends Mapper<Notice> {
    @Select("SELECT sn.*, snc.L_OPERID AS receiveOperId, snc.C_OPERNAME AS receiveOperName, snc.L_CUSTNO AS receiveCustNo, snc.C_CUSTNAME AS receiveCustName FROM t_sys_notice sn, t_sys_notice_cust snc WHERE sn.C_BUSIN_STATUS = '1' AND sn.ID = snc.L_NOTICE_ID AND snc.L_OPERID = #{operId} AND snc.C_IS_READ = #{isRead} AND snc.C_IS_DELETED = '0'")
    @ResultType(Notice.class)
    public Page<Notice> selectNoticeByCondition(@Param("operId") Long operId, @Param("isRead") Boolean isRead);

    @Select("SELECT COUNT(sn.ID) FROM t_sys_notice sn, t_sys_notice_cust snc WHERE sn.C_BUSIN_STATUS = '1' AND sn.ID = snc.L_NOTICE_ID AND snc.L_OPERID = #{operId} AND snc.C_IS_READ = '0' AND snc.C_IS_DELETED = '0'")
    @ResultType(Long.class)
    public Long selectCountUnreadNotice(@Param("operId") Long operId);
}