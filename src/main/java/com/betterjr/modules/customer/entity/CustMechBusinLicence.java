package com.betterjr.modules.customer.entity;

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
import com.betterjr.common.utils.UserUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Access(AccessType.FIELD)
@Entity
@Table(name = "t_cust_mech_busin_licence")
public class CustMechBusinLicence implements BetterjrEntity {
    /**
     * 编号
     */
    @Id
    @Column(name = "ID",  columnDefinition="INTEGER" )
    @MetaData( value="编号", comments = "编号")
    private Long id;

    /**
     * 数据版本号
     */
    @JsonIgnore
    @Column(name = "N_VERSION",  columnDefinition="INTEGER" )
    @MetaData( value="数据版本号", comments = "数据版本号")
    private Long version;

    /**
     * 统一社会信用代码
     */
    @Column(name = "C_UNIFIED_CODE",  columnDefinition="VARCHAR" )
    @MetaData( value="统一社会信用代码", comments = "统一社会信用代码")
    private String unifiedCode;

    /**
     * 注册号
     */
    @Column(name = "C_REG_NO",  columnDefinition="VARCHAR" )
    @MetaData( value="注册号", comments = "注册号")
    private String regNo;

    /**
     * 组织机构代码证号
     */
    @Column(name = "C_ORG_CODE",  columnDefinition="VARCHAR" )
    @MetaData( value="组织机构代码证号", comments = "组织机构代码证号")
    private String orgCode;

    /**
     * 税务登记证号
     */
    @Column(name = "C_TAX_NO",  columnDefinition="VARCHAR" )
    @MetaData( value="税务登记证号", comments = "税务登记证号")
    private String taxNo;

    /**
     * 企业类型: 0国有企业 1集体所有制企业 2私营企业 3股份制企业 4联营企业 5外商投资企业 6港澳台投资企业 7股份合作企业
     */
    @Column(name = "C_CORP_TYPE",  columnDefinition="VARCHAR" )
    @MetaData( value="企业类型", comments = "企业类型: 0国有企业 1集体所有制企业 2私营企业 3股份制企业 4联营企业 5外商投资企业 6港澳台投资企业 7股份合作企业")
    private String corpType;

    /**
     * 地址
     */
    @Column(name = "C_ADDRESS",  columnDefinition="VARCHAR" )
    @MetaData( value="地址", comments = "地址")
    private String address;

    /**
     * 法人姓名
     */
    @Column(name = "C_LAW_NAME",  columnDefinition="VARCHAR" )
    @MetaData( value="法人姓名", comments = "法人姓名")
    private String lawName;

    /**
     * 注册资本
     */
    @Column(name = "F_REG_CAPITAL",  columnDefinition="VARCHAR" )
    @MetaData( value="注册资本", comments = "注册资本")
    private String regCapital;

    /**
     * 实收资本
     */
    @Column(name = "F_PAID_CAPITAL",  columnDefinition="VARCHAR" )
    @MetaData( value="实收资本", comments = "实收资本")
    private String paidCapital;

    /**
     * 成立日期
     */
    @JsonSerialize(using = CustDateJsonSerializer.class)
    @Column(name = "D_SETUP_DATE",  columnDefinition="VARCHAR" )
    @MetaData( value="成立日期", comments = "成立日期")
    private String setupDate;

    /**
     * 经营开始日期
     */
    @JsonSerialize(using = CustDateJsonSerializer.class)
    @Column(name = "D_START_DATE",  columnDefinition="VARCHAR" )
    @MetaData( value="经营开始日期", comments = "经营开始日期")
    private String startDate;

    /**
     * 经营结束时间
     */
    @JsonSerialize(using = CustDateJsonSerializer.class)
    @Column(name = "D_END_DATE",  columnDefinition="VARCHAR" )
    @MetaData( value="经营结束时间", comments = "经营结束时间")
    private String endDate;

    /**
     * 登机机关
     */
    @Column(name = "C_REG_BRANCH",  columnDefinition="VARCHAR" )
    @MetaData( value="登机机关", comments = "登机机关")
    private String regBranch;

