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
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

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
        final StringBuilder sql = new StringBuilder(
                "SELECT sn.ID AS id, sn.C_CHANNEL AS channel, sn.D_SENT_DATE AS sentDate, sn.T_SENT_TIME AS sentTime, sn.C_SUBJECT AS subject, sn.L_CUSTNO AS custNo, sn.C_CUSTNAME AS custName FROM t_sys_notifi sn, t_sys_notifi_cust snc "
                        + "WHERE sn.ID = snc.L_NOTIFICATION_ID AND snc.L_OPERID = #{operId} AND snc.C_IS_READ = #{isRead} AND snc.C_IS_DELETED = '0' AND sn.C_CHANNEL='0' ");
        final Map<String, Object> param = (Map<String, Object>) anParam.get("param");
        final Optional<Object> custNo = Optional.ofNullable(param.get("custNo"));
        custNo.ifPresent(data -> {
            final String tempCustNo = String.valueOf(data);
            if (StringUtils.isBlank(tempCustNo) == false) {
                sql.append(" AND snc.L_CUSTNO = #{param.custNo} ");
            }
        });
        final Optional<Object> LIKEsubject = Optional.ofNullable(param.get("LIKEsubject"));
        LIKEsubject.ifPresent(data -> {
            final String subject = String.valueOf(data);
            if (StringUtils.isBlank(subject) == false) {
                sql.append(" AND sn.C_SUBJECT LIKE #{param.LIKEsubject} ");
            }
        });
        final Optional<Object> GTEsentDate = Optional.ofNullable(param.get("GTEsentDate"));
        GTEsentDate.ifPresent(data -> {
            final String sentDate = String.valueOf(data);
            if (StringUtils.isBlank(sentDate) == false) {
                sql.append(" AND sn.D_SENT_DATE >= #{param.GTEsentDate} ");
            }
        });
        final Optional<Object> LTEsentDate = Optional.ofNullable(param.get("LTEsentDate"));
        LTEsentDate.ifPresent(data -> {
            final String sentDate = String.valueOf(data);
            if (StringUtils.isBlank(sentDate) == false) {
                sql.append(" AND sn.D_SENT_DATE <= #{param.LTEsentDate} ");
            }
        });
        sql.append(" ORDER BY sn.D_SENT_DATE DESC, sn.T_SENT_TIME DESC ");
        return sql.toString();
    }

    @SuppressWarnings("unchecked")
    public String selectNotificationProfileByCustsSql(final Map<String, Object> anParam) {
        final Set<String> customers = (Set<String>) anParam.get("customers");
        final String customer = customers.stream().map(String::valueOf).collect(Collectors.joining(","));
        final StringBuilder sql = new StringBuilder(
                "SELECT np.C_PROFILE_NAME AS profileName, np.C_PROFILE_RULE AS profileRule, c.L_CUSTNO AS custNo, c.C_CUSTNAME AS custName ");
        sql.append(" FROM t_sys_notifi_profile np ")
                .append(" INNER JOIN (SELECT * FROM t_custinfo c_1 WHERE c_1.L_CUSTNO IN (");
        sql.append(customer);
        sql.append(
                ")) c ON np.C_PROFILE_RULE IN (SELECT ccr.C_RULE FROM t_cust_certinfo_rule ccr WHERE ccr.C_SERIALNO IN (SELECT cc.C_SERIALNO FROM t_cust_certinfo cc WHERE  cc.C_OPERORG = c.C_OPERORG))");
        sql.append(" AND np.C_CUSTOM != '1' ");
        sql.append(" WHERE np.C_SUBSCRIBE_ENABLE = '0' AND ( ");
        sql.append(
                " EXISTS ( SELECT np_1.ID  FROM t_sys_notifi_profile np_1 WHERE np_1.C_PROFILE_NAME = np.C_PROFILE_NAME AND np_1.C_PROFILE_RULE = np.C_PROFILE_RULE AND np_1.L_CUSTNO = c.L_CUSTNO AND np_1.C_CUSTOM = '1' AND np_1.C_BUSIN_STATUS = '1' ");
        sql.append(
                " ) OR NOT EXISTS ( SELECT np_1.ID FROM t_sys_notifi_profile np_1 WHERE np_1.C_PROFILE_NAME = np.C_PROFILE_NAME AND np_1.C_PROFILE_RULE = np.C_PROFILE_RULE AND np_1.L_CUSTNO = c.L_CUSTNO AND np_1.C_CUSTOM = '1' ))");

        return sql.toString();
    }

}
