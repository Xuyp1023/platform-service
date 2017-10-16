package com.betterjr.modules.cert.entity;

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
import com.betterjr.modules.account.entity.CustOperatorInfo;

@Access(AccessType.FIELD)
@Entity
@Table(name = "t_sys_verify_sign_cert")
public class VerifySignCert implements BetterjrEntity {
    /**
     * 编号
     */
    @Id
    @Column(name = "ID", columnDefinition = "INTEGER")
    @MetaData(value = "编号", comments = "编号")
    private Long id;

    /**
     * 名称
     */
    @Column(name = "C_NAME", columnDefinition = "VARCHAR")
    @MetaData(value = "名称", comments = "名称")
    private String name;

    /**
     * 参数类型
     */
    @Column(name = "C_TYPE", columnDefinition = "CHAR")
    @MetaData(value = "参数类型", comments = "参数类型")
    private String type;

    /**
     * 证书序列号
     */
    @Column(name = "C_SERIALNO", columnDefinition = "VARCHAR")
    @MetaData(value = "证书序列号", comments = "证书序列号")
    private String serialNo;

    /**
     * 证书公钥信息，Base64编码
     */
    @Column(name = "C_CERTINFO", columnDefinition = "TEXT")
    @MetaData(value = "证书公钥信息", comments = "证书公钥信息，Base64编码")
    private String certInfo;

    /**
     * 机构
     */
    @Column(name = "C_OPERORG", columnDefinition = "VARCHAR")
    @MetaData(value = "机构", comments = "机构")
    private String operOrg;

    /**
     * 公司
     */
    @Column(name = "L_CUSTNO", columnDefinition = "INTEGER")
    @MetaData(value = "公司", comments = "公司")
    private Long custNo;

    /**
     * 公司名称
     */
    @Column(name = "C_CUSTNAME", columnDefinition = "VARCHAR")
    @MetaData(value = "公司名称", comments = "公司名称")
    private String custName;

    /**
     * 业务状态
     */
    @Column(name = "C_BUSIN_STATUS", columnDefinition = "VARCHAR")
    @MetaData(value = "业务状态", comments = "业务状态")
    private String businStatus;

    @Column(name = "C_LAST_STATUS", columnDefinition = "VARCHAR")
    @MetaData(value = "", comments = "")
    private String lastStatus;

    @Column(name = "T_MODI_TIME", columnDefinition = "VARCHAR")
    @MetaData(value = "", comments = "")
    private String modiTime;

    @Column(name = "D_MODI_DATE", columnDefinition = "VARCHAR")
    @MetaData(value = "", comments = "")
    private String modiDate;

    @Column(name = "C_MODI_OPERNAME", columnDefinition = "VARCHAR")
    @MetaData(value = "", comments = "")
    private String modiOperName;

    @Column(name = "L_MODI_OPERID", columnDefinition = "INTEGER")
    @MetaData(value = "", comments = "")
    private Long modiOperId;

    @Column(name = "T_REG_TIME", columnDefinition = "VARCHAR")
    @MetaData(value = "", comments = "")
    private String regTime;

    @Column(name = "D_REG_DATE", columnDefinition = "VARCHAR")
    @MetaData(value = "", comments = "")
    private String regDate;

    @Column(name = "C_REG_OPERNAME", columnDefinition = "VARCHAR")
    @MetaData(value = "", comments = "")
    private String regOperName;

    @Column(name = "L_REG_OPERID", columnDefinition = "INTEGER")
    @MetaData(value = "", comments = "")
    private Long regOperId;

    @Column(name = "N_VERSION", columnDefinition = "INTEGER")
    @MetaData(value = "", comments = "")
    private Long version;

    private static final long serialVersionUID = -7937067974906643347L;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(final String serialNo) {
        this.serialNo = serialNo;
    }

    public String getCertInfo() {
        return certInfo;
    }

    public void setCertInfo(final String certInfo) {
        this.certInfo = certInfo;
    }

    public String getOperOrg() {
        return operOrg;
    }

