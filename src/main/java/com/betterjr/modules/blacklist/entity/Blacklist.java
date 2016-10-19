package com.betterjr.modules.blacklist.entity;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.betterjr.common.annotation.MetaData;
import com.betterjr.common.entity.BetterjrEntity;
import com.betterjr.common.mapper.CustDateJsonSerializer;
import com.betterjr.common.selectkey.SerialGenerator;
import com.betterjr.common.utils.BetterDateUtils;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.modules.blacklist.constant.BlacklistConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Access(AccessType.FIELD)
@Entity
@Table(name = "T_SCF_BLACKLIST")
public class Blacklist implements BetterjrEntity {
    /**
     * 流水号
     */
    @Id
    @Column(name = "ID", columnDefinition = "INTEGER")
    @MetaData(value = "流水号", comments = "流水号")
    private Long id;

    /**
     * 类型(0:个人;1:机构;)
     */
    @Column(name = "C_CUSTTYPE", columnDefinition = "VARCHAR")
    @MetaData(value = "类型(0:个人;1:机构;)", comments = "类型(0:个人;1:机构;)")
    private String custType;

    /**
     * 被执行人姓名/名称
     */
    @Column(name = "C_NAME", columnDefinition = "VARCHAR")
    @MetaData(value = "被执行人姓名/名称", comments = "被执行人姓名/名称")
    private String name;

    /**
     * 证件号码:身份证号码/组织机构代码
     */
    @Column(name = "C_IDENTNO", columnDefinition = "VARCHAR")
    @MetaData(value = "证件号码:身份证号码/组织机构代码", comments = "证件号码:身份证号码/组织机构代码")
    private String identNo;

    /**
     * 法定代表人/负责人姓名
     */
    @Column(name = "C_LAWNAME", columnDefinition = "VARCHAR")
    @MetaData(value = "法定代表人/负责人姓名", comments = "法定代表人/负责人姓名")
    private String lawName;

    /**
     * 失信行为
     */
    @Column(name = "C_DISBEHAVIOR", columnDefinition = "VARCHAR")
    @MetaData(value = "失信行为", comments = "失信行为")
    private String disBehavior;

    /**
     * 黑名单录入操作员ID号
     */
    @Column(name = "L_REG_OPERID", columnDefinition = "INTEGER")
    @MetaData(value = "黑名单录入操作员ID号", comments = "黑名单录入操作员ID号")
    private Long regOperId;

    /**
     * 黑名单录入操作员姓名
     */
    @Column(name = "C_REG_OPERNAME", columnDefinition = "VARCHAR")
    @MetaData(value = "黑名单录入操作员姓名", comments = "黑名单录入操作员姓名")
    private String regOperName;

    /**
     * 黑名单录入日期
     */
    @JsonSerialize(using = CustDateJsonSerializer.class)
    @Column(name = "D_REG_DATE", columnDefinition = "VARCHAR")
    @MetaData(value = "黑名单录入日期", comments = "黑名单录入日期")
    private String regDate;

    /**
     * 黑名单录入时间
     */
    @Column(name = "T_REG_TIME", columnDefinition = "VARCHAR")
    @MetaData(value = "黑名单录入时间", comments = "黑名单录入时间")
    private String regTime;

    /**
     * 黑名单修改操作员ID号
     */
    @Column(name = "L_MODI_OPERID", columnDefinition = "INTEGER")
    @MetaData(value = "黑名单修改操作员ID号", comments = "黑名单修改操作员ID号")
    private Long modiOperId;

    /**
     * 黑名单修改操作员姓名
     */
    @Column(name = "C_MODI_OPERNAME", columnDefinition = "VARCHAR")
    @MetaData(value = "黑名单修改操作员姓名", comments = "黑名单修改操作员姓名")
    private String modiOperName;

    /**
     * 黑名单修改日期
     */
    @JsonSerialize(using = CustDateJsonSerializer.class)
    @Column(name = "D_MODI_DATE", columnDefinition = "VARCHAR")
    @MetaData(value = "黑名单修改日期", comments = "黑名单修改日期")
    private String modiDate;

