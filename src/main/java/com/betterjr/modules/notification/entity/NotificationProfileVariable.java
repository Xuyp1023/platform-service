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
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Access(AccessType.FIELD)
@Entity
@Table(name = "t_sys_notifi_profile_var")
public class NotificationProfileVariable implements BetterjrEntity {
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
     * 模板编号
     */
    @JsonIgnore
    @Column(name = "L_CHANNEL_PROFILE_ID", columnDefinition = "INTEGER")
    @MetaData(value = "模板编号", comments = "模板编号")
    private Long channelProfileId;

    /**
     * 变量名称
     */
    @Column(name = "C_VAR_NAME", columnDefinition = "VARCHAR")
    @MetaData(value = "变量名称", comments = "变量名称")
    private String variableName;

    /**
     * 变量值 eg.: ${entity.custName}  - #公司名称# - 公司名称      ->    variableValue - variableName - variableMark
     */
    @Column(name = "C_VAR_VALUE", columnDefinition = "VARCHAR")
    @MetaData(value = "变量值", comments = "变量值")
    private String variableValue;

    /**
     * 变量描述
     */
    @Column(name = "C_VAR_MARK", columnDefinition = "VARCHAR")
    @MetaData(value = "变量描述", comments = "变量描述")
    private String variableMark;

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

    @JsonIgnore
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
    @JsonIgnore
    @Column(name = "L_CUSTNO", columnDefinition = "INTEGER")
    @MetaData(value = "模板所属客户编号", comments = "模板所属客户编号")
    private Long custNo;

    /**
     * 模板所属客户名称
     */
    @JsonIgnore
    @Column(name = "C_CUSTNAME", columnDefinition = "VARCHAR")
    @MetaData(value = "模板所属客户名称", comments = "模板所属客户名称")
    private String custName;

    private static final long serialVersionUID = 1468812783881L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getChannelProfileId() {
        return channelProfileId;
    }

    public void setChannelProfileId(Long anChannelProfileId) {
        channelProfileId = anChannelProfileId;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String anVariableName) {
        variableName = anVariableName;
    }

    public String getVariableValue() {
        return variableValue;
    }

    public void setVariableValue(String anVariableValue) {
        variableValue = anVariableValue;
    }

    public String getVariableMark() {
        return variableMark;
    }

    public void setVariableMark(String anVariableMark) {
        variableMark = anVariableMark;
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

    public String getOperOrg() {
        return operOrg;
    }

    public void setOperOrg(String operOrg) {
        this.operOrg = operOrg == null ? null : operOrg.trim();
    }

    public String getBusinStatus() {
        return businStatus;
    }

    public void setBusinStatus(String businStatus) {
        this.businStatus = businStatus == null ? null : businStatus.trim();
    }

    public String getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(String lastStatus) {
        this.lastStatus = lastStatus == null ? null : lastStatus.trim();
    }

    public Long getCustNo() {
        return custNo;
    }

    public void setCustNo(Long custNo) {
        this.custNo = custNo;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName == null ? null : custName.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", version=").append(version);
        sb.append(", channelProfileId=").append(channelProfileId);
        sb.append(", variableName=").append(variableName);
        sb.append(", variableValue=").append(variableValue);
        sb.append(", variableMark=").append(variableMark);
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
        NotificationProfileVariable other = (NotificationProfileVariable) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getVersion() == null ? other.getVersion() == null
                        : this.getVersion().equals(other.getVersion()))
                && (this.getChannelProfileId() == null ? other.getChannelProfileId() == null
                        : this.getChannelProfileId().equals(other.getChannelProfileId()))
                && (this.getVariableName() == null ? other.getVariableName() == null
                        : this.getVariableName().equals(other.getVariableName()))
                && (this.getVariableValue() == null ? other.getVariableValue() == null
                        : this.getVariableValue().equals(other.getVariableValue()))
                && (this.getVariableMark() == null ? other.getVariableMark() == null
                        : this.getVariableMark().equals(other.getVariableMark()))
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
                && (this.getCustName() == null ? other.getCustName() == null
                        : this.getCustName().equals(other.getCustName()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        result = prime * result + ((getChannelProfileId() == null) ? 0 : getChannelProfileId().hashCode());
        result = prime * result + ((getVariableName() == null) ? 0 : getVariableName().hashCode());
        result = prime * result + ((getVariableValue() == null) ? 0 : getVariableValue().hashCode());
        result = prime * result + ((getVariableMark() == null) ? 0 : getVariableMark().hashCode());
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
        result = prime * result + ((getCustName() == null) ? 0 : getCustName().hashCode());
        return result;
    }

    public void initAddValue(Long anChannelProfileId, NotificationProfileVariable anProfileVariable,
            CustInfo anCustInfo, CustOperatorInfo anOperator) {
        this.id = SerialGenerator.getLongValue("NotificationProfileVariable.id");

        this.regOperId = anOperator.getId();
        this.regOperName = anOperator.getName();
        this.operOrg = anOperator.getOperOrg();

        this.regDate = BetterDateUtils.getNumDate();
        this.regTime = BetterDateUtils.getNumTime();

        this.modiOperId = anOperator.getId();
        this.modiOperName = anOperator.getName();
        this.modiDate = BetterDateUtils.getNumDate();
        this.modiTime = BetterDateUtils.getNumTime();

        this.channelProfileId = anChannelProfileId;
        this.custNo = anCustInfo.getCustNo();

        this.variableName = anProfileVariable.getVariableName();
        this.variableValue = anProfileVariable.getVariableValue();
        this.variableMark = anProfileVariable.getVariableMark();
    }
}