    /**
     * 制证日期
     */
    @JsonSerialize(using = CustDateJsonSerializer.class)
    @Column(name = "D_CERTIFIED_DATE",  columnDefinition="VARCHAR" )
    @MetaData( value="制证日期", comments = "制证日期")
    private String certifiedDate;

    /**
     * 营业范围
     */
    @Column(name = "C_BUSIN_SCOPE",  columnDefinition="VARCHAR" )
    @MetaData( value="营业范围", comments = "营业范围")
    private String businScope;

    /**
     * 附件
     */
    @Column(name = "N_BATCHNO",  columnDefinition="INTEGER" )
    @MetaData( value="附件", comments = "附件")
    private Long batchNo;

    /**
     * 创建人(操作员)ID号
     */
    @JsonIgnore
    @Column(name = "L_REG_OPERID",  columnDefinition="INTEGER" )
    @MetaData( value="创建人(操作员)ID号", comments = "创建人(操作员)ID号")
    private Long regOperId;

    /**
     * 创建人(操作员)姓名
     */
    @JsonIgnore
    @Column(name = "C_REG_OPERNAME",  columnDefinition="VARCHAR" )
    @MetaData( value="创建人(操作员)姓名", comments = "创建人(操作员)姓名")
    private String regOperName;

    /**
     * 创建日期
     */
    @JsonSerialize(using = CustDateJsonSerializer.class)
    @Column(name = "D_REG_DATE",  columnDefinition="VARCHAR" )
    @MetaData( value="创建日期", comments = "创建日期")
    private String regDate;

    /**
     * 创建时间
     */
    @JsonIgnore
    @Column(name = "T_REG_TIME",  columnDefinition="VARCHAR" )
    @MetaData( value="创建时间", comments = "创建时间")
    private String regTime;

    /**
     * 修改人(操作员)ID号
     */
    @JsonIgnore
    @Column(name = "L_MODI_OPERID",  columnDefinition="INTEGER" )
    @MetaData( value="修改人(操作员)ID号", comments = "修改人(操作员)ID号")
    private Long modiOperId;

    /**
     * 修改人(操作员)姓名
     */
    @JsonIgnore
    @Column(name = "C_MODI_OPERNAME",  columnDefinition="VARCHAR" )
    @MetaData( value="修改人(操作员)姓名", comments = "修改人(操作员)姓名")
    private String modiOperName;

    /**
     * 修改日期
     */
    @JsonSerialize(using = CustDateJsonSerializer.class)
    @Column(name = "D_MODI_DATE",  columnDefinition="VARCHAR" )
    @MetaData( value="修改日期", comments = "修改日期")
    private String modiDate;

    /**
     * 修改时间
     */
    @JsonIgnore
    @Column(name = "T_MODI_TIME",  columnDefinition="VARCHAR" )
    @MetaData( value="修改时间", comments = "修改时间")
    private String modiTime;

    /**
     * 操作机构
     */
    @JsonIgnore
    @Column(name = "C_OPERORG",  columnDefinition="VARCHAR" )
    @MetaData( value="操作机构", comments = "操作机构")
    private String operOrg;

    @Column(name = "C_BUSIN_STATUS",  columnDefinition="CHAR" )
    @MetaData( value="", comments = "")
    private String businStatus;

    @Column(name = "C_LAST_STATUS",  columnDefinition="CHAR" )
    @MetaData( value="", comments = "")
    private String lastStatus;

    /**
     * 客户编号
     */
    @Column(name = "L_CUSTNO",  columnDefinition="INTEGER" )
    @MetaData( value="客户编号", comments = "客户编号")
    private Long custNo;

    /**
     * 客户全称
     */
    @Column(name = "C_CUSTNAME",  columnDefinition="VARCHAR" )
    @MetaData( value="客户全称", comments = "客户全称")
    private String custName;

    private static final long serialVersionUID = 1468812783858L;

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

    public String getUnifiedCode() {
        return unifiedCode;
    }

