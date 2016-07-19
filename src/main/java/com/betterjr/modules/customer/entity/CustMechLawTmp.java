package com.betterjr.modules.customer.entity;

import com.betterjr.common.annotation.*;
import com.betterjr.common.entity.BetterjrEntity;
import javax.persistence.*;

@Access(AccessType.FIELD)
@Entity
@Table(name = "t_cust_mech_law_tmp")
public class CustMechLawTmp implements BetterjrEntity {
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
    @Column(name = "N_VERSION",  columnDefinition="INTEGER" )
    @MetaData( value="数据版本号", comments = "数据版本号")
    private Long version;

    /**
     * 姓名
     */
    @Column(name = "C_NAME",  columnDefinition="VARCHAR" )
    @MetaData( value="姓名", comments = "姓名")
    private String name;

    /**
     * 证件类型:0-身份证，1-护照，2-军官证，3-士兵证，4-回乡证，5-户口本，6-外国护照
     */
    @Column(name = "C_IDENTTYPE",  columnDefinition="CHAR" )
    @MetaData( value="证件类型:0-身份证", comments = "证件类型:0-身份证，1-护照，2-军官证，3-士兵证，4-回乡证，5-户口本，6-外国护照")
    private String identType;

    /**
     * 证件号码
     */
    @Column(name = "C_IDENTNO",  columnDefinition="VARCHAR" )
    @MetaData( value="证件号码", comments = "证件号码")
    private String identNo;

    /**
     * 证件有效期
     */
    @Column(name = "D_VALIDDATE",  columnDefinition="VARCHAR" )
    @MetaData( value="证件有效期", comments = "证件有效期")
    private String validDate;

    /**
     * 性别 0女 1男
     */
    @Column(name = "C_SEX",  columnDefinition="CHAR" )
    @MetaData( value="性别 0女 1男", comments = "性别 0女 1男")
    private String sex;

    /**
     * 出生日期
     */
    @Column(name = "D_BIRTHDATE",  columnDefinition="VARCHAR" )
    @MetaData( value="出生日期", comments = "出生日期")
    private String birthdate;

    /**
     * 电话
     */
    @Column(name = "C_PHONE",  columnDefinition="VARCHAR" )
    @MetaData( value="电话", comments = "电话")
    private String phone;

    /**
     * 教育水平 0初级中学 1高级中学 2大学专科 3大学本科 4硕士研究生 5博士研究生
     */
    @Column(name = "C_EDU_LEVEL",  columnDefinition="CHAR" )
    @MetaData( value="教育水平 0初级中学 1高级中学 2大学专科 3大学本科 4硕士研究生 5博士研究生", comments = "教育水平 0初级中学 1高级中学 2大学专科 3大学本科 4硕士研究生 5博士研究生")
    private String eduLevel;

    /**
     * 婚姻状况: 0未婚 1已婚 2离异
     */
    @Column(name = "C_MARTIAL_STATUS",  columnDefinition="CHAR" )
    @MetaData( value="婚姻状况: 0未婚 1已婚 2离异", comments = "婚姻状况: 0未婚 1已婚 2离异")
    private String martialStatus;

    /**
     * 籍贯
     */
    @Column(name = "C_NATIVE_PLACE",  columnDefinition="VARCHAR" )
    @MetaData( value="籍贯", comments = "籍贯")
    private String nativePlace;

    /**
     * 地址
     */
    @Column(name = "C_ADDRESS",  columnDefinition="VARCHAR" )
    @MetaData( value="地址", comments = "地址")
    private String address;

    /**
     * 附件
     */
    @Column(name = "N_BATCHNO",  columnDefinition="INTEGER" )
    @MetaData( value="附件", comments = "附件")
    private Long batchNo;

    /**
     * 创建人(操作员)ID号
     */
    @Column(name = "L_REG_OPERID",  columnDefinition="INTEGER" )
    @MetaData( value="创建人(操作员)ID号", comments = "创建人(操作员)ID号")
    private Long regOperId;

