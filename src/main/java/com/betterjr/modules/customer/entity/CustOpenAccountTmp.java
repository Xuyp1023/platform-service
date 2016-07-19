package com.betterjr.modules.customer.entity;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.betterjr.common.annotation.MetaData;
import com.betterjr.common.entity.BetterjrEntity;

@Access(AccessType.FIELD)
@Entity
@Table(name = "t_cust_open_account_tmp")
public class CustOpenAccountTmp implements BetterjrEntity {
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
     * 客户名称
     */
    @Column(name = "C_CUSTNAME",  columnDefinition="VARCHAR" )
    @MetaData( value="客户名称", comments = "客户名称")
    private String custName;

    /**
     * 证件号码
     */
    @Column(name = "C_IDENTNO",  columnDefinition="VARCHAR" )
    @MetaData( value="证件号码", comments = "证件号码")
    private String identNo;

    /**
     * 证件类型
     */
    @Column(name = "C_IDENTTYPE",  columnDefinition="CHAR" )
    @MetaData( value="证件类型", comments = "证件类型")
    private String identType;

    /**
     * 证件有效期
     */
    @Column(name = "D_VALIDDATE",  columnDefinition="VARCHAR" )
    @MetaData( value="证件有效期", comments = "证件有效期")
    private String validDate;

    /**
     * 营业执照号码
     */
    @Column(name = "C_BUSIN_LICENCE",  columnDefinition="VARCHAR" )
    @MetaData( value="营业执照号码", comments = "营业执照号码")
    private String businLicence;

    /**
     * 营业执照登记日期
     */
    @Column(name = "D_BUSIN_LICENCE_REGDATE",  columnDefinition="VARCHAR" )
    @MetaData( value="营业执照登记日期", comments = "营业执照登记日期")
    private String businLicenceRegDate;

    /**
     * 营业执照截止日期
     */
    @Column(name = "D_BUSIN_LICENCE_VALIDDATE",  columnDefinition="VARCHAR" )
    @MetaData( value="营业执照截止日期", comments = "营业执照截止日期")
    private String businLicenceValidDate;

    /**
     * 地址
     */
    @Column(name = "C_ADDRESS",  columnDefinition="VARCHAR" )
    @MetaData( value="地址", comments = "地址")
    private String address;

    /**
     * 邮编
     */
    @Column(name = "C_ZIPCODE",  columnDefinition="VARCHAR" )
    @MetaData( value="邮编", comments = "邮编")
    private String zipcode;

    /**
     * 电话
     */
    @Column(name = "C_PHONE",  columnDefinition="VARCHAR" )
    @MetaData( value="电话", comments = "电话")
    private String phone;

    /**
     * 传真号码
     */
    @Column(name = "C_FAX",  columnDefinition="VARCHAR" )
    @MetaData( value="传真号码", comments = "传真号码")
    private String fax;

    /**
     * 电子邮件
     */
    @Column(name = "C_EMAIL",  columnDefinition="VARCHAR" )
    @MetaData( value="电子邮件", comments = "电子邮件")
    private String email;

    /**
     * 银行账户
     */
    @Column(name = "C_BANK_ACCO",  columnDefinition="VARCHAR" )
    @MetaData( value="银行账户", comments = "银行账户")
    private String bankAcco;

    /**
     * 银行户名
     */
    @Column(name = "C_BANK_ACCONAME",  columnDefinition="VARCHAR" )
    @MetaData( value="银行户名", comments = "银行户名")
    private String bankAccoName;

    /**
     * 银行编码
     */
    @Column(name = "C_BANK_NO",  columnDefinition="VARCHAR" )
    @MetaData( value="银行编码", comments = "银行编码")
    private String bankNo;

    /**
     * 银行城市地区代码
     */
    @Column(name = "C_BANK_CITYNO",  columnDefinition="VARCHAR" )
    @MetaData( value="银行城市地区代码", comments = "银行城市地区代码")
    private String bankCityno;

    /**
     * 开户银行(全称)
     */
    @Column(name = "C_BANK_NAME",  columnDefinition="VARCHAR" )
    @MetaData( value="开户银行(全称)", comments = "开户银行(全称)")
    private String bankName;

