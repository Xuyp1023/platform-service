package com.betterjr.modules.notification.entity;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.betterjr.common.annotation.MetaData;
import com.betterjr.common.entity.BetterjrEntity;
import com.betterjr.common.selectkey.SerialGenerator;
import com.betterjr.common.utils.BetterDateUtils;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.notification.constants.NotificationConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Access(AccessType.FIELD)
@Entity
@Table(name = "t_sys_notifi_profile")
public class NotificationProfile implements BetterjrEntity {
    /**
     * 编号
     */
    @Id
    @Column(name = "ID", columnDefinition = "INTEGER")
    @MetaData(value = "编号", comments = "编号")
    private Long id;

    /**
     * 数据版本号
     */
    @JsonIgnore
    @Column(name = "N_VERSION", columnDefinition = "INTEGER")
    @MetaData(value = "数据版本号", comments = "数据版本号")
    private Long version;

    /**
     * 模板名称
     */
    @Column(name = "C_PROFILE_NAME", columnDefinition = "VARCHAR")
    @MetaData(value = "模板名称", comments = "模板名称")
    private String profileName;

    /**
     * 模板类型  PLATFORM_USER:平台,FACTOR_USER:保理公司,CORE_USER:核心企业,SUPPLIER_USER:供应商,SELLER_USER:经销商
     */
    @Column(name = "C_PROFILE_RULE", columnDefinition = "VARCHAR")
    @MetaData(value = "模板类型  0:平台,1:保理公司,2:核心企业,3:供应商,4:经销商", comments = "模板类型  0:平台,1:保理公司,2:核心企业,3:供应商,4:经销商")
    private String profileRule;

    /**
     * 是否允许订阅 ：0不允许订阅，1允许订阅
     */
    @Column(name = "C_SUBSCRIBE_ENABLE", columnDefinition = "CHAR")
    @MetaData(value = "是否允许订阅 ：0不允许订阅", comments = "是否允许订阅 ：0不允许订阅，1允许订阅")
    private String subscribeEnable;

    /**
     * 可订阅的角色范围,空代表所有关系客户
     */
    @Column(name = "C_SUBSCRIBE_RULE_LIST", columnDefinition = "CHAR")
    @MetaData(value = "可订阅的角色范围,空代表所有关系客户", comments = "可订阅的角色范围,空代表所有关系客户")
    private String subscribeRuleList;

    /**
     * 创建人(操作员)ID号
     */
    @JsonIgnore
    @Column(name = "L_REG_OPERID", columnDefinition = "INTEGER")
    @MetaData(value = "创建人(操作员)ID号", comments = "创建人(操作员)ID号")
    private Long regOperId;

    /**
     * 创建人(操作员)姓名
     */
    @JsonIgnore
    @Column(name = "C_REG_OPERNAME", columnDefinition = "VARCHAR")
    @MetaData(value = "创建人(操作员)姓名", comments = "创建人(操作员)姓名")
    private String regOperName;

    /**
     * 创建日期
     */
    @JsonIgnore
    @Column(name = "D_REG_DATE", columnDefinition = "VARCHAR")
    @MetaData(value = "创建日期", comments = "创建日期")
    private String regDate;

    /**
     * 创建时间
     */
    @JsonIgnore
    @Column(name = "T_REG_TIME", columnDefinition = "VARCHAR")
    @MetaData(value = "创建时间", comments = "创建时间")
    private String regTime;

    /**
     * 修改人(操作员)ID号
     */
    @JsonIgnore
    @Column(name = "L_MODI_OPERID", columnDefinition = "INTEGER")
    @MetaData(value = "修改人(操作员)ID号", comments = "修改人(操作员)ID号")
    private Long modiOperId;

    /**
     * 修改人(操作员)姓名
     */
    @JsonIgnore
    @Column(name = "C_MODI_OPERNAME", columnDefinition = "VARCHAR")
    @MetaData(value = "修改人(操作员)姓名", comments = "修改人(操作员)姓名")
    private String modiOperName;

