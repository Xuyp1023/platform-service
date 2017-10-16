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
@Table(name = "T_SALE_TAINFO")
public class PlatformAgencyInfo implements BetterjrEntity {
    /**
     * TA代码；或者销售上代码
     */
    @Id
    @Column(name = "C_TANO", columnDefinition = "VARCHAR")
    @MetaData(value = "TA代码", comments = "TA代码")
    private String tano;

    /**
     * 简称
     */
    @Column(name = "C_SHORTNAME", columnDefinition = "VARCHAR")
    @MetaData(value = "简称", comments = "简称")
    private String shortName;

    /**
     * 名称
     */
    @Column(name = "C_NAME", columnDefinition = "VARCHAR")
    @MetaData(value = "名称", comments = "名称")
    private String name;

    /**
     * 邮编
     */
    @Column(name = "C_ZIPCODE", columnDefinition = "VARCHAR")
    @MetaData(value = "邮编", comments = "邮编")
    private String zipCode;

    /**
     * 联系人
     */
    @Column(name = "C_CONTACT", columnDefinition = "VARCHAR")
    @MetaData(value = "联系人", comments = "联系人")
    private String contName;

    /**
     * 电话
     */
    @Column(name = "C_PHONE", columnDefinition = "VARCHAR")
    @MetaData(value = "电话", comments = "电话")
    private String phone;

    /**
     * 手机号码
     */
    @Column(name = "C_MOBILENO", columnDefinition = "VARCHAR")
    @MetaData(value = "手机号码", comments = "手机号码")
    private String mobileNo;

    /**
     * 传真号码
     */
    @Column(name = "C_FAXNO", columnDefinition = "VARCHAR")
    @MetaData(value = "传真号码", comments = "传真号码")
    private String faxNo;

    /**
     * email地址
     */
    @Column(name = "C_EMAIL", columnDefinition = "VARCHAR")
    @MetaData(value = "email地址", comments = "email地址")
    private String email;

    /**
     * 通讯地址
     */
    @Column(name = "C_ADDRESS", columnDefinition = "VARCHAR")
    @MetaData(value = "通讯地址", comments = "通讯地址")
    private String address;

    /**
     * 银行编码
     */
    @Column(name = "C_BANKNO", columnDefinition = "VARCHAR")
    @MetaData(value = "银行编码", comments = "银行编码")
    private String bankNo;

    /**
     * 银行帐号
     */
    @Column(name = "C_BANKACCO", columnDefinition = "VARCHAR")
    @MetaData(value = "银行帐号", comments = "银行帐号")
    private String bankAccount;

    /**
     * 银行全称
     */
    @Column(name = "C_BANKNAME", columnDefinition = "VARCHAR")
    @MetaData(value = "银行全称", comments = "银行全称")
    private String bankName;

    /**
     * 基金资金清算机构代码
     */
    @Column(name = "C_CLEAR_ORG", columnDefinition = "VARCHAR")
    @MetaData(value = "基金资金清算机构代码", comments = "基金资金清算机构代码")
    private String clearOrg;

    /**
     * 状态
     */
    @Column(name = "C_STATUS", columnDefinition = "VARCHAR")
    @MetaData(value = "状态", comments = "状态")
    private String status;

    /**
     * 类型0自TA，1中登深圳TA，2中登上海TA
     */
    @Column(name = "C_TYPE", columnDefinition = "VARCHAR")
    @MetaData(value = "类型0自TA", comments = "类型0自TA，1中登深圳TA，2中登上海TA")
    private String taType;

    /**
     * 最后更新日期
     */
    @Column(name = "D_MODIDATE", columnDefinition = "VARCHAR")
    @MetaData(value = "最后更新日期", comments = "最后更新日期")
    private String modiDate;

    /**
     * 开户日期
     */
    @Column(name = "D_REGDATE", columnDefinition = "VARCHAR")
    @MetaData(value = "开户日期", comments = "开户日期")
    private String regDate;