    /**
     * 黑名单修改时间
     */
    @Column(name = "T_MODI_TIME", columnDefinition = "VARCHAR")
    @MetaData(value = "黑名单修改时间", comments = "黑名单修改时间")
    private String modiTime;

    /**
     * 黑名单激活操作员ID号
     */
    @Column(name = "L_ACTIVATE_OPERID", columnDefinition = "INTEGER")
    @MetaData(value = "黑名单激活操作员ID号", comments = "黑名单激活操作员ID号")
    private Long activateOperId;

    /**
     * 黑名单激活操作员姓名
     */
    @Column(name = "C_ACTIVATE_OPERNAME", columnDefinition = "VARCHAR")
    @MetaData(value = "黑名单激活操作员姓名", comments = "黑名单激活操作员姓名")
    private String activateOperName;

    /**
     * 黑名单激活日期
     */
    @JsonSerialize(using = CustDateJsonSerializer.class)
    @Column(name = "D_ACTIVATE_DATE", columnDefinition = "VARCHAR")
    @MetaData(value = "黑名单激活日期", comments = "黑名单激活日期")
    private String activateDate;

    /**
     * 黑名单激活时间
     */
    @Column(name = "T_ACTIVATE_TIME", columnDefinition = "VARCHAR")
    @MetaData(value = "黑名单激活时间", comments = "黑名单激活时间")
    private String activateTime;

    /**
     * 黑名单注销操作员ID号
     */
    @Column(name = "L_CANCEL_OPERID", columnDefinition = "INTEGER")
    @MetaData(value = "黑名单注销操作员ID号", comments = "黑名单注销操作员ID号")
    private Long cancelOperId;

    /**
     * 黑名单注销操作员姓名
     */
    @Column(name = "C_CANCEL_OPERNAME", columnDefinition = "VARCHAR")
    @MetaData(value = "黑名单注销操作员姓名", comments = "黑名单注销操作员姓名")
    private String cancelOperName;

    /**
     * 黑名单注销日期
     */
    @JsonSerialize(using = CustDateJsonSerializer.class)
    @Column(name = "D_CANCEL_DATE", columnDefinition = "VARCHAR")
    @MetaData(value = "黑名单注销日期", comments = "黑名单注销日期")
    private String cancelDate;

    /**
     * 黑名单注销时间
     */
    @Column(name = "T_CANCEL_TIME", columnDefinition = "VARCHAR")
    @MetaData(value = "黑名单注销时间", comments = "黑名单注销时间")
    private String cancelTime;

    /**
     * 状态(0:未生效;1:已生效;)
     */
    @Column(name = "C_BUSIN_STATUS", columnDefinition = "VARCHAR")
    @MetaData(value = "状态(0:未生效;1:已生效;)", comments = "状态(0:未生效;1:已生效;)")
    private String businStatus;

    /**
     * 操作机构
     */
    @JsonIgnore
    @Column(name = "C_OPERORG", columnDefinition = "VARCHAR")
    @MetaData(value = "操作机构", comments = "操作机构")
    private String operOrg;

    /**
     * 客户编号
     */
    @Column(name = "L_CUSTNO", columnDefinition = "INTEGER")
    @MetaData(value = "客户编号", comments = "客户编号")
    private Long custNo;

    private static final long serialVersionUID = 1467703726401L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustType() {
        return custType;
    }