    /**
     * 修改日期
     */
    @JsonIgnore
    @Column(name = "D_MODI_DATE", columnDefinition = "VARCHAR")
    @MetaData(value = "修改日期", comments = "修改日期")
    private String modiDate;

    /**
     * 修改时间
     */
    @JsonIgnore
    @Column(name = "T_MODI_TIME", columnDefinition = "VARCHAR")
    @MetaData(value = "修改时间", comments = "修改时间")
    private String modiTime;

    /**
     * 操作机构
     */
    @JsonIgnore
    @Column(name = "C_OPERORG", columnDefinition = "VARCHAR")
    @MetaData(value = "操作机构", comments = "操作机构")
    private String operOrg;

    @Column(name = "C_BUSIN_STATUS", columnDefinition = "CHAR")
    @MetaData(value = "", comments = "")
    private String businStatus;

    @JsonIgnore
    @Column(name = "C_LAST_STATUS", columnDefinition = "CHAR")
    @MetaData(value = "", comments = "")
    private String lastStatus;

    /**
     * 模板所属客户编号
     */
    @Column(name = "L_CUSTNO", columnDefinition = "INTEGER")
    @MetaData(value = "模板所属客户编号", comments = "模板所属客户编号")
    private Long custNo;

    /**
     * 模板所属客户名称
     */
    @Column(name = "C_CUSTNAME", columnDefinition = "VARCHAR")
    @MetaData(value = "模板所属客户名称", comments = "模板所属客户名称")
    private String custName;

    /**
     * 是否立即发送 0否 1是
     */
    @Column(name = "C_IMMEDIATE", columnDefinition = "CHAR")
    @MetaData(value = "是否立即发送", comments = "是否立即发送 0否 1是")
    private String immediate;

    /**
     * 是否属于用户自定义模板 0否 1是
     */
    @Column(name = "C_CUSTOM", columnDefinition = "CHAR")
    @MetaData(value = "是否属于用户自定义模板", comments = "是否属于用户自定义模板 0否 1是")
    private String custom;