    /**
     * 创建人(操作员)姓名
     */
    @Column(name = "C_REG_OPERNAME",  columnDefinition="VARCHAR" )
    @MetaData( value="创建人(操作员)姓名", comments = "创建人(操作员)姓名")
    private String regOperName;

    /**
     * 创建日期
     */
    @Column(name = "D_REG_DATE",  columnDefinition="VARCHAR" )
    @MetaData( value="创建日期", comments = "创建日期")
    private String regDate;

    /**
     * 创建时间
     */
    @Column(name = "T_REG_TIME",  columnDefinition="VARCHAR" )
    @MetaData( value="创建时间", comments = "创建时间")
    private String regTime;

    /**
     * 修改人(操作员)ID号
     */
    @Column(name = "L_MODI_OPERID",  columnDefinition="INTEGER" )
    @MetaData( value="修改人(操作员)ID号", comments = "修改人(操作员)ID号")
    private Long modiOperId;

    /**
     * 修改人(操作员)姓名
     */
    @Column(name = "C_MODI_OPERNAME",  columnDefinition="VARCHAR" )
    @MetaData( value="修改人(操作员)姓名", comments = "修改人(操作员)姓名")
    private String modiOperName;

    /**
     * 修改日期
     */
    @Column(name = "D_MODI_DATE",  columnDefinition="VARCHAR" )
    @MetaData( value="修改日期", comments = "修改日期")
    private String modiDate;

    /**
     * 修改时间
     */
    @Column(name = "T_MODI_TIME",  columnDefinition="VARCHAR" )
    @MetaData( value="修改时间", comments = "修改时间")
    private String modiTime;

    /**
     * 操作机构
     */
    @Column(name = "C_OPERORG",  columnDefinition="VARCHAR" )
    @MetaData( value="操作机构", comments = "操作机构")
    private String operOrg;

    /**
     * 使用状态:0未使用  1使用中  2已使用
     */
    @Column(name = "C_BUSIN_STATUS",  columnDefinition="CHAR" )
    @MetaData( value="使用状态:0未使用  1使用中  2已使用", comments = "使用状态:0未使用  1使用中  2已使用")
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
     * 引用编号
     */
    @Column(name = "L_REF_ID",  columnDefinition="INTEGER" )
    @MetaData( value="引用编号", comments = "引用编号")
    private Long refId;

    /**
     * 流水类型:0 代录  1 变更  2 暂存
     */
    @Column(name = "C_TMP_TYPE",  columnDefinition="CHAR" )
    @MetaData( value="流水类型:0 代录  1 变更  2 暂存", comments = "流水类型:0 代录  1 变更  2 暂存")
    private String tmpType;

    /**
     * 流水操作类型:0 新增  1 修改  2 删除
     */
    @Column(name = "C_TMP_OPER_TYPE",  columnDefinition="CHAR" )
    @MetaData( value="流水操作类型:0 新增  1 修改  2 删除", comments = "流水操作类型:0 新增  1 修改  2 删除")
    private String tmpOperType;

    private static final long serialVersionUID = 1468812783866L;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getIdentType() {
        return identType;
    }

    public void setIdentType(String identType) {
        this.identType = identType == null ? null : identType.trim();
    }

    public String getIdentNo() {
        return identNo;
    }

    public void setIdentNo(String identNo) {
        this.identNo = identNo == null ? null : identNo.trim();
    }

    public String getValidDate() {
        return validDate;
    }