    /**
     * 汇总对账模式 0 双向对帐 1 以基金公司为准 2 以代销商为准
     */
    @Column(name = "C_CHKMODE", columnDefinition = "VARCHAR")
    @MetaData(value = "汇总对账模式  0 双向对帐 1 以基金公司为准   2 以代销商为准", comments = "汇总对账模式  0 双向对帐 1 以基金公司为准   2 以代销商为准")
    private String chkMode;

    /**
     * 业务控制标志(按位来控制)
     */
    @Column(name = "C_BUSINFLAG", columnDefinition = "VARCHAR")
    @MetaData(value = "业务控制标志(按位来控制)", comments = "业务控制标志(按位来控制)")
    private String businFlag;

    /**
     * 清算状态 0 正常 1 清算完毕 2 交收中 3 交收完毕 4 初始化中 (属性字段)
     */
    @Column(name = "C_CLEAR_STATUS", columnDefinition = "VARCHAR")
    @MetaData(value = "清算状态 0 正常 1 清算完毕 2 交收中 3 交收完毕 4 初始化中  (属性字段)", comments = "清算状态 0 正常 1 清算完毕 2 交收中 3 交收完毕 4 初始化中  (属性字段)")
    private String clearStatus;

    /**
     * 交收完毕后份额是否实时可用 0 可用 1 不可用
     */
    @Column(name = "C_VALID", columnDefinition = "VARCHAR")
    @MetaData(value = "交收完毕后份额是否实时可用 0 可用 1 不可用", comments = "交收完毕后份额是否实时可用 0 可用 1 不可用")
    private Boolean shareValid;

    /**
     * 证监会TA代码
     */
    @Column(name = "C_CSRCTANO", columnDefinition = "VARCHAR")
    @MetaData(value = "证监会TA代码", comments = "证监会TA代码")
    private String csrcTano;

    /**
     * 银行户名
     */
    @Column(name = "C_BANKACCONAME", columnDefinition = "VARCHAR")
    @MetaData(value = "银行户名", comments = "银行户名")
    private String bankAcountName;

    /**
     * 错误模板；每个TA 系统对应一套错误模板；相同的TA开发商使用一套模板
     */
    @Column(name = "C_ERRMODE", columnDefinition = "VARCHAR")
    @MetaData(value = "错误模板", comments = "错误模板；每个TA 系统对应一套错误模板；相同的TA开发商使用一套模板")
    private String errMode;

    /**
     * 首次投资判断：1、TA中有份额记录，2、TA中有交易申请，3、基金中有份额记录，4、基金中有交易申请
     */
    @Column(name = "C_FIRST_BASIS", columnDefinition = "VARCHAR")
    @MetaData(value = "首次投资判断：1、TA中有份额记录", comments = "首次投资判断：1、TA中有份额记录，2、TA中有交易申请，3、基金中有份额记录，4、基金中有交易申请")
    private String firstBasis;

    /**
     * 操作员
     */
    @Column(name = "C_OPERNO", columnDefinition = "VARCHAR")
    @MetaData(value = "操作员", comments = "操作员")
    private String operNo;

    /**
     * 直销网点编码定义
     */
    @Column(name = "C_NETNO", columnDefinition = "VARCHAR")
    @MetaData(value = "网点编码", comments = "网点编码")
    private String netNo;

    /**
     * 清算日期
     */
    @Column(name = "D_CLEARDATE", columnDefinition = "VARCHAR")
    @MetaData(value = "清算日期", comments = "清算日期，即该机构已经清算的数据日期，将根据该日期计算存量情况！")
    private String clearDate;

    /**
     * 基金公司销售人代码
     */
    @Column(name = "C_SALE_AGENCYNO", columnDefinition = "VARCHAR")
    @MetaData(value = "基金公司销售人代码", comments = "基金公司销售人代码！")
    private String saleAgencyNo;

    /**
     * 基金公司网址
     */
    @Column(name = "C_URL", columnDefinition = "VARCHAR")
    @MetaData(value = "基金公司网址", comments = "基金公司网址！")
    private String saleUrl;

    /**
     * 直销分中心编码定义
     */
    @Column(name = "C_PAYCENTER", columnDefinition = "VARCHAR")
    @MetaData(value = "分中心", comments = "分中心")
    private String payCenterNo;