    /**
     * 经办人姓名
     */
    @Column(name = "C_OPERNAME",  columnDefinition="VARCHAR" )
    @MetaData( value="经办人姓名", comments = "经办人姓名")
    private String operName;

    /**
     * 经办人证件类型
     */
    @Column(name = "C_OPER_IDENTTYPE",  columnDefinition="CHAR" )
    @MetaData( value="经办人证件类型", comments = "经办人证件类型")
    private String operIdenttype;

    /**
     * 经办人证件号码
     */
    @Column(name = "C_OPER_IDENTNO",  columnDefinition="VARCHAR" )
    @MetaData( value="经办人证件号码", comments = "经办人证件号码")
    private String operIdentno;

    /**
     * 经办人证件有效期
     */
    @Column(name = "D_OPER_VALIDDATE",  columnDefinition="VARCHAR" )
    @MetaData( value="经办人证件有效期", comments = "经办人证件有效期")
    private String operValiddate;

    /**
     * 经办人手机号码
     */
    @Column(name = "C_OPER_MOBILE",  columnDefinition="VARCHAR" )
    @MetaData( value="经办人手机号码", comments = "经办人手机号码")
    private String operMobile;

    /**
     * 经办人邮箱
     */
    @Column(name = "C_OPER_EMAIL",  columnDefinition="VARCHAR" )
    @MetaData( value="经办人邮箱", comments = "经办人邮箱")
    private String operEmail;

    /**
     * 经办人联系电话
     */
    @Column(name = "C_OPER_PHONE",  columnDefinition="VARCHAR" )
    @MetaData( value="经办人联系电话", comments = "经办人联系电话")
    private String operPhone;

    /**
     * 经办人传真号码
     */
    @Column(name = "C_OPER_FAX_NO",  columnDefinition="VARCHAR" )
    @MetaData( value="经办人传真号码", comments = "经办人传真号码")
    private String operFaxNo;

    /**
     * 法人姓名
     */
    @Column(name = "C_LAW_NAME",  columnDefinition="VARCHAR" )
    @MetaData( value="法人姓名", comments = "法人姓名")
    private String lawName;

    /**
     * 法人证件类型
     */
    @Column(name = "C_LAW_IDENTTYPE",  columnDefinition="VARCHAR" )
    @MetaData( value="法人证件类型", comments = "法人证件类型")
    private String lawIdentType;

    /**
     * 法人证件号码
     */
    @Column(name = "C_LAW_IDENTNO",  columnDefinition="VARCHAR" )
    @MetaData( value="法人证件号码", comments = "法人证件号码")
    private String lawIdentNo;

    /**
     * 法人证件有限期
     */
    @Column(name = "D_LAW_VALIDDATE",  columnDefinition="VARCHAR" )
    @MetaData( value="法人证件有限期", comments = "法人证件有限期")
    private String lawValidDate;

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
     * 使用状态:0未使用  1使用中  2已使用
     */
    @Column(name = "C_BUSIN_STATUS",  columnDefinition="CHAR" )
    @MetaData( value="使用状态:0未使用  1使用中  2已使用", comments = "使用状态:0未使用  1使用中  2已使用")
    private String businStatus;

    @Column(name = "C_LAST_STATUS",  columnDefinition="CHAR" )
    @MetaData( value="", comments = "")
    private String lastStatus;

    /**
     * 操作机构
     */
    @Column(name = "C_OPERORG",  columnDefinition="VARCHAR" )
    @MetaData( value="操作机构", comments = "操作机构")
    private String operOrg;

    /**
     * 流水类型:0 代录   2 暂存
     */
    @Column(name = "C_TMP_TYPE",  columnDefinition="CHAR" )
    @MetaData( value="流水类型:0 代录   2 暂存", comments = "流水类型:0 代录   2 暂存")
    private String tmpType;

    /**
     * 流水操作类型:0 新增  1 修改  2 删除
     */
    @Column(name = "C_TMP_OPER_TYPE",  columnDefinition="CHAR" )
    @MetaData( value="流水操作类型:0 新增  1 修改  2 删除", comments = "流水操作类型:0 新增  1 修改  2 删除")
    private String tmpOperType;

