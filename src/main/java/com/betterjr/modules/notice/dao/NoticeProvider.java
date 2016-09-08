// Copyright (c) 2014-2016 Betty. All rights reserved.
// ============================================================================
// CURRENT VERSION 
// ============================================================================
// CHANGE LOG
// V2.0 : 2016年9月7日, liuwl, creation
// ============================================================================
package com.betterjr.modules.notice.dao;

import java.util.Map;
import java.util.Optional;

import com.betterjr.common.utils.BetterStringUtils;

/**
 * @author liuwl
 *
 */
public class NoticeProvider {
    /**
     * 查看未读公告
     * @param anParam
     * @return
     */
    @SuppressWarnings("unchecked")
    public String selectUnreadNoticeSql(final Map<String, Object> anParam) {
        final StringBuilder sql = new StringBuilder("SELECT sn.ID as id, sn.C_SUBJECT as subject, sn.D_PUBLISH_DATE as publishDate, sn.T_PUBLISH_TIME as publishTime, sn.L_CUSTNO as custNo, sn.C_CUSTNAME as custName, snc.L_CUSTNO as receiveCustNo, snc.C_CUSTNAME as receiveCustName ");
        sql.append("FROM t_sys_notice sn ");
        sql.append("LEFT JOIN t_sys_notice_cust snc ON sn.ID = snc.L_NOTICE_ID ");
        sql.append("LEFT JOIN t_sys_notice_oper sno ON sn.ID = sno.L_NOTICE_ID AND (sno.L_OPERID = #{operId} AND sno.L_CUSTNO = snc.L_CUSTNO) ");
        sql.append("WHERE sn.C_BUSIN_STATUS = '1' AND EXISTS (SELECT cor.* FROM t_cust_operator_relation cor WHERE cor.L_OPERNO = #{operId} AND snc.L_CUSTNO = cor.L_CUSTNO) AND sno.ID IS NULL ");
        Map<String, Object> param = (Map<String, Object>) anParam.get("param");
        Optional<Object> custNo = Optional.ofNullable(param.get("custNo"));
        custNo.ifPresent(data -> {
            final String tempCustNo = String.valueOf(data);
            if (BetterStringUtils.isBlank(tempCustNo) == false) {
                sql.append(" AND snc.L_CUSTNO = ");
                sql.append(tempCustNo);
                sql.append(" ");
            }
        });
        Optional<Object> LIKEsubject = Optional.ofNullable(param.get("LIKEsubject"));
        LIKEsubject.ifPresent(data -> {
            final String subject = String.valueOf(data);
            if (BetterStringUtils.isBlank(subject) == false) {
                sql.append(" AND sn.C_SUBJECT LIKE '%");
                sql.append(subject);
                sql.append("%' ");
            }
        });
        Optional<Object> GTEpublishDate = Optional.ofNullable(param.get("GTEpublishDate"));
        GTEpublishDate.ifPresent(data -> {
            final String publishDate = String.valueOf(data);
            if (BetterStringUtils.isBlank(publishDate) == false) {
                sql.append(" AND sn.D_PUBLISH_DATE >= ");
                sql.append(publishDate);
                sql.append(" ");
            }
        });
        Optional<Object> LTEpublishDate = Optional.ofNullable(param.get("LTEpublishDate"));
        LTEpublishDate.ifPresent(data -> {
            final String publishDate = String.valueOf(data);
            if (BetterStringUtils.isBlank(publishDate) == false) {
                sql.append(" AND sn.D_PUBLISH_DATE <= ");
                sql.append(publishDate);
                sql.append(" ");
            }
        });
        return sql.toString();
    }
    
    /**
     * 查询已读公告
     * @param anParam
     * @return
     */
    @SuppressWarnings("unchecked")
    public String selectReadNoticeSql(final Map<String, Object> anParam) {
        final StringBuilder sql = new StringBuilder("SELECT sn.ID as id, sn.C_SUBJECT as subject, sn.D_PUBLISH_DATE as publishDate, sn.T_PUBLISH_TIME as publishTime, sn.L_CUSTNO as custNo, sn.C_CUSTNAME as custName, snc.L_CUSTNO as receiveCustNo, snc.C_CUSTNAME as receiveCustName ");
        sql.append("FROM t_sys_notice sn ");
        sql.append("LEFT JOIN t_sys_notice_cust snc ON sn.ID = snc.L_NOTICE_ID ");
        sql.append("LEFT JOIN t_sys_notice_oper sno ON sn.ID = sno.L_NOTICE_ID AND (sno.L_OPERID = #{operId} AND sno.L_CUSTNO = snc.L_CUSTNO) ");
        sql.append("WHERE sn.C_BUSIN_STATUS = '1' AND EXISTS (SELECT cor.* FROM t_cust_operator_relation cor WHERE cor.L_OPERNO = #{operId} AND snc.L_CUSTNO = cor.L_CUSTNO) AND sno.ID IS NOT NULL ");
        Map<String, Object> param = (Map<String, Object>) anParam.get("param");
        Optional<Object> custNo = Optional.ofNullable(param.get("custNo"));
        custNo.ifPresent(data -> {
            final String tempCustNo = String.valueOf(data);
            if (BetterStringUtils.isBlank(tempCustNo) == false) {
                sql.append(" AND snc.L_CUSTNO = ");
                sql.append(tempCustNo);
                sql.append(" ");
            }
        });
        Optional<Object> LIKEsubject = Optional.ofNullable(param.get("LIKEsubject"));
        LIKEsubject.ifPresent(data -> {
            final String subject = String.valueOf(data);
            if (BetterStringUtils.isBlank(subject) == false) {
                sql.append(" AND sn.C_SUBJECT LIKE '%");
                sql.append(subject);
                sql.append("%' ");
            }
        });
        Optional<Object> GTEpublishDate = Optional.ofNullable(param.get("GTEpublishDate"));
        GTEpublishDate.ifPresent(data -> {
            final String publishDate = String.valueOf(data);
            if (BetterStringUtils.isBlank(publishDate) == false) {
                sql.append(" AND sn.D_PUBLISH_DATE >= ");
                sql.append(publishDate);
                sql.append(" ");
            }
        });
        Optional<Object> LTEpublishDate = Optional.ofNullable(param.get("LTEpublishDate"));
        LTEpublishDate.ifPresent(data -> {
            final String publishDate = String.valueOf(data);
            if (BetterStringUtils.isBlank(publishDate) == false) {
                sql.append(" AND sn.D_PUBLISH_DATE <= ");
                sql.append(publishDate);
                sql.append(" ");
            }
        });
        return sql.toString();
    }
}