    public void setCustType(String custType) {
        this.custType = custType == null ? null : custType.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getIdentNo() {
        return identNo;
    }

    public void setIdentNo(String identNo) {
        this.identNo = identNo == null ? null : identNo.trim();
    }

    public String getLawName() {
        return lawName;
    }

    public void setLawName(String lawName) {
        this.lawName = lawName == null ? null : lawName.trim();
    }

    public String getDisBehavior() {
        return disBehavior;
    }

    public void setDisBehavior(String disBehavior) {
        this.disBehavior = disBehavior == null ? null : disBehavior.trim();
    }

    public Long getRegOperId() {
        return regOperId;
    }

    public void setRegOperId(Long regOperId) {
        this.regOperId = regOperId;
    }

    public String getRegOperName() {
        return regOperName;
    }

    public void setRegOperName(String regOperName) {
        this.regOperName = regOperName == null ? null : regOperName.trim();
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate == null ? null : regDate.trim();
    }

    public String getRegTime() {
        return regTime;
    }

    public void setRegTime(String regTime) {
        this.regTime = regTime == null ? null : regTime.trim();
    }

    public Long getModiOperId() {
        return modiOperId;
    }

    public void setModiOperId(Long modiOperId) {
        this.modiOperId = modiOperId;
    }

    public String getModiOperName() {
        return modiOperName;
    }

    public void setModiOperName(String modiOperName) {
        this.modiOperName = modiOperName == null ? null : modiOperName.trim();
    }

    public String getModiDate() {
        return modiDate;
    }

    public void setModiDate(String modiDate) {
        this.modiDate = modiDate == null ? null : modiDate.trim();
    }

    public String getModiTime() {
        return modiTime;
    }

    public void setModiTime(String modiTime) {
        this.modiTime = modiTime == null ? null : modiTime.trim();
    }

    public Long getActivateOperId() {
        return activateOperId;
    }

    public void setActivateOperId(Long activateOperId) {
        this.activateOperId = activateOperId;
    }

    public String getActivateOperName() {
        return activateOperName;
    }

    public void setActivateOperName(String activateOperName) {
        this.activateOperName = activateOperName == null ? null : activateOperName.trim();
    }

    public String getActivateDate() {
        return activateDate;
    }

    public void setActivateDate(String activateDate) {
        this.activateDate = activateDate == null ? null : activateDate.trim();
    }

    public String getActivateTime() {
        return activateTime;
    }

    public void setActivateTime(String activateTime) {
        this.activateTime = activateTime == null ? null : activateTime.trim();
    }

    public Long getCancelOperId() {
        return cancelOperId;
    }

    public void setCancelOperId(Long cancelOperId) {
        this.cancelOperId = cancelOperId;
    }

    public String getCancelOperName() {
        return cancelOperName;
    }

    public void setCancelOperName(String cancelOperName) {
        this.cancelOperName = cancelOperName == null ? null : cancelOperName.trim();
    }

    public String getCancelDate() {
        return cancelDate;
    }

    public void setCancelDate(String cancelDate) {
        this.cancelDate = cancelDate == null ? null : cancelDate.trim();
    }

    public String getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(String cancelTime) {
        this.cancelTime = cancelTime == null ? null : cancelTime.trim();
    }

    public String getBusinStatus() {
        return businStatus;
    }

    public void setBusinStatus(String businStatus) {
        this.businStatus = businStatus == null ? null : businStatus.trim();
    }

    public String getOperOrg() {
        return operOrg;
    }

    public void setOperOrg(String operOrg) {
        this.operOrg = operOrg == null ? null : operOrg.trim();
    }

    public Long getCustNo() {
        return custNo;
    }

    public void setCustNo(Long custNo) {
        this.custNo = custNo;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", type=").append(custType);
        sb.append(", name=").append(name);
        sb.append(", identNo=").append(identNo);
        sb.append(", lawName=").append(lawName);
        sb.append(", disBehavior=").append(disBehavior);
        sb.append(", regOperId=").append(regOperId);
        sb.append(", regOperName=").append(regOperName);
        sb.append(", regDate=").append(regDate);
        sb.append(", regTime=").append(regTime);
        sb.append(", modiOperId=").append(modiOperId);
        sb.append(", modiOperName=").append(modiOperName);
        sb.append(", modiDate=").append(modiDate);
        sb.append(", modiTime=").append(modiTime);
        sb.append(", activateOperId=").append(activateOperId);
        sb.append(", activateOperName=").append(activateOperName);
        sb.append(", activateDate=").append(activateDate);
        sb.append(", activateTime=").append(activateTime);
        sb.append(", cancelOperId=").append(cancelOperId);
        sb.append(", cancelOperName=").append(cancelOperName);
        sb.append(", cancelDate=").append(cancelDate);
        sb.append(", cancelTime=").append(cancelTime);
        sb.append(", businStatus=").append(businStatus);
        sb.append(", operOrg=").append(operOrg);
        sb.append(", custNo=").append(custNo);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Blacklist other = (Blacklist) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getCustType() == null ? other.getCustType() == null : this.getCustType().equals(other.getCustType()))
                && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
                && (this.getIdentNo() == null ? other.getIdentNo() == null : this.getIdentNo().equals(other.getIdentNo()))
                && (this.getLawName() == null ? other.getLawName() == null : this.getLawName().equals(other.getLawName()))
                && (this.getDisBehavior() == null ? other.getDisBehavior() == null : this.getDisBehavior().equals(other.getDisBehavior()))
                && (this.getRegOperId() == null ? other.getRegOperId() == null : this.getRegOperId().equals(other.getRegOperId()))
                && (this.getRegOperName() == null ? other.getRegOperName() == null : this.getRegOperName().equals(other.getRegOperName()))
                && (this.getRegDate() == null ? other.getRegDate() == null : this.getRegDate().equals(other.getRegDate()))
                && (this.getRegTime() == null ? other.getRegTime() == null : this.getRegTime().equals(other.getRegTime()))
                && (this.getModiOperId() == null ? other.getModiOperId() == null : this.getModiOperId().equals(other.getModiOperId()))
                && (this.getModiOperName() == null ? other.getModiOperName() == null : this.getModiOperName().equals(other.getModiOperName()))
                && (this.getModiDate() == null ? other.getModiDate() == null : this.getModiDate().equals(other.getModiDate()))
                && (this.getModiTime() == null ? other.getModiTime() == null : this.getModiTime().equals(other.getModiTime()))
                && (this.getActivateOperId() == null ? other.getActivateOperId() == null : this.getActivateOperId().equals(other.getActivateOperId()))
                && (this.getActivateOperName() == null ? other.getActivateOperName() == null
                        : this.getActivateOperName().equals(other.getActivateOperName()))
                && (this.getActivateDate() == null ? other.getActivateDate() == null : this.getActivateDate().equals(other.getActivateDate()))
                && (this.getActivateTime() == null ? other.getActivateTime() == null : this.getActivateTime().equals(other.getActivateTime()))
                && (this.getCancelOperId() == null ? other.getCancelOperId() == null : this.getCancelOperId().equals(other.getCancelOperId()))
                && (this.getCancelOperName() == null ? other.getCancelOperName() == null : this.getCancelOperName().equals(other.getCancelOperName()))
                && (this.getCancelDate() == null ? other.getCancelDate() == null : this.getCancelDate().equals(other.getCancelDate()))
                && (this.getCancelTime() == null ? other.getCancelTime() == null : this.getCancelTime().equals(other.getCancelTime()))
                && (this.getBusinStatus() == null ? other.getBusinStatus() == null : this.getBusinStatus().equals(other.getBusinStatus()))
                && (this.getOperOrg() == null ? other.getOperOrg() == null : this.getOperOrg().equals(other.getOperOrg()))
                && (this.getCustNo() == null ? other.getCustNo() == null : this.getCustNo().equals(other.getCustNo()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getCustType() == null) ? 0 : getCustType().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getIdentNo() == null) ? 0 : getIdentNo().hashCode());
        result = prime * result + ((getLawName() == null) ? 0 : getLawName().hashCode());
        result = prime * result + ((getDisBehavior() == null) ? 0 : getDisBehavior().hashCode());
        result = prime * result + ((getRegOperId() == null) ? 0 : getRegOperId().hashCode());
        result = prime * result + ((getRegOperName() == null) ? 0 : getRegOperName().hashCode());
        result = prime * result + ((getRegDate() == null) ? 0 : getRegDate().hashCode());
        result = prime * result + ((getRegTime() == null) ? 0 : getRegTime().hashCode());
        result = prime * result + ((getModiOperId() == null) ? 0 : getModiOperId().hashCode());
        result = prime * result + ((getModiOperName() == null) ? 0 : getModiOperName().hashCode());
        result = prime * result + ((getModiDate() == null) ? 0 : getModiDate().hashCode());
        result = prime * result + ((getModiTime() == null) ? 0 : getModiTime().hashCode());
        result = prime * result + ((getActivateOperId() == null) ? 0 : getActivateOperId().hashCode());
        result = prime * result + ((getActivateOperName() == null) ? 0 : getActivateOperName().hashCode());
        result = prime * result + ((getActivateDate() == null) ? 0 : getActivateDate().hashCode());
        result = prime * result + ((getActivateTime() == null) ? 0 : getActivateTime().hashCode());
        result = prime * result + ((getCancelOperId() == null) ? 0 : getCancelOperId().hashCode());
        result = prime * result + ((getCancelOperName() == null) ? 0 : getCancelOperName().hashCode());
        result = prime * result + ((getCancelDate() == null) ? 0 : getCancelDate().hashCode());
        result = prime * result + ((getCancelTime() == null) ? 0 : getCancelTime().hashCode());
        result = prime * result + ((getBusinStatus() == null) ? 0 : getBusinStatus().hashCode());
        result = prime * result + ((getOperOrg() == null) ? 0 : getOperOrg().hashCode());
        result = prime * result + ((getCustNo() == null) ? 0 : getCustNo().hashCode());
        return result;
    }

    public void initAddValue() {
        this.id = SerialGenerator.getLongValue("ScfBlacklist.id");
        this.regOperId = UserUtils.getOperatorInfo().getId();
        this.regOperName = UserUtils.getOperatorInfo().getName();
        this.regDate = BetterDateUtils.getNumDate();
        this.regTime = BetterDateUtils.getNumTime();
        this.operOrg = UserUtils.getOperatorInfo().getOperOrg();
        // 设置黑名单注销状态(businStatus:0)
        this.businStatus = BlacklistConstants.BLACKLIST_STATUS_INEFFECTIVE;
    }

    public void initActivateValue() {
        this.activateOperId = UserUtils.getOperatorInfo().getId();
        this.activateOperName = UserUtils.getOperatorInfo().getName();
        this.activateDate = BetterDateUtils.getNumDate();
        this.activateTime = BetterDateUtils.getNumTime();
        // 设置黑名单激活状态(businStatus:1)
        this.businStatus = BlacklistConstants.BLACKLIST_STATUS_EFFECTIVE;
    }

    public void initCancelValue() {
        this.cancelOperId = UserUtils.getOperatorInfo().getId();
        this.cancelOperName = UserUtils.getOperatorInfo().getName();
        this.cancelDate = BetterDateUtils.getNumDate();
        this.cancelTime = BetterDateUtils.getNumTime();
        // 设置黑名单注销状态(businStatus:0)
        this.businStatus = BlacklistConstants.BLACKLIST_STATUS_INEFFECTIVE;
    }

    public void initModifyValue(Blacklist request) {
        this.id = request.getId();
        this.businStatus = request.getBusinStatus();
        this.operOrg = request.getOperOrg();
        this.custNo = request.getCustNo();

        this.regOperId = request.getRegOperId();
        this.regOperName = request.getRegOperName();
        this.regDate = request.getRegDate();
        this.regTime = request.getRegTime();

        this.modiOperId = UserUtils.getOperatorInfo().getId();
        this.modiOperName = UserUtils.getOperatorInfo().getName();
        this.modiDate = BetterDateUtils.getNumDate();
        this.modiTime = BetterDateUtils.getNumTime();
    }

    public void initLawName(String anCustType) {
        // 是否个人黑名单:custType,0-个人,1-机构
        if (BetterStringUtils.equals(anCustType, BlacklistConstants.BLACKLIST_TYPE_PERSONAL) == true) {
            this.lawName = " ";
        }
    }

}