    private static final long serialVersionUID = 1468812783872L;

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

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName == null ? null : custName.trim();
    }

    public String getIdentNo() {
        return identNo;
    }

    public void setIdentNo(String identNo) {
        this.identNo = identNo == null ? null : identNo.trim();
    }

    public String getIdentType() {
        return identType;
    }

    public void setIdentType(String identType) {
        this.identType = identType == null ? null : identType.trim();
    }

    public String getValidDate() {
        return validDate;
    }

    public void setValidDate(String validDate) {
        this.validDate = validDate == null ? null : validDate.trim();
    }

    public String getBusinLicence() {
        return businLicence;
    }

    public void setBusinLicence(String businLicence) {
        this.businLicence = businLicence == null ? null : businLicence.trim();
    }

    public String getBusinLicenceRegDate() {
        return businLicenceRegDate;
    }

    public void setBusinLicenceRegDate(String businLicenceRegDate) {
        this.businLicenceRegDate = businLicenceRegDate == null ? null : businLicenceRegDate.trim();
    }

    public String getBusinLicenceValidDate() {
        return businLicenceValidDate;
    }

    public void setBusinLicenceValidDate(String businLicenceValidDate) {
        this.businLicenceValidDate = businLicenceValidDate == null ? null : businLicenceValidDate.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode == null ? null : zipcode.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax == null ? null : fax.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getBankAcco() {
        return bankAcco;
    }

    public void setBankAcco(String bankAcco) {
        this.bankAcco = bankAcco == null ? null : bankAcco.trim();
    }

    public String getBankAccoName() {
        return bankAccoName;
    }

    public void setBankAccoName(String bankAccoName) {
        this.bankAccoName = bankAccoName == null ? null : bankAccoName.trim();
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo == null ? null : bankNo.trim();
    }

    public String getBankCityno() {
        return bankCityno;
    }

    public void setBankCityno(String bankCityno) {
        this.bankCityno = bankCityno == null ? null : bankCityno.trim();
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName == null ? null : bankName.trim();
    }

    public String getOperName() {
        return operName;
    }

    public void setOperName(String operName) {
        this.operName = operName == null ? null : operName.trim();
    }

    public String getOperIdenttype() {
        return operIdenttype;
    }

    public void setOperIdenttype(String operIdenttype) {
        this.operIdenttype = operIdenttype == null ? null : operIdenttype.trim();
    }

    public String getOperIdentno() {
        return operIdentno;
    }

    public void setOperIdentno(String operIdentno) {
        this.operIdentno = operIdentno == null ? null : operIdentno.trim();
    }

    public String getOperValiddate() {
        return operValiddate;
    }

    public void setOperValiddate(String operValiddate) {
        this.operValiddate = operValiddate == null ? null : operValiddate.trim();
    }

    public String getOperMobile() {
        return operMobile;
    }

    public void setOperMobile(String operMobile) {
        this.operMobile = operMobile == null ? null : operMobile.trim();
    }

    public String getOperEmail() {
        return operEmail;
    }

    public void setOperEmail(String operEmail) {
        this.operEmail = operEmail == null ? null : operEmail.trim();
    }

    public String getOperPhone() {
        return operPhone;
    }

    public void setOperPhone(String operPhone) {
        this.operPhone = operPhone == null ? null : operPhone.trim();
    }

    public String getOperFaxNo() {
        return operFaxNo;
    }

    public void setOperFaxNo(String operFaxNo) {
        this.operFaxNo = operFaxNo == null ? null : operFaxNo.trim();
    }

    public String getLawName() {
        return lawName;
    }

    public void setLawName(String lawName) {
        this.lawName = lawName == null ? null : lawName.trim();
    }

    public String getLawIdentType() {
        return lawIdentType;
    }

    public void setLawIdentType(String lawIdentType) {
        this.lawIdentType = lawIdentType == null ? null : lawIdentType.trim();
    }

    public String getLawIdentNo() {
        return lawIdentNo;
    }

    public void setLawIdentNo(String lawIdentNo) {
        this.lawIdentNo = lawIdentNo == null ? null : lawIdentNo.trim();
    }

    public String getLawValidDate() {
        return lawValidDate;
    }

    public void setLawValidDate(String lawValidDate) {
        this.lawValidDate = lawValidDate == null ? null : lawValidDate.trim();
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

    public String getOperOrg() {
        return operOrg;
    }

    public void setOperOrg(String operOrg) {
        this.operOrg = operOrg == null ? null : operOrg.trim();
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
        sb.append(", custName=").append(custName);
        sb.append(", identNo=").append(identNo);
        sb.append(", identType=").append(identType);
        sb.append(", validDate=").append(validDate);
        sb.append(", businLicence=").append(businLicence);
        sb.append(", businLicenceRegDate=").append(businLicenceRegDate);
        sb.append(", businLicenceValidDate=").append(businLicenceValidDate);
        sb.append(", address=").append(address);
        sb.append(", zipcode=").append(zipcode);
        sb.append(", phone=").append(phone);
        sb.append(", fax=").append(fax);
        sb.append(", email=").append(email);
        sb.append(", bankAcco=").append(bankAcco);
        sb.append(", bankAccoName=").append(bankAccoName);
        sb.append(", bankNo=").append(bankNo);
        sb.append(", bankCityno=").append(bankCityno);
        sb.append(", bankName=").append(bankName);
        sb.append(", operName=").append(operName);
        sb.append(", operIdenttype=").append(operIdenttype);
        sb.append(", operIdentno=").append(operIdentno);
        sb.append(", operValiddate=").append(operValiddate);
        sb.append(", operMobile=").append(operMobile);
        sb.append(", operEmail=").append(operEmail);
        sb.append(", operPhone=").append(operPhone);
        sb.append(", operFaxNo=").append(operFaxNo);
        sb.append(", lawName=").append(lawName);
        sb.append(", lawIdentType=").append(lawIdentType);
        sb.append(", lawIdentNo=").append(lawIdentNo);
        sb.append(", lawValidDate=").append(lawValidDate);
        sb.append(", batchNo=").append(batchNo);
        sb.append(", regOperId=").append(regOperId);
        sb.append(", regOperName=").append(regOperName);
        sb.append(", regDate=").append(regDate);
        sb.append(", regTime=").append(regTime);
        sb.append(", modiOperId=").append(modiOperId);
        sb.append(", modiOperName=").append(modiOperName);
        sb.append(", modiDate=").append(modiDate);
        sb.append(", modiTime=").append(modiTime);
        sb.append(", businStatus=").append(businStatus);
        sb.append(", lastStatus=").append(lastStatus);
        sb.append(", operOrg=").append(operOrg);
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
        CustOpenAccountTmp other = (CustOpenAccountTmp) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getVersion() == null ? other.getVersion() == null : this.getVersion().equals(other.getVersion()))
            && (this.getCustName() == null ? other.getCustName() == null : this.getCustName().equals(other.getCustName()))
            && (this.getIdentNo() == null ? other.getIdentNo() == null : this.getIdentNo().equals(other.getIdentNo()))
            && (this.getIdentType() == null ? other.getIdentType() == null : this.getIdentType().equals(other.getIdentType()))
            && (this.getValidDate() == null ? other.getValidDate() == null : this.getValidDate().equals(other.getValidDate()))
            && (this.getBusinLicence() == null ? other.getBusinLicence() == null : this.getBusinLicence().equals(other.getBusinLicence()))
            && (this.getBusinLicenceRegDate() == null ? other.getBusinLicenceRegDate() == null : this.getBusinLicenceRegDate().equals(other.getBusinLicenceRegDate()))
            && (this.getBusinLicenceValidDate() == null ? other.getBusinLicenceValidDate() == null : this.getBusinLicenceValidDate().equals(other.getBusinLicenceValidDate()))
            && (this.getAddress() == null ? other.getAddress() == null : this.getAddress().equals(other.getAddress()))
            && (this.getZipcode() == null ? other.getZipcode() == null : this.getZipcode().equals(other.getZipcode()))
            && (this.getPhone() == null ? other.getPhone() == null : this.getPhone().equals(other.getPhone()))
            && (this.getFax() == null ? other.getFax() == null : this.getFax().equals(other.getFax()))
            && (this.getEmail() == null ? other.getEmail() == null : this.getEmail().equals(other.getEmail()))
            && (this.getBankAcco() == null ? other.getBankAcco() == null : this.getBankAcco().equals(other.getBankAcco()))
            && (this.getBankAccoName() == null ? other.getBankAccoName() == null : this.getBankAccoName().equals(other.getBankAccoName()))
            && (this.getBankNo() == null ? other.getBankNo() == null : this.getBankNo().equals(other.getBankNo()))
            && (this.getBankCityno() == null ? other.getBankCityno() == null : this.getBankCityno().equals(other.getBankCityno()))
            && (this.getBankName() == null ? other.getBankName() == null : this.getBankName().equals(other.getBankName()))
            && (this.getOperName() == null ? other.getOperName() == null : this.getOperName().equals(other.getOperName()))
            && (this.getOperIdenttype() == null ? other.getOperIdenttype() == null : this.getOperIdenttype().equals(other.getOperIdenttype()))
            && (this.getOperIdentno() == null ? other.getOperIdentno() == null : this.getOperIdentno().equals(other.getOperIdentno()))
            && (this.getOperValiddate() == null ? other.getOperValiddate() == null : this.getOperValiddate().equals(other.getOperValiddate()))
            && (this.getOperMobile() == null ? other.getOperMobile() == null : this.getOperMobile().equals(other.getOperMobile()))
            && (this.getOperEmail() == null ? other.getOperEmail() == null : this.getOperEmail().equals(other.getOperEmail()))
            && (this.getOperPhone() == null ? other.getOperPhone() == null : this.getOperPhone().equals(other.getOperPhone()))
            && (this.getOperFaxNo() == null ? other.getOperFaxNo() == null : this.getOperFaxNo().equals(other.getOperFaxNo()))
            && (this.getLawName() == null ? other.getLawName() == null : this.getLawName().equals(other.getLawName()))
            && (this.getLawIdentType() == null ? other.getLawIdentType() == null : this.getLawIdentType().equals(other.getLawIdentType()))
            && (this.getLawIdentNo() == null ? other.getLawIdentNo() == null : this.getLawIdentNo().equals(other.getLawIdentNo()))
            && (this.getLawValidDate() == null ? other.getLawValidDate() == null : this.getLawValidDate().equals(other.getLawValidDate()))
            && (this.getBatchNo() == null ? other.getBatchNo() == null : this.getBatchNo().equals(other.getBatchNo()))
            && (this.getRegOperId() == null ? other.getRegOperId() == null : this.getRegOperId().equals(other.getRegOperId()))
            && (this.getRegOperName() == null ? other.getRegOperName() == null : this.getRegOperName().equals(other.getRegOperName()))
            && (this.getRegDate() == null ? other.getRegDate() == null : this.getRegDate().equals(other.getRegDate()))
            && (this.getRegTime() == null ? other.getRegTime() == null : this.getRegTime().equals(other.getRegTime()))
            && (this.getModiOperId() == null ? other.getModiOperId() == null : this.getModiOperId().equals(other.getModiOperId()))
            && (this.getModiOperName() == null ? other.getModiOperName() == null : this.getModiOperName().equals(other.getModiOperName()))
            && (this.getModiDate() == null ? other.getModiDate() == null : this.getModiDate().equals(other.getModiDate()))
            && (this.getModiTime() == null ? other.getModiTime() == null : this.getModiTime().equals(other.getModiTime()))
            && (this.getBusinStatus() == null ? other.getBusinStatus() == null : this.getBusinStatus().equals(other.getBusinStatus()))
            && (this.getLastStatus() == null ? other.getLastStatus() == null : this.getLastStatus().equals(other.getLastStatus()))
            && (this.getOperOrg() == null ? other.getOperOrg() == null : this.getOperOrg().equals(other.getOperOrg()))
            && (this.getTmpType() == null ? other.getTmpType() == null : this.getTmpType().equals(other.getTmpType()))
            && (this.getTmpOperType() == null ? other.getTmpOperType() == null : this.getTmpOperType().equals(other.getTmpOperType()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        result = prime * result + ((getCustName() == null) ? 0 : getCustName().hashCode());
        result = prime * result + ((getIdentNo() == null) ? 0 : getIdentNo().hashCode());
        result = prime * result + ((getIdentType() == null) ? 0 : getIdentType().hashCode());
        result = prime * result + ((getValidDate() == null) ? 0 : getValidDate().hashCode());
        result = prime * result + ((getBusinLicence() == null) ? 0 : getBusinLicence().hashCode());
        result = prime * result + ((getBusinLicenceRegDate() == null) ? 0 : getBusinLicenceRegDate().hashCode());
        result = prime * result + ((getBusinLicenceValidDate() == null) ? 0 : getBusinLicenceValidDate().hashCode());
        result = prime * result + ((getAddress() == null) ? 0 : getAddress().hashCode());
        result = prime * result + ((getZipcode() == null) ? 0 : getZipcode().hashCode());
        result = prime * result + ((getPhone() == null) ? 0 : getPhone().hashCode());
        result = prime * result + ((getFax() == null) ? 0 : getFax().hashCode());
        result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
        result = prime * result + ((getBankAcco() == null) ? 0 : getBankAcco().hashCode());
        result = prime * result + ((getBankAccoName() == null) ? 0 : getBankAccoName().hashCode());
        result = prime * result + ((getBankNo() == null) ? 0 : getBankNo().hashCode());
        result = prime * result + ((getBankCityno() == null) ? 0 : getBankCityno().hashCode());
        result = prime * result + ((getBankName() == null) ? 0 : getBankName().hashCode());
        result = prime * result + ((getOperName() == null) ? 0 : getOperName().hashCode());
        result = prime * result + ((getOperIdenttype() == null) ? 0 : getOperIdenttype().hashCode());
        result = prime * result + ((getOperIdentno() == null) ? 0 : getOperIdentno().hashCode());
        result = prime * result + ((getOperValiddate() == null) ? 0 : getOperValiddate().hashCode());
        result = prime * result + ((getOperMobile() == null) ? 0 : getOperMobile().hashCode());
        result = prime * result + ((getOperEmail() == null) ? 0 : getOperEmail().hashCode());
        result = prime * result + ((getOperPhone() == null) ? 0 : getOperPhone().hashCode());
        result = prime * result + ((getOperFaxNo() == null) ? 0 : getOperFaxNo().hashCode());
        result = prime * result + ((getLawName() == null) ? 0 : getLawName().hashCode());
        result = prime * result + ((getLawIdentType() == null) ? 0 : getLawIdentType().hashCode());
        result = prime * result + ((getLawIdentNo() == null) ? 0 : getLawIdentNo().hashCode());
        result = prime * result + ((getLawValidDate() == null) ? 0 : getLawValidDate().hashCode());
        result = prime * result + ((getBatchNo() == null) ? 0 : getBatchNo().hashCode());
        result = prime * result + ((getRegOperId() == null) ? 0 : getRegOperId().hashCode());
        result = prime * result + ((getRegOperName() == null) ? 0 : getRegOperName().hashCode());
        result = prime * result + ((getRegDate() == null) ? 0 : getRegDate().hashCode());
        result = prime * result + ((getRegTime() == null) ? 0 : getRegTime().hashCode());
        result = prime * result + ((getModiOperId() == null) ? 0 : getModiOperId().hashCode());
        result = prime * result + ((getModiOperName() == null) ? 0 : getModiOperName().hashCode());
        result = prime * result + ((getModiDate() == null) ? 0 : getModiDate().hashCode());
        result = prime * result + ((getModiTime() == null) ? 0 : getModiTime().hashCode());
        result = prime * result + ((getBusinStatus() == null) ? 0 : getBusinStatus().hashCode());
        result = prime * result + ((getLastStatus() == null) ? 0 : getLastStatus().hashCode());
        result = prime * result + ((getOperOrg() == null) ? 0 : getOperOrg().hashCode());
        result = prime * result + ((getTmpType() == null) ? 0 : getTmpType().hashCode());
        result = prime * result + ((getTmpOperType() == null) ? 0 : getTmpOperType().hashCode());
        return result;
    }
}