    /**
     * 在平台关联的客户编号
     */
    @Column(name = "C_RELA_CUSTNO", columnDefinition = "VARCHAR")
    @MetaData(value = "关联的客户编号", comments = "关联的客户编号")
    private String relaCustNo;

    private static final long serialVersionUID = 1443795724645L;

    public String getTano() {
        return tano;
    }

    public void setTano(String tano) {
        this.tano = tano == null ? null : tano.trim();
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName == null ? null : shortName.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode == null ? null : zipCode.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo == null ? null : mobileNo.trim();
    }

    public String getFaxNo() {
        return faxNo;
    }

    public void setFaxNo(String faxNo) {
        this.faxNo = faxNo == null ? null : faxNo.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo == null ? null : bankNo.trim();
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount == null ? null : bankAccount.trim();
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName == null ? null : bankName.trim();
    }

    public String getClearOrg() {
        return clearOrg;
    }

    public void setClearOrg(String clearOrg) {
        this.clearOrg = clearOrg == null ? null : clearOrg.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getTaType() {
        return taType;
    }

    public void setTaType(String taType) {
        this.taType = taType == null ? null : taType.trim();
    }

    public String getModiDate() {
        return modiDate;
    }

    public void setModiDate(String modiDate) {
        this.modiDate = modiDate == null ? null : modiDate.trim();
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate == null ? null : regDate.trim();
    }

    public String getChkMode() {
        return chkMode;
    }

    public void setChkMode(String chkMode) {
        this.chkMode = chkMode == null ? null : chkMode.trim();
    }

    public String getBusinFlag() {
        return businFlag;
    }

    public void setBusinFlag(String businFlag) {
        this.businFlag = businFlag == null ? null : businFlag.trim();
    }

    public String getClearStatus() {
        return clearStatus;
    }

    public void setClearStatus(String clearStatus) {
        this.clearStatus = clearStatus == null ? null : clearStatus.trim();
    }

    public Boolean getShareValid() {
        return shareValid;
    }

    public void setShareValid(Boolean shareValid) {
        this.shareValid = shareValid;
    }

    public String getCsrcTano() {
        return csrcTano;
    }

    public void setCsrcTano(String csrcTano) {
        this.csrcTano = csrcTano == null ? null : csrcTano.trim();
    }

    public String getBankAcountName() {
        return bankAcountName;
    }

    public void setBankAcountName(String bankAcountName) {
        this.bankAcountName = bankAcountName == null ? null : bankAcountName.trim();
    }

    public String getErrMode() {
        return errMode;
    }

    public void setErrMode(String errMode) {
        this.errMode = errMode == null ? null : errMode.trim();
    }

    public String getFirstBasis() {
        return firstBasis;
    }

    public void setFirstBasis(String firstBasis) {
        this.firstBasis = firstBasis == null ? null : firstBasis.trim();
    }

    public String getOperNo() {
        return operNo;
    }

    public void setOperNo(String operNo) {
        this.operNo = operNo == null ? null : operNo.trim();
    }

    public String getNetNo() {
        return netNo;
    }

    public void setNetNo(String netNo) {
        this.netNo = netNo == null ? null : netNo.trim();
    }

    public String getClearDate() {
        return this.clearDate;
    }

    public void setClearDate(String anClearDate) {
        this.clearDate = anClearDate;
    }

    public String getPayCenterNo() {
        return this.payCenterNo;
    }

    public void setPayCenterNo(String anPayCenterNo) {
        this.payCenterNo = anPayCenterNo;
    }

    public String getSaleAgencyNo() {
        return this.saleAgencyNo;
    }

    public void setSaleAgencyNo(String anSaleAgencyNo) {
        this.saleAgencyNo = anSaleAgencyNo;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", tano=").append(tano);
        sb.append(", shortName=").append(shortName);
        sb.append(", name=").append(name);
        sb.append(", zipCode=").append(zipCode);
        sb.append(", contName=").append(contName);
        sb.append(", phone=").append(phone);
        sb.append(", mobileNo=").append(mobileNo);
        sb.append(", faxNo=").append(faxNo);
        sb.append(", email=").append(email);
        sb.append(", address=").append(address);
        sb.append(", bankNo=").append(bankNo);
        sb.append(", bankAccount=").append(bankAccount);
        sb.append(", bankName=").append(bankName);
        sb.append(", clearOrg=").append(clearOrg);
        sb.append(", status=").append(status);
        sb.append(", taType=").append(taType);
        sb.append(", modiDate=").append(modiDate);
        sb.append(", regDate=").append(regDate);
        sb.append(", chkMode=").append(chkMode);
        sb.append(", businFlag=").append(businFlag);
        sb.append(", clearStatus=").append(clearStatus);
        sb.append(", shareValid=").append(shareValid);
        sb.append(", csrcTano=").append(csrcTano);
        sb.append(", bankAcountName=").append(bankAcountName);
        sb.append(", errMode=").append(errMode);
        sb.append(", firstBasis=").append(firstBasis);
        sb.append(", operNo=").append(operNo);
        sb.append(", netNo=").append(netNo);
        sb.append(", clearDate=").append(clearDate);
        sb.append(", payCenterNo=").append(payCenterNo);
        sb.append(", saleAgencyNo=").append(saleAgencyNo);
        sb.append("]");
        return sb.toString();
    }

