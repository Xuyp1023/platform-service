package com.betterjr.modules.customer.entity;

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
import com.betterjr.modules.customer.data.ICustAuditEntityFace;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Access(AccessType.FIELD)
@Entity
@Table(name = "t_cust_mech_contacter_tmp")
public class CustMechContacterTmp implements BetterjrEntity, ICustAuditEntityFace {
    /**
     * 编号
     */
    @Id
    @Column(name = "ID", columnDefinition = "INTEGER")
    @MetaData(value = "编号", comments = "编号")
    private Long id;

    /**
     * 代录记录/变更申请 编号
     */
    @Column(name = "L_PARENTID", columnDefinition = "INTEGER")
    @MetaData(value = "代录记录/变更申请 编号", comments = "代录记录/变更申请 编号")
    private Long parentId;

    /**
     * 数据版本号
     */
    @JsonIgnore
    @Column(name = "N_VERSION", columnDefinition = "INTEGER")
    @MetaData(value = "数据版本号", comments = "数据版本号")
    private Long version;

    /**
     * 姓名
     */
    @Column(name = "C_NAME", columnDefinition = "VARCHAR")
    @MetaData(value = "姓名", comments = "姓名")
    private String name;

    /**
     * 性别 0女 1男
     */
    @Column(name = "C_SEX", columnDefinition = "CHAR")
    @MetaData(value = "性别 0女 1男", comments = "性别 0女 1男")
    private String sex;

    /**
     * 移动电话
     */
    @Column(name = "C_MOBILE", columnDefinition = "VARCHAR")
    @MetaData(value = "移动电话", comments = "移动电话")
    private String mobile;

    /**
     * 电话
     */
    @Column(name = "C_PHONE", columnDefinition = "VARCHAR")
    @MetaData(value = "电话", comments = "电话")
    private String phone;

    /**
     * 电子邮件
     */
    @Column(name = "C_EMAIL", columnDefinition = "VARCHAR")
    @MetaData(value = "电子邮件", comments = "电子邮件")
    private String email;

    /**
     * 地址
     */
    @Column(name = "C_ADDRESS", columnDefinition = "VARCHAR")
    @MetaData(value = "地址", comments = "地址")
    private String address;

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

    /**
     * 使用状态:0未使用 1使用中 2已使用
     */
    @Column(name = "C_BUSIN_STATUS", columnDefinition = "CHAR")
    @MetaData(value = "使用状态:0未使用  1使用中  2已使用", comments = "使用状态:0未使用  1使用中  2已使用")
    private String businStatus;

    @JsonIgnore
    @Column(name = "C_LAST_STATUS", columnDefinition = "CHAR")
    @MetaData(value = "", comments = "")
    private String lastStatus;

    /**
     * 客户编号
     */
    @Column(name = "L_CUSTNO", columnDefinition = "INTEGER")
    @MetaData(value = "客户编号", comments = "客户编号")
    private Long custNo;

    /**
     * 附件
     */
    @Column(name = "N_BATCHNO", columnDefinition = "INTEGER")
    @MetaData(value = "附件", comments = "附件")
    private Long batchNo;

    /**
     * 引用编号
     */
    @Column(name = "L_REF_ID", columnDefinition = "INTEGER")
    @MetaData(value = "引用编号", comments = "引用编号")
    private Long refId;

    /**
     * 流水类型:0 代录 1 变更 2 暂存
     */
    @Column(name = "C_TMP_TYPE", columnDefinition = "CHAR")
    @MetaData(value = "流水类型:0 代录  1 变更  2 暂存", comments = "流水类型:0 代录  1 变更  2 暂存")
    private String tmpType;

    /**
     * 流水操作类型:0 新增 1 修改 2 删除
     */
    @Column(name = "C_TMP_OPER_TYPE", columnDefinition = "CHAR")
    @MetaData(value = "流水操作类型:0 新增  1 修改  2 删除", comments = "流水操作类型:0 新增  1 修改  2 删除")
    private String tmpOperType;

    private static final long serialVersionUID = 1468812783862L;

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

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getSex() {
        return sex;
    }

