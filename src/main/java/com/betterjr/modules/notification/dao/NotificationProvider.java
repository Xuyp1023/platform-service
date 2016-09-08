// Copyright (c) 2014-2016 Betty. All rights reserved.
// ============================================================================
// CURRENT VERSION 
// ============================================================================
// CHANGE LOG
// V2.0 : 2016年9月7日, liuwl, creation
// ============================================================================
package com.betterjr.modules.notification.dao;

import java.util.Map;
import java.util.Optional;

import com.betterjr.common.utils.BetterStringUtils;

/**
 * @author liuwl
 *
 */
public class NotificationProvider {
    /**
     * 
     * @param param
     * @return
     */
    @SuppressWarnings("unchecked")
    public String selectNotificationByConditionSql(final Map<String, Object> anParam) {
        StringBuilder sql = new StringBuilder( "SELECT sn.* FROM t_sys_notifi sn, t_sys_notifi_cust snc "
                + "WHERE sn.ID = snc.L_NOTIFICATION_ID AND snc.L_OPERID = #{operId} AND snc.C_IS_READ = #{isRead} AND snc.C_IS_DELETED = '0' AND sn.C_CHANNEL='0' ");
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
        Optional<Object> GTEsentDate = Optional.ofNullable(param.get("GTEsentDate"));
        GTEsentDate.ifPresent(data -> {
            final String sentDate = String.valueOf(data);
            if (BetterStringUtils.isBlank(sentDate) == false) {
                sql.append(" AND sn.D_SENT_DATE >= ");
                sql.append(sentDate);
                sql.append(" ");
            }
        });
        Optional<Object> LTEsentDate = Optional.ofNullable(param.get("LTEsentDate"));
        LTEsentDate.ifPresent(data -> {
            final String sentDate = String.valueOf(data);
            if (BetterStringUtils.isBlank(sentDate) == false) {
                sql.append(" AND sn.D_SENT_DATE <= ");
                sql.append(sentDate);
                sql.append(" ");
            }
        });
        return sql.toString();
    }
}