    public String getContName() {
        return this.contName;
    }

    public void setContName(String anContName) {
        this.contName = anContName;
    }

    public String getSaleUrl() {
        return this.saleUrl;
    }

    public void setSaleUrl(String anSaleUrl) {
        this.saleUrl = anSaleUrl;
    }

    public String getRelaCustNo() {
        return this.relaCustNo;
    }

    public void setRelaCustNo(String anRelaCustNo) {
        this.relaCustNo = anRelaCustNo;
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
        PlatformAgencyInfo other = (PlatformAgencyInfo) that;
        return (this.getTano() == null ? other.getTano() == null : this.getTano().equals(other.getTano()))
                && (this.getShortName() == null ? other.getShortName() == null
                        : this.getShortName().equals(other.getShortName()))
                && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
                && (this.getZipCode() == null ? other.getZipCode() == null
                        : this.getZipCode().equals(other.getZipCode()))
                && (this.getContName() == null ? other.getContName() == null
                        : this.getContName().equals(other.getContName()))
                && (this.getPhone() == null ? other.getPhone() == null : this.getPhone().equals(other.getPhone()))
                && (this.getMobileNo() == null ? other.getMobileNo() == null
                        : this.getMobileNo().equals(other.getMobileNo()))
                && (this.getFaxNo() == null ? other.getFaxNo() == null : this.getFaxNo().equals(other.getFaxNo()))
                && (this.getEmail() == null ? other.getEmail() == null : this.getEmail().equals(other.getEmail()))
                && (this.getAddress() == null ? other.getAddress() == null
                        : this.getAddress().equals(other.getAddress()))
                && (this.getBankNo() == null ? other.getBankNo() == null : this.getBankNo().equals(other.getBankNo()))
                && (this.getBankAccount() == null ? other.getBankAccount() == null
                        : this.getBankAccount().equals(other.getBankAccount()))
                && (this.getBankName() == null ? other.getBankName() == null
                        : this.getBankName().equals(other.getBankName()))
                && (this.getClearOrg() == null ? other.getClearOrg() == null
                        : this.getClearOrg().equals(other.getClearOrg()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
                && (this.getTaType() == null ? other.getTaType() == null : this.getTaType().equals(other.getTaType()))
                && (this.getModiDate() == null ? other.getModiDate() == null
                        : this.getModiDate().equals(other.getModiDate()))
                && (this.getRegDate() == null ? other.getRegDate() == null
                        : this.getRegDate().equals(other.getRegDate()))
                && (this.getChkMode() == null ? other.getChkMode() == null
                        : this.getChkMode().equals(other.getChkMode()))
                && (this.getBusinFlag() == null ? other.getBusinFlag() == null
                        : this.getBusinFlag().equals(other.getBusinFlag()))
                && (this.getClearStatus() == null ? other.getClearStatus() == null
                        : this.getClearStatus().equals(other.getClearStatus()))
                && (this.getShareValid() == null ? other.getShareValid() == null
                        : this.getShareValid().equals(other.getShareValid()))
                && (this.getCsrcTano() == null ? other.getCsrcTano() == null
                        : this.getCsrcTano().equals(other.getCsrcTano()))
                && (this.getBankAcountName() == null ? other.getBankAcountName() == null
                        : this.getBankAcountName().equals(other.getBankAcountName()))
                && (this.getErrMode() == null ? other.getErrMode() == null
                        : this.getErrMode().equals(other.getErrMode()))
                && (this.getFirstBasis() == null ? other.getFirstBasis() == null
                        : this.getFirstBasis().equals(other.getFirstBasis()))
                && (this.getOperNo() == null ? other.getOperNo() == null : this.getOperNo().equals(other.getOperNo()))
                && (this.getNetNo() == null ? other.getNetNo() == null : this.getNetNo().equals(other.getNetNo()))
                && (this.getClearDate() == null ? other.getClearDate() == null
                        : this.getClearDate().equals(other.getClearDate()))
                && (this.getPayCenterNo() == null ? other.getPayCenterNo() == null
                        : this.getPayCenterNo().equals(other.getPayCenterNo()))
                && (this.getRelaCustNo() == null ? other.getRelaCustNo() == null
                        : this.getRelaCustNo().equals(other.getRelaCustNo()))
                && (this.getSaleAgencyNo() == null ? other.getSaleAgencyNo() == null
                        : this.getSaleAgencyNo().equals(other.getSaleAgencyNo()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getTano() == null) ? 0 : getTano().hashCode());
        result = prime * result + ((getShortName() == null) ? 0 : getShortName().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getZipCode() == null) ? 0 : getZipCode().hashCode());
        result = prime * result + ((getContName() == null) ? 0 : getContName().hashCode());
        result = prime * result + ((getPhone() == null) ? 0 : getPhone().hashCode());
        result = prime * result + ((getMobileNo() == null) ? 0 : getMobileNo().hashCode());
        result = prime * result + ((getFaxNo() == null) ? 0 : getFaxNo().hashCode());
        result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
        result = prime * result + ((getAddress() == null) ? 0 : getAddress().hashCode());
        result = prime * result + ((getBankNo() == null) ? 0 : getBankNo().hashCode());
        result = prime * result + ((getBankAccount() == null) ? 0 : getBankAccount().hashCode());
        result = prime * result + ((getBankName() == null) ? 0 : getBankName().hashCode());
        result = prime * result + ((getClearOrg() == null) ? 0 : getClearOrg().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getTaType() == null) ? 0 : getTaType().hashCode());
        result = prime * result + ((getModiDate() == null) ? 0 : getModiDate().hashCode());
        result = prime * result + ((getRegDate() == null) ? 0 : getRegDate().hashCode());
        result = prime * result + ((getChkMode() == null) ? 0 : getChkMode().hashCode());
        result = prime * result + ((getBusinFlag() == null) ? 0 : getBusinFlag().hashCode());
        result = prime * result + ((getClearStatus() == null) ? 0 : getClearStatus().hashCode());
        result = prime * result + ((getShareValid() == null) ? 0 : getShareValid().hashCode());
        result = prime * result + ((getCsrcTano() == null) ? 0 : getCsrcTano().hashCode());
        result = prime * result + ((getBankAcountName() == null) ? 0 : getBankAcountName().hashCode());
        result = prime * result + ((getErrMode() == null) ? 0 : getErrMode().hashCode());
        result = prime * result + ((getFirstBasis() == null) ? 0 : getFirstBasis().hashCode());
        result = prime * result + ((getOperNo() == null) ? 0 : getOperNo().hashCode());
        result = prime * result + ((getNetNo() == null) ? 0 : getNetNo().hashCode());
        result = prime * result + ((getClearDate() == null) ? 0 : getClearDate().hashCode());
        result = prime * result + ((getPayCenterNo() == null) ? 0 : getPayCenterNo().hashCode());
        result = prime * result + ((getSaleAgencyNo() == null) ? 0 : getSaleAgencyNo().hashCode());
        result = prime * result + ((getRelaCustNo() == null) ? 0 : getRelaCustNo().hashCode());
        return result;
    }
}