    public void setSex(final String sex) {
        this.sex = sex == null ? null : sex.trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(final String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address == null ? null : address.trim();
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

    @Override
    public Long getCustNo() {
        return custNo;
    }

    public void setCustNo(final Long custNo) {
        this.custNo = custNo;
    }

    @Override
    public Long getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(final Long anBatchNo) {
        batchNo = anBatchNo;
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(final Long refId) {
        this.refId = refId;
    }

    public String getTmpType() {
        return tmpType;
    }

    public void setTmpType(final String tmpType) {
        this.tmpType = tmpType == null ? null : tmpType.trim();
    }

    public String getTmpOperType() {
        return tmpOperType;
    }

    public void setTmpOperType(final String tmpOperType) {
        this.tmpOperType = tmpOperType == null ? null : tmpOperType.trim();
    }

    @Override
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(final Long anParentId) {
        parentId = anParentId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", parentId=").append(parentId);
        sb.append(", version=").append(version);
        sb.append(", name=").append(name);
        sb.append(", sex=").append(sex);
        sb.append(", mobile=").append(mobile);
        sb.append(", phone=").append(phone);
        sb.append(", email=").append(email);
        sb.append(", address=").append(address);
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
        sb.append(", batchNo=").append(batchNo);
        sb.append(", refId=").append(refId);
        sb.append(", tmpType=").append(tmpType);
        sb.append(", tmpOperType=").append(tmpOperType);
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
        final CustMechContacterTmp other = (CustMechContacterTmp) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getParentId() == null ? other.getParentId() == null
                        : this.getParentId().equals(other.getParentId()))
                && (this.getVersion() == null ? other.getVersion() == null
                        : this.getVersion().equals(other.getVersion()))
                && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
                && (this.getSex() == null ? other.getSex() == null : this.getSex().equals(other.getSex()))
                && (this.getMobile() == null ? other.getMobile() == null : this.getMobile().equals(other.getMobile()))
                && (this.getPhone() == null ? other.getPhone() == null : this.getPhone().equals(other.getPhone()))
                && (this.getEmail() == null ? other.getEmail() == null : this.getEmail().equals(other.getEmail()))
                && (this.getAddress() == null ? other.getAddress() == null
                        : this.getAddress().equals(other.getAddress()))
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
                && (this.getBatchNo() == null ? other.getBatchNo() == null
                        : this.getBatchNo().equals(other.getBatchNo()))
                && (this.getRefId() == null ? other.getRefId() == null : this.getRefId().equals(other.getRefId()))
                && (this.getTmpType() == null ? other.getTmpType() == null
                        : this.getTmpType().equals(other.getTmpType()))
                && (this.getTmpOperType() == null ? other.getTmpOperType() == null
                        : this.getTmpOperType().equals(other.getTmpOperType()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getParentId() == null) ? 0 : getParentId().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getSex() == null) ? 0 : getSex().hashCode());
        result = prime * result + ((getMobile() == null) ? 0 : getMobile().hashCode());
        result = prime * result + ((getPhone() == null) ? 0 : getPhone().hashCode());
        result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
        result = prime * result + ((getAddress() == null) ? 0 : getAddress().hashCode());
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
        result = prime * result + ((getBatchNo() == null) ? 0 : getBatchNo().hashCode());
        result = prime * result + ((getRefId() == null) ? 0 : getRefId().hashCode());
        result = prime * result + ((getTmpType() == null) ? 0 : getTmpType().hashCode());
        result = prime * result + ((getTmpOperType() == null) ? 0 : getTmpOperType().hashCode());
        return result;
    }

    public void initAddValue(final String anBusinStatus) {
        this.initAddValue(anBusinStatus, null, null);
    }

    public void initAddValue(final CustMechContacter anContacter, final String anBusinStatus) {
        this.initAddValue(anBusinStatus, null, null);

        this.custNo = anContacter.getCustNo();
        this.name = anContacter.getName();
        this.sex = anContacter.getSex();
        this.mobile = anContacter.getMobile();
        this.phone = anContacter.getPhone();
        this.address = anContacter.getAddress();
        this.email = anContacter.getEmail();
        this.batchNo = anContacter.getBatchNo();
    }

    public void initAddValue(final String anBusinStatus, final String anTmpType, final Long anVersion) {
        this.id = SerialGenerator.getLongValue("CustMechContacterTmp.id");

        this.regOperId = UserUtils.getOperatorInfo().getId();
        this.regOperName = UserUtils.getOperatorInfo().getName();
        this.regDate = BetterDateUtils.getNumDate();
        this.regTime = BetterDateUtils.getNumTime();

        this.modiOperId = UserUtils.getOperatorInfo().getId();
        this.modiOperName = UserUtils.getOperatorInfo().getName();
        this.modiDate = BetterDateUtils.getNumDate();
        this.modiTime = BetterDateUtils.getNumTime();

        this.operOrg = UserUtils.getOperatorInfo().getOperOrg();
        this.businStatus = anBusinStatus;
        this.version = anVersion;

        this.tmpType = anTmpType;
    }

    public void initModifyValue(final CustMechContacterTmp anContacterTmp) {
        this.modiOperId = UserUtils.getOperatorInfo().getId();
        this.modiOperName = UserUtils.getOperatorInfo().getName();
        this.modiDate = BetterDateUtils.getNumDate();
        this.modiTime = BetterDateUtils.getNumTime();

        this.name = anContacterTmp.getName();
        this.sex = anContacterTmp.getSex();
        this.mobile = anContacterTmp.getMobile();
        this.phone = anContacterTmp.getPhone();
        this.address = anContacterTmp.getAddress();
        this.email = anContacterTmp.getEmail();
    }

    public void initModifyValue(final CustMechContacterTmp anContacterTmp, final String anBusinStatus,
            final Long anVersion) {
        this.initModifyValue(anContacterTmp);

        this.businStatus = anBusinStatus;
        this.version = anVersion;
    }

    public void initModifyValue(final CustMechContacter anContacter, final String anBusinStatus) {
        this.modiOperId = UserUtils.getOperatorInfo().getId();
        this.modiOperName = UserUtils.getOperatorInfo().getName();
        this.modiDate = BetterDateUtils.getNumDate();
        this.modiTime = BetterDateUtils.getNumTime();

        this.name = anContacter.getName();
        this.sex = anContacter.getSex();
        this.mobile = anContacter.getMobile();
        this.phone = anContacter.getPhone();
        this.address = anContacter.getAddress();
        this.email = anContacter.getEmail();

        this.businStatus = anBusinStatus;
    }
}