    public void setValidDate(String validDate) {
        this.validDate = validDate == null ? null : validDate.trim();
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex == null ? null : sex.trim();
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate == null ? null : birthdate.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getEduLevel() {
        return eduLevel;
    }

    public void setEduLevel(String eduLevel) {
        this.eduLevel = eduLevel == null ? null : eduLevel.trim();
    }

    public String getMartialStatus() {
        return martialStatus;
    }

    public void setMartialStatus(String martialStatus) {
        this.martialStatus = martialStatus == null ? null : martialStatus.trim();
    }

    public String getNativePlace() {
        return nativePlace;
    }

    public void setNativePlace(String nativePlace) {
        this.nativePlace = nativePlace == null ? null : nativePlace.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public Long getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(Long batchNo) {
        this.batchNo = batchNo;
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

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public String getTmpType() {
        return tmpType;
    }

    public void setTmpType(String tmpType) {
        this.tmpType = tmpType == null ? null : tmpType.trim();
    }

    public String getTmpOperType() {
        return tmpOperType;
    }

    public void setTmpOperType(String tmpOperType) {
        this.tmpOperType = tmpOperType == null ? null : tmpOperType.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", version=").append(version);
        sb.append(", name=").append(name);
        sb.append(", identType=").append(identType);
        sb.append(", identNo=").append(identNo);
        sb.append(", validDate=").append(validDate);
        sb.append(", sex=").append(sex);
        sb.append(", birthdate=").append(birthdate);
        sb.append(", phone=").append(phone);
        sb.append(", eduLevel=").append(eduLevel);
        sb.append(", martialStatus=").append(martialStatus);
        sb.append(", nativePlace=").append(nativePlace);
        sb.append(", address=").append(address);
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
        sb.append(", refId=").append(refId);
        sb.append(", tmpType=").append(tmpType);
        sb.append(", tmpOperType=").append(tmpOperType);
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
        CustMechLawTmp other = (CustMechLawTmp) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getVersion() == null ? other.getVersion() == null : this.getVersion().equals(other.getVersion()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getIdentType() == null ? other.getIdentType() == null : this.getIdentType().equals(other.getIdentType()))
            && (this.getIdentNo() == null ? other.getIdentNo() == null : this.getIdentNo().equals(other.getIdentNo()))
            && (this.getValidDate() == null ? other.getValidDate() == null : this.getValidDate().equals(other.getValidDate()))
            && (this.getSex() == null ? other.getSex() == null : this.getSex().equals(other.getSex()))
            && (this.getBirthdate() == null ? other.getBirthdate() == null : this.getBirthdate().equals(other.getBirthdate()))
            && (this.getPhone() == null ? other.getPhone() == null : this.getPhone().equals(other.getPhone()))
            && (this.getEduLevel() == null ? other.getEduLevel() == null : this.getEduLevel().equals(other.getEduLevel()))
            && (this.getMartialStatus() == null ? other.getMartialStatus() == null : this.getMartialStatus().equals(other.getMartialStatus()))
            && (this.getNativePlace() == null ? other.getNativePlace() == null : this.getNativePlace().equals(other.getNativePlace()))
            && (this.getAddress() == null ? other.getAddress() == null : this.getAddress().equals(other.getAddress()))
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
            && (this.getRefId() == null ? other.getRefId() == null : this.getRefId().equals(other.getRefId()))
            && (this.getTmpType() == null ? other.getTmpType() == null : this.getTmpType().equals(other.getTmpType()))
            && (this.getTmpOperType() == null ? other.getTmpOperType() == null : this.getTmpOperType().equals(other.getTmpOperType()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getIdentType() == null) ? 0 : getIdentType().hashCode());
        result = prime * result + ((getIdentNo() == null) ? 0 : getIdentNo().hashCode());
        result = prime * result + ((getValidDate() == null) ? 0 : getValidDate().hashCode());
        result = prime * result + ((getSex() == null) ? 0 : getSex().hashCode());
        result = prime * result + ((getBirthdate() == null) ? 0 : getBirthdate().hashCode());
        result = prime * result + ((getPhone() == null) ? 0 : getPhone().hashCode());
        result = prime * result + ((getEduLevel() == null) ? 0 : getEduLevel().hashCode());
        result = prime * result + ((getMartialStatus() == null) ? 0 : getMartialStatus().hashCode());
        result = prime * result + ((getNativePlace() == null) ? 0 : getNativePlace().hashCode());
        result = prime * result + ((getAddress() == null) ? 0 : getAddress().hashCode());
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
        result = prime * result + ((getRefId() == null) ? 0 : getRefId().hashCode());
        result = prime * result + ((getTmpType() == null) ? 0 : getTmpType().hashCode());
        result = prime * result + ((getTmpOperType() == null) ? 0 : getTmpOperType().hashCode());
        return result;
    }
}