    private static final long serialVersionUID = 1468812783881L;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(final Long version) {
        this.version = version;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(final String profileName) {
        this.profileName = profileName == null ? null : profileName.trim();
    }

    public String getProfileRule() {
        return profileRule;
    }

    public void setProfileRule(final String profileRule) {
        this.profileRule = profileRule == null ? null : profileRule.trim();
    }

    public String getSubscribeEnable() {
        return subscribeEnable;
    }

    public void setSubscribeEnable(final String subscribeEnable) {
        this.subscribeEnable = subscribeEnable == null ? null : subscribeEnable.trim();
    }

    public Long getRegOperId() {
        return regOperId;
    }

    public void setRegOperId(final Long regOperId) {
        this.regOperId = regOperId;
    }

    public String getRegOperName() {
        return regOperName;
    }

    public void setRegOperName(final String regOperName) {
        this.regOperName = regOperName == null ? null : regOperName.trim();
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(final String regDate) {
        this.regDate = regDate == null ? null : regDate.trim();
    }

    public String getRegTime() {
        return regTime;
    }

    public void setRegTime(final String regTime) {
        this.regTime = regTime == null ? null : regTime.trim();
    }

    public Long getModiOperId() {
        return modiOperId;
    }

    public void setModiOperId(final Long modiOperId) {
        this.modiOperId = modiOperId;
    }

    public String getModiOperName() {
        return modiOperName;
    }

    public void setModiOperName(final String modiOperName) {
        this.modiOperName = modiOperName == null ? null : modiOperName.trim();
    }

    public String getModiDate() {
        return modiDate;
    }

    public void setModiDate(final String modiDate) {
        this.modiDate = modiDate == null ? null : modiDate.trim();
    }

    public String getModiTime() {
        return modiTime;
    }

    public void setModiTime(final String modiTime) {
        this.modiTime = modiTime == null ? null : modiTime.trim();
    }

    public String getOperOrg() {
        return operOrg;
    }

    public void setOperOrg(final String operOrg) {
        this.operOrg = operOrg == null ? null : operOrg.trim();
    }

    public String getBusinStatus() {
        return businStatus;
    }

    public void setBusinStatus(final String businStatus) {
        this.businStatus = businStatus == null ? null : businStatus.trim();
    }

    public String getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(final String lastStatus) {
        this.lastStatus = lastStatus == null ? null : lastStatus.trim();
    }

    public Long getCustNo() {
        return custNo;
    }

    public void setCustNo(final Long custNo) {
        this.custNo = custNo;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(final String custName) {
        this.custName = custName == null ? null : custName.trim();
    }

    public String getSubscribeRuleList() {
        return subscribeRuleList;
    }

    public void setSubscribeRuleList(final String anSubscribeRuleList) {
        subscribeRuleList = anSubscribeRuleList;
    }

    public String getImmediate() {
        return immediate;
    }

    public void setImmediate(final String anImmediate) {
        immediate = anImmediate;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(final String anCustom) {
        custom = anCustom;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", version=").append(version);
        sb.append(", profileName=").append(profileName);
        sb.append(", profileRule=").append(profileRule);
        sb.append(", subscribeEnable=").append(subscribeEnable);
        sb.append(", subscribeRuleList=").append(subscribeRuleList);
        sb.append(", regOperId=").append(regOperId);
        sb.append(", regOperName=").append(regOperName);
        sb.append(", regDate=").append(regDate);
        sb.append(", regTime=").append(regTime);
        sb.append(", modiOperId=").append(modiOperId);
        sb.append(", modiOperName=").append(modiOperName);
        sb.append(", modiDate=").append(modiDate);
        sb.append(", modiTime=").append(modiTime);
        sb.append(", operOrg=").append(operOrg);
        sb.append(", businStatus=").append(businStatus);
        sb.append(", lastStatus=").append(lastStatus);
        sb.append(", custNo=").append(custNo);
        sb.append(", custName=").append(custName);
        sb.append(", immediate=").append(immediate);
        sb.append(", custom=").append(custom);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        final NotificationProfile other = (NotificationProfile) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getVersion() == null ? other.getVersion() == null
                        : this.getVersion().equals(other.getVersion()))
                && (this.getProfileName() == null ? other.getProfileName() == null
                        : this.getProfileName().equals(other.getProfileName()))
                && (this.getProfileRule() == null ? other.getProfileRule() == null
                        : this.getProfileRule().equals(other.getProfileRule()))
                && (this.getSubscribeEnable() == null ? other.getSubscribeEnable() == null
                        : this.getSubscribeEnable().equals(other.getSubscribeEnable()))
                && (this.getSubscribeRuleList() == null ? other.getSubscribeRuleList() == null
                        : this.getSubscribeRuleList().equals(other.getSubscribeRuleList()))
                && (this.getRegOperId() == null ? other.getRegOperId() == null
                        : this.getRegOperId().equals(other.getRegOperId()))
                && (this.getRegOperName() == null ? other.getRegOperName() == null
                        : this.getRegOperName().equals(other.getRegOperName()))
                && (this.getRegDate() == null ? other.getRegDate() == null
                        : this.getRegDate().equals(other.getRegDate()))
                && (this.getRegTime() == null ? other.getRegTime() == null
                        : this.getRegTime().equals(other.getRegTime()))
                && (this.getModiOperId() == null ? other.getModiOperId() == null
                        : this.getModiOperId().equals(other.getModiOperId()))
                && (this.getModiOperName() == null ? other.getModiOperName() == null
                        : this.getModiOperName().equals(other.getModiOperName()))
                && (this.getModiDate() == null ? other.getModiDate() == null
                        : this.getModiDate().equals(other.getModiDate()))
                && (this.getModiTime() == null ? other.getModiTime() == null
                        : this.getModiTime().equals(other.getModiTime()))
                && (this.getOperOrg() == null ? other.getOperOrg() == null
                        : this.getOperOrg().equals(other.getOperOrg()))
                && (this.getBusinStatus() == null ? other.getBusinStatus() == null
                        : this.getBusinStatus().equals(other.getBusinStatus()))
                && (this.getLastStatus() == null ? other.getLastStatus() == null
                        : this.getLastStatus().equals(other.getLastStatus()))
                && (this.getCustNo() == null ? other.getCustNo() == null : this.getCustNo().equals(other.getCustNo()))
                && (this.getImmediate() == null ? other.getImmediate() == null
                        : this.getImmediate().equals(other.getImmediate()))
                && (this.getCustom() == null ? other.getCustom() == null : this.getCustom().equals(other.getCustom()))
                && (this.getCustName() == null ? other.getCustName() == null
                        : this.getCustName().equals(other.getCustName()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        result = prime * result + ((getProfileName() == null) ? 0 : getProfileName().hashCode());
        result = prime * result + ((getProfileRule() == null) ? 0 : getProfileRule().hashCode());
        result = prime * result + ((getSubscribeEnable() == null) ? 0 : getSubscribeEnable().hashCode());
        result = prime * result + ((getSubscribeRuleList() == null) ? 0 : getSubscribeRuleList().hashCode());
        result = prime * result + ((getRegOperId() == null) ? 0 : getRegOperId().hashCode());
        result = prime * result + ((getRegOperName() == null) ? 0 : getRegOperName().hashCode());
        result = prime * result + ((getRegDate() == null) ? 0 : getRegDate().hashCode());
        result = prime * result + ((getRegTime() == null) ? 0 : getRegTime().hashCode());
        result = prime * result + ((getModiOperId() == null) ? 0 : getModiOperId().hashCode());
        result = prime * result + ((getModiOperName() == null) ? 0 : getModiOperName().hashCode());
        result = prime * result + ((getModiDate() == null) ? 0 : getModiDate().hashCode());
        result = prime * result + ((getModiTime() == null) ? 0 : getModiTime().hashCode());
        result = prime * result + ((getOperOrg() == null) ? 0 : getOperOrg().hashCode());
        result = prime * result + ((getBusinStatus() == null) ? 0 : getBusinStatus().hashCode());
        result = prime * result + ((getLastStatus() == null) ? 0 : getLastStatus().hashCode());
        result = prime * result + ((getCustNo() == null) ? 0 : getCustNo().hashCode());
        result = prime * result + ((getImmediate() == null) ? 0 : getImmediate().hashCode());
        result = prime * result + ((getCustom() == null) ? 0 : getCustom().hashCode());
        result = prime * result + ((getCustName() == null) ? 0 : getCustName().hashCode());
        return result;
    }

    public void initModifyValue(final String anBusinStatus) {
        this.modiOperId = UserUtils.getOperatorInfo().getId();
        this.modiOperName = UserUtils.getOperatorInfo().getName();
        this.modiDate = BetterDateUtils.getNumDate();
        this.modiTime = BetterDateUtils.getNumTime();

        this.businStatus = anBusinStatus;
    }

    public void initAddValue(final NotificationProfile anNotificationProfile, final CustInfo anCustInfo,
            final CustOperatorInfo anOperator) {
        this.id = SerialGenerator.getLongValue("NotificationProfile.id");

        this.regOperId = anOperator.getId();
        this.regOperName = anOperator.getName();
        this.operOrg = anOperator.getOperOrg();

        this.regDate = BetterDateUtils.getNumDate();
        this.regTime = BetterDateUtils.getNumTime();

        this.modiOperId = anOperator.getId();
        this.modiOperName = anOperator.getName();
        this.modiDate = BetterDateUtils.getNumDate();
        this.modiTime = BetterDateUtils.getNumTime();

        this.subscribeEnable = anNotificationProfile.getSubscribeEnable();
        this.subscribeRuleList = anNotificationProfile.getSubscribeRuleList();
        this.profileName = anNotificationProfile.getProfileName();
        this.profileRule = anNotificationProfile.getProfileRule();
        this.immediate = anNotificationProfile.getImmediate();

        this.custNo = anCustInfo.getCustNo();
        this.custName = anCustInfo.getCustName();

        this.businStatus = anNotificationProfile.getBusinStatus();

        this.custom = NotificationConstants.PROFILE_CUSTOM; // 用户自定义
    }
}