    public void setOperOrg(final String operOrg) {
        this.operOrg = operOrg;
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
        this.custName = custName;
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

    public String getModiTime() {
        return modiTime;
    }

    public void setModiTime(final String modiTime) {
        this.modiTime = modiTime == null ? null : modiTime.trim();
    }

    public String getModiDate() {
        return modiDate;
    }

    public void setModiDate(final String modiDate) {
        this.modiDate = modiDate == null ? null : modiDate.trim();
    }

    public String getModiOperName() {
        return modiOperName;
    }

    public void setModiOperName(final String modiOperName) {
        this.modiOperName = modiOperName == null ? null : modiOperName.trim();
    }

    public Long getModiOperId() {
        return modiOperId;
    }

    public void setModiOperId(final Long modiOperId) {
        this.modiOperId = modiOperId;
    }

    public String getRegTime() {
        return regTime;
    }

    public void setRegTime(final String regTime) {
        this.regTime = regTime == null ? null : regTime.trim();
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(final String regDate) {
        this.regDate = regDate == null ? null : regDate.trim();
    }

    public String getRegOperName() {
        return regOperName;
    }

    public void setRegOperName(final String regOperName) {
        this.regOperName = regOperName == null ? null : regOperName.trim();
    }

    public Long getRegOperId() {
        return regOperId;
    }

    public void setRegOperId(final Long regOperId) {
        this.regOperId = regOperId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(final Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", type=").append(type);
        sb.append(", serialNo=").append(serialNo);
        sb.append(", certInfo=").append(certInfo);
        sb.append(", operOrg=").append(operOrg);
        sb.append(", custNo=").append(custNo);
        sb.append(", custName=").append(custName);
        sb.append(", businStatus=").append(businStatus);
        sb.append(", lastStatus=").append(lastStatus);
        sb.append(", modiTime=").append(modiTime);
        sb.append(", modiDate=").append(modiDate);
        sb.append(", modiOperName=").append(modiOperName);
        sb.append(", modiOperId=").append(modiOperId);
        sb.append(", regTime=").append(regTime);
        sb.append(", regDate=").append(regDate);
        sb.append(", regOperName=").append(regOperName);
        sb.append(", regOperId=").append(regOperId);
        sb.append(", version=").append(version);
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
        final VerifySignCert other = (VerifySignCert) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
                && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
                && (this.getSerialNo() == null ? other.getSerialNo() == null
                        : this.getSerialNo().equals(other.getSerialNo()))
                && (this.getCertInfo() == null ? other.getCertInfo() == null
                        : this.getCertInfo().equals(other.getCertInfo()))
                && (this.getOperOrg() == null ? other.getOperOrg() == null
                        : this.getOperOrg().equals(other.getOperOrg()))
                && (this.getCustNo() == null ? other.getCustNo() == null : this.getCustNo().equals(other.getCustNo()))
                && (this.getCustName() == null ? other.getCustName() == null
                        : this.getCustName().equals(other.getCustName()))
                && (this.getBusinStatus() == null ? other.getBusinStatus() == null
                        : this.getBusinStatus().equals(other.getBusinStatus()))
                && (this.getLastStatus() == null ? other.getLastStatus() == null
                        : this.getLastStatus().equals(other.getLastStatus()))
                && (this.getModiTime() == null ? other.getModiTime() == null
                        : this.getModiTime().equals(other.getModiTime()))
                && (this.getModiDate() == null ? other.getModiDate() == null
                        : this.getModiDate().equals(other.getModiDate()))
                && (this.getModiOperName() == null ? other.getModiOperName() == null
                        : this.getModiOperName().equals(other.getModiOperName()))
                && (this.getModiOperId() == null ? other.getModiOperId() == null
                        : this.getModiOperId().equals(other.getModiOperId()))
                && (this.getRegTime() == null ? other.getRegTime() == null
                        : this.getRegTime().equals(other.getRegTime()))
                && (this.getRegDate() == null ? other.getRegDate() == null
                        : this.getRegDate().equals(other.getRegDate()))
                && (this.getRegOperName() == null ? other.getRegOperName() == null
                        : this.getRegOperName().equals(other.getRegOperName()))
                && (this.getRegOperId() == null ? other.getRegOperId() == null
                        : this.getRegOperId().equals(other.getRegOperId()))
                && (this.getVersion() == null ? other.getVersion() == null
                        : this.getVersion().equals(other.getVersion()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getSerialNo() == null) ? 0 : getSerialNo().hashCode());
        result = prime * result + ((getCertInfo() == null) ? 0 : getCertInfo().hashCode());
        result = prime * result + ((getOperOrg() == null) ? 0 : getOperOrg().hashCode());
        result = prime * result + ((getCustNo() == null) ? 0 : getCustNo().hashCode());
        result = prime * result + ((getCustName() == null) ? 0 : getCustName().hashCode());
        result = prime * result + ((getBusinStatus() == null) ? 0 : getBusinStatus().hashCode());
        result = prime * result + ((getLastStatus() == null) ? 0 : getLastStatus().hashCode());
        result = prime * result + ((getModiTime() == null) ? 0 : getModiTime().hashCode());
        result = prime * result + ((getModiDate() == null) ? 0 : getModiDate().hashCode());
        result = prime * result + ((getModiOperName() == null) ? 0 : getModiOperName().hashCode());
        result = prime * result + ((getModiOperId() == null) ? 0 : getModiOperId().hashCode());
        result = prime * result + ((getRegTime() == null) ? 0 : getRegTime().hashCode());
        result = prime * result + ((getRegDate() == null) ? 0 : getRegDate().hashCode());
        result = prime * result + ((getRegOperName() == null) ? 0 : getRegOperName().hashCode());
        result = prime * result + ((getRegOperId() == null) ? 0 : getRegOperId().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        return result;
    }

    /**
     * @param anOperator
     */
    public void init(final CustOperatorInfo anOperator) {
        this.setId(SerialGenerator.getLongValue("VerifySignCert.id"));

        this.setRegDate(BetterDateUtils.getNumDate());
        this.setRegTime(BetterDateUtils.getNumTime());
        this.setModiDate(BetterDateUtils.getNumDate());
        this.setModiTime(BetterDateUtils.getNumTime());

        if (anOperator != null) {
            this.setRegOperId(anOperator.getId());
            this.setRegOperName(anOperator.getName());

            this.setModiOperId(anOperator.getId());
            this.setModiOperName(anOperator.getName());
        }
    }

    /**
     * @param anOperator
     */
    public void modify(final CustOperatorInfo anOperator) {
        this.setModiDate(BetterDateUtils.getNumDate());
        this.setModiTime(BetterDateUtils.getNumTime());

        if (anOperator != null) {
            this.setModiOperId(anOperator.getId());
            this.setModiOperName(anOperator.getName());
        }
    }
}