    public void setUnifiedCode(final String anUnifiedCode) {
        unifiedCode = anUnifiedCode;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(final String regNo) {
        this.regNo = regNo == null ? null : regNo.trim();
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(final String anOrgCode) {
        orgCode = anOrgCode;
    }

    public String getTaxNo() {
        return taxNo;
    }

    public void setTaxNo(final String anTaxNo) {
        taxNo = anTaxNo;
    }

    public String getCorpType() {
        return corpType;
    }

    public void setCorpType(final String corpType) {
        this.corpType = corpType == null ? null : corpType.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getLawName() {
        return lawName;
    }

    public void setLawName(final String lawName) {
        this.lawName = lawName == null ? null : lawName.trim();
    }

    public String getRegCapital() {
        return regCapital;
    }

    public void setRegCapital(final String regCapital) {
        this.regCapital = regCapital == null ? null : regCapital.trim();
    }

    public String getPaidCapital() {
        return paidCapital;
    }

    public void setPaidCapital(final String paidCapital) {
        this.paidCapital = paidCapital == null ? null : paidCapital.trim();
    }

    public String getSetupDate() {
        return setupDate;
    }

    public void setSetupDate(final String setupDate) {
        this.setupDate = setupDate == null ? null : setupDate.trim();
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(final String startDate) {
        this.startDate = startDate == null ? null : startDate.trim();
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(final String endDate) {
        this.endDate = endDate == null ? null : endDate.trim();
    }

    public String getRegBranch() {
        return regBranch;
    }

    public void setRegBranch(final String regBranch) {
        this.regBranch = regBranch == null ? null : regBranch.trim();
    }

    public String getCertifiedDate() {
        return certifiedDate;
    }

    public void setCertifiedDate(final String certifiedDate) {
        this.certifiedDate = certifiedDate == null ? null : certifiedDate.trim();
    }

    public String getBusinScope() {
        return businScope;
    }

    public void setBusinScope(final String businScope) {
        this.businScope = businScope == null ? null : businScope.trim();
    }

    public Long getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(final Long batchNo) {
        this.batchNo = batchNo;
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

    public void setCustName(final String anCustName) {
        custName = anCustName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", version=").append(version);
        sb.append(", unifiedCode=").append(unifiedCode);
        sb.append(", regNo=").append(regNo);
        sb.append(", orgCode=").append(orgCode);
        sb.append(", taxNo=").append(taxNo);
        sb.append(", corpType=").append(corpType);
        sb.append(", address=").append(address);
        sb.append(", lawName=").append(lawName);
        sb.append(", regCapital=").append(regCapital);
        sb.append(", paidCapital=").append(paidCapital);
        sb.append(", setupDate=").append(setupDate);
        sb.append(", startDate=").append(startDate);
        sb.append(", endDate=").append(endDate);
        sb.append(", regBranch=").append(regBranch);
        sb.append(", certifiedDate=").append(certifiedDate);
        sb.append(", businScope=").append(businScope);
        sb.append(", batchNo=").append(batchNo);
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
        final CustMechBusinLicence other = (CustMechBusinLicence) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getVersion() == null ? other.getVersion() == null : this.getVersion().equals(other.getVersion()))
                && (this.getUnifiedCode() == null ? other.getUnifiedCode() == null : this.getUnifiedCode().equals(other.getUnifiedCode()))
                && (this.getRegNo() == null ? other.getRegNo() == null : this.getRegNo().equals(other.getRegNo()))
                && (this.getOrgCode() == null ? other.getOrgCode() == null : this.getOrgCode().equals(other.getOrgCode()))
                && (this.getTaxNo() == null ? other.getTaxNo() == null : this.getTaxNo().equals(other.getTaxNo()))
                && (this.getCorpType() == null ? other.getCorpType() == null : this.getCorpType().equals(other.getCorpType()))
                && (this.getAddress() == null ? other.getAddress() == null : this.getAddress().equals(other.getAddress()))
                && (this.getLawName() == null ? other.getLawName() == null : this.getLawName().equals(other.getLawName()))
                && (this.getRegCapital() == null ? other.getRegCapital() == null : this.getRegCapital().equals(other.getRegCapital()))
                && (this.getPaidCapital() == null ? other.getPaidCapital() == null : this.getPaidCapital().equals(other.getPaidCapital()))
                && (this.getSetupDate() == null ? other.getSetupDate() == null : this.getSetupDate().equals(other.getSetupDate()))
                && (this.getStartDate() == null ? other.getStartDate() == null : this.getStartDate().equals(other.getStartDate()))
                && (this.getEndDate() == null ? other.getEndDate() == null : this.getEndDate().equals(other.getEndDate()))
                && (this.getRegBranch() == null ? other.getRegBranch() == null : this.getRegBranch().equals(other.getRegBranch()))
                && (this.getCertifiedDate() == null ? other.getCertifiedDate() == null : this.getCertifiedDate().equals(other.getCertifiedDate()))
                && (this.getBusinScope() == null ? other.getBusinScope() == null : this.getBusinScope().equals(other.getBusinScope()))
                && (this.getBatchNo() == null ? other.getBatchNo() == null : this.getBatchNo().equals(other.getBatchNo()))
                && (this.getRegOperId() == null ? other.getRegOperId() == null : this.getRegOperId().equals(other.getRegOperId()))
                && (this.getRegOperName() == null ? other.getRegOperName() == null : this.getRegOperName().equals(other.getRegOperName()))
                && (this.getRegDate() == null ? other.getRegDate() == null : this.getRegDate().equals(other.getRegDate()))
                && (this.getRegTime() == null ? other.getRegTime() == null : this.getRegTime().equals(other.getRegTime()))
                && (this.getModiOperId() == null ? other.getModiOperId() == null : this.getModiOperId().equals(other.getModiOperId()))
                && (this.getModiOperName() == null ? other.getModiOperName() == null : this.getModiOperName().equals(other.getModiOperName()))
                && (this.getModiDate() == null ? other.getModiDate() == null : this.getModiDate().equals(other.getModiDate()))
                && (this.getModiTime() == null ? other.getModiTime() == null : this.getModiTime().equals(other.getModiTime()))
                && (this.getOperOrg() == null ? other.getOperOrg() == null : this.getOperOrg().equals(other.getOperOrg()))
                && (this.getBusinStatus() == null ? other.getBusinStatus() == null : this.getBusinStatus().equals(other.getBusinStatus()))
                && (this.getLastStatus() == null ? other.getLastStatus() == null : this.getLastStatus().equals(other.getLastStatus()))
                && (this.getCustNo() == null ? other.getCustNo() == null : this.getCustNo().equals(other.getCustNo()))
                && (this.getCustName() == null ? other.getCustName() == null : this.getCustName().equals(other.getCustName()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        result = prime * result + ((getUnifiedCode() == null) ? 0 : getUnifiedCode().hashCode());
        result = prime * result + ((getRegNo() == null) ? 0 : getRegNo().hashCode());
        result = prime * result + ((getOrgCode() == null) ? 0 : getOrgCode().hashCode());
        result = prime * result + ((getTaxNo() == null) ? 0 : getTaxNo().hashCode());
        result = prime * result + ((getCorpType() == null) ? 0 : getCorpType().hashCode());
        result = prime * result + ((getAddress() == null) ? 0 : getAddress().hashCode());
        result = prime * result + ((getLawName() == null) ? 0 : getLawName().hashCode());
        result = prime * result + ((getRegCapital() == null) ? 0 : getRegCapital().hashCode());
        result = prime * result + ((getPaidCapital() == null) ? 0 : getPaidCapital().hashCode());
        result = prime * result + ((getSetupDate() == null) ? 0 : getSetupDate().hashCode());
        result = prime * result + ((getStartDate() == null) ? 0 : getStartDate().hashCode());
        result = prime * result + ((getEndDate() == null) ? 0 : getEndDate().hashCode());
        result = prime * result + ((getRegBranch() == null) ? 0 : getRegBranch().hashCode());
        result = prime * result + ((getCertifiedDate() == null) ? 0 : getCertifiedDate().hashCode());
        result = prime * result + ((getBusinScope() == null) ? 0 : getBusinScope().hashCode());
        result = prime * result + ((getBatchNo() == null) ? 0 : getBatchNo().hashCode());
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

    public void initAddValue() {
        this.initAddValue(null, null, null, null, null);
    }

    public void initAddValue(final Long anCustNo, final String anCustName, final Long anRegOperId, final String anRegOperName, final String anOperOrg) {
        this.id = SerialGenerator.getLongValue("CustMechBusinLicence.id");

        this.regOperId = anRegOperId;
        this.regOperName = anRegOperName;
        this.operOrg = anOperOrg;

        this.regDate = BetterDateUtils.getNumDate();
        this.regTime = BetterDateUtils.getNumTime();

        if (UserUtils.getOperatorInfo() != null) {
            this.modiOperId = UserUtils.getOperatorInfo().getId();
            this.modiOperName = UserUtils.getOperatorInfo().getName();
        }else{
            this.modiOperId = anRegOperId;
            this.modiOperName = anRegOperName;
        }

        this.modiDate = BetterDateUtils.getNumDate();
        this.modiTime = BetterDateUtils.getNumTime();

        this.custNo = anCustNo;
        this.custName = anCustName;

        this.businStatus = "0";
    }

    public void initModifyValue(final CustMechBusinLicence anCustMechBusinLicence) {
        this.modiOperId = UserUtils.getOperatorInfo().getId();
        this.modiOperName = UserUtils.getOperatorInfo().getName();
        this.modiDate = BetterDateUtils.getNumDate();
        this.modiTime = BetterDateUtils.getNumTime();

        this.unifiedCode = anCustMechBusinLicence.getUnifiedCode();
        this.regNo = anCustMechBusinLicence.getRegNo();
        this.orgCode = anCustMechBusinLicence.getOrgCode();
        this.taxNo = anCustMechBusinLicence.getTaxNo();
        this.corpType = anCustMechBusinLicence.getCorpType();
        this.address = anCustMechBusinLicence.getAddress();
        this.lawName = anCustMechBusinLicence.getLawName();
        this.regCapital = anCustMechBusinLicence.getRegCapital();
        this.paidCapital = anCustMechBusinLicence.getPaidCapital();
        this.setupDate = anCustMechBusinLicence.getSetupDate();
        this.startDate = anCustMechBusinLicence.getStartDate();
        this.endDate = anCustMechBusinLicence.getEndDate();
        this.regBranch = anCustMechBusinLicence.getRegBranch();
        this.certifiedDate = anCustMechBusinLicence.getCertifiedDate();
        this.businScope = anCustMechBusinLicence.getBusinScope();
        this.batchNo = anCustMechBusinLicence.getBatchNo();
        /*
        this.custNo = anCustMechBusinLicence.getCustNo();
         */
        this.custName = anCustMechBusinLicence.getCustName();

        this.businStatus = anCustMechBusinLicence.getBusinStatus();
    }

    public void initModifyValue(final CustMechBusinLicenceTmp anBusinLicenceTmp) {
        this.modiOperId = UserUtils.getOperatorInfo().getId();
        this.modiOperName = UserUtils.getOperatorInfo().getName();
        this.modiDate = BetterDateUtils.getNumDate();
        this.modiTime = BetterDateUtils.getNumTime();

        this.unifiedCode = anBusinLicenceTmp.getUnifiedCode();
        this.regNo = anBusinLicenceTmp.getRegNo();
        this.orgCode = anBusinLicenceTmp.getOrgCode();
        this.taxNo = anBusinLicenceTmp.getTaxNo();
        this.corpType = anBusinLicenceTmp.getCorpType();
        this.address = anBusinLicenceTmp.getAddress();
        this.lawName = anBusinLicenceTmp.getLawName();
        this.regCapital = anBusinLicenceTmp.getRegCapital();
        this.paidCapital = anBusinLicenceTmp.getPaidCapital();
        this.setupDate = anBusinLicenceTmp.getSetupDate();
        this.startDate = anBusinLicenceTmp.getStartDate();
        this.endDate = anBusinLicenceTmp.getEndDate();
        this.regBranch = anBusinLicenceTmp.getRegBranch();
        this.certifiedDate = anBusinLicenceTmp.getCertifiedDate();
        this.businScope = anBusinLicenceTmp.getBusinScope();
        this.batchNo = anBusinLicenceTmp.getBatchNo();
        /*
        this.custNo = anBusinLicenceTmp.getCustNo();
         */
        this.custName = anBusinLicenceTmp.getCustName();
    }
}