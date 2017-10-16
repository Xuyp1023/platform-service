package com.betterjr.modules.customer.entity;

import java.math.BigDecimal;

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
import com.fasterxml.jackson.annotation.JsonIgnore;

@Access(AccessType.FIELD)
@Entity
@Table(name = "t_cust_bankflow")
public class CustBankFlow implements BetterjrEntity {
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
     * 银行流水号
     */
    @Column(name = "C_REQUESTNO", columnDefinition = "VARCHAR")
    @MetaData(value = "银行流水号", comments = "银行流水号")
    private String requestNo;

    /**
     * 银行账户
     */
    @Column(name = "C_BANK_ACCO", columnDefinition = "VARCHAR")
    @MetaData(value = "银行账户", comments = "银行账户")
    private String bankAcco;

    /**
     * 银行全称
     */
    @Column(name = "C_BANK_NAME", columnDefinition = "VARCHAR")
    @MetaData(value = "银行全称", comments = "银行全称")
    private String bankName;

    /**
     * 凭证代码
     */
    @Column(name = "C_CERT_CODE", columnDefinition = "VARCHAR")
    @MetaData(value = "凭证代码", comments = "凭证代码")
    private String certCode;

    /**
     * 币种 CNY、HKD、USD、GBP、AED
     */
    @Column(name = "C_CURRENCY", columnDefinition = "VARCHAR")
    @MetaData(value = "币种 CNY、HKD、USD、GBP、AED", comments = "币种 CNY、HKD、USD、GBP、AED")
    private String currency;

    /**
     * 现/转(支付方式0现金 1转账)
     */
    @Column(name = "C_PAY_TYPE", columnDefinition = "VARCHAR")
    @MetaData(value = "现/转(支付方式0现金 1转账)", comments = "现/转(支付方式0现金 1转账)")
    private String payType;

    /**
     * 借方金额
     */
    @Column(name = "F_DEBTOR_AMOUNT", columnDefinition = "DOUBLE")
    @MetaData(value = "借方金额", comments = "借方金额")
    private BigDecimal debtorAmount;

    /**
     * 贷方金额
     */
    @Column(name = "F_LENDER_AMOUNT", columnDefinition = "DOUBLE")
    @MetaData(value = "贷方金额", comments = "贷方金额")
    private BigDecimal lenderAmount;

    /**
     * 账户余额
     */
    @Column(name = "F_BALANCE", columnDefinition = "DOUBLE")
    @MetaData(value = "账户余额", comments = "账户余额")
    private BigDecimal balance;

    /**
     * 摘要
     */
    @Column(name = "C_DESCRIPTION", columnDefinition = "VARCHAR")
    @MetaData(value = "摘要", comments = "摘要")
    private String description;

    /**
     * 对方账号
     */
    @Column(name = "C_TARGET_BANK_ACCO", columnDefinition = "VARCHAR")
    @MetaData(value = "对方账号", comments = "对方账号")
    private String targetBankAcco;

    /**
     * 对方户名
     */
    @Column(name = "C_TARGET_BANK_ACCONAME", columnDefinition = "VARCHAR")
    @MetaData(value = "对方户名", comments = "对方户名")
    private String targetBankAccoName;

    /**
     * 对方银行
     */
    @Column(name = "C_TARGET_BANK_NAME", columnDefinition = "VARCHAR")
    @MetaData(value = "对方银行", comments = "对方银行")
    private String targetBankName;

    /**
     * 交易日期
     */
    @Column(name = "D_DEAL_DATE", columnDefinition = "VARCHAR")
    @MetaData(value = "交易日期", comments = "交易日期")
    private String dealDate;

    /**
     * 用途
     */
    @Column(name = "C_PURPOSE", columnDefinition = "VARCHAR")
    @MetaData(value = "用途", comments = "用途")
    private String purpose;

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

    @Column(name = "C_LAST_STATUS", columnDefinition = "CHAR")
    @MetaData(value = "", comments = "")
    private String lastStatus;

    /**
     * 客户编号
     */
    @Column(name = "L_CUSTNO", columnDefinition = "INTEGER")
    @MetaData(value = "客户编号", comments = "客户编号")
    private Long custNo;

    private static final long serialVersionUID = 1468812783840L;

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

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo == null ? null : requestNo.trim();
    }

    public String getBankAcco() {
        return bankAcco;
    }

    public void setBankAcco(String bankAcco) {
        this.bankAcco = bankAcco == null ? null : bankAcco.trim();
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName == null ? null : bankName.trim();
    }

    public String getCertCode() {
        return certCode;
    }

    public void setCertCode(String certCode) {
        this.certCode = certCode == null ? null : certCode.trim();
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency == null ? null : currency.trim();
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType == null ? null : payType.trim();
    }

    public BigDecimal getDebtorAmount() {
        return debtorAmount;
    }

    public void setDebtorAmount(BigDecimal debtorAmount) {
        this.debtorAmount = debtorAmount;
    }

    public BigDecimal getLenderAmount() {
        return lenderAmount;
    }

    public void setLenderAmount(BigDecimal lenderAmount) {
        this.lenderAmount = lenderAmount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getTargetBankAcco() {
        return targetBankAcco;
    }

    public void setTargetBankAcco(String targetBankAcco) {
        this.targetBankAcco = targetBankAcco == null ? null : targetBankAcco.trim();
    }

    public String getTargetBankAccoName() {
        return targetBankAccoName;
    }

    public void setTargetBankAccoName(String targetBankAccoName) {
        this.targetBankAccoName = targetBankAccoName == null ? null : targetBankAccoName.trim();
    }

    public String getTargetBankName() {
        return targetBankName;
    }

    public void setTargetBankName(String targetBankName) {
        this.targetBankName = targetBankName == null ? null : targetBankName.trim();
    }

    public String getDealDate() {
        return dealDate;
    }

    public void setDealDate(String dealDate) {
        this.dealDate = dealDate == null ? null : dealDate.trim();
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose == null ? null : purpose.trim();
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", version=").append(version);
        sb.append(", requestNo=").append(requestNo);
        sb.append(", bankAcco=").append(bankAcco);
        sb.append(", bankName=").append(bankName);
        sb.append(", certCode=").append(certCode);
        sb.append(", currency=").append(currency);
        sb.append(", payType=").append(payType);
        sb.append(", debtorAmount=").append(debtorAmount);
        sb.append(", lenderAmount=").append(lenderAmount);
        sb.append(", balance=").append(balance);
        sb.append(", description=").append(description);
        sb.append(", targetBankAcco=").append(targetBankAcco);
        sb.append(", targetBankAccoName=").append(targetBankAccoName);
        sb.append(", targetBankName=").append(targetBankName);
        sb.append(", dealDate=").append(dealDate);
        sb.append(", purpose=").append(purpose);
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
        CustBankFlow other = (CustBankFlow) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getVersion() == null ? other.getVersion() == null
                        : this.getVersion().equals(other.getVersion()))
                && (this.getRequestNo() == null ? other.getRequestNo() == null
                        : this.getRequestNo().equals(other.getRequestNo()))
                && (this.getBankAcco() == null ? other.getBankAcco() == null
                        : this.getBankAcco().equals(other.getBankAcco()))
                && (this.getBankName() == null ? other.getBankName() == null
                        : this.getBankName().equals(other.getBankName()))
                && (this.getCertCode() == null ? other.getCertCode() == null
                        : this.getCertCode().equals(other.getCertCode()))
                && (this.getCurrency() == null ? other.getCurrency() == null
                        : this.getCurrency().equals(other.getCurrency()))
                && (this.getPayType() == null ? other.getPayType() == null
                        : this.getPayType().equals(other.getPayType()))
                && (this.getDebtorAmount() == null ? other.getDebtorAmount() == null
                        : this.getDebtorAmount().equals(other.getDebtorAmount()))
                && (this.getLenderAmount() == null ? other.getLenderAmount() == null
                        : this.getLenderAmount().equals(other.getLenderAmount()))
                && (this.getBalance() == null ? other.getBalance() == null
                        : this.getBalance().equals(other.getBalance()))
                && (this.getDescription() == null ? other.getDescription() == null
                        : this.getDescription().equals(other.getDescription()))
                && (this.getTargetBankAcco() == null ? other.getTargetBankAcco() == null
                        : this.getTargetBankAcco().equals(other.getTargetBankAcco()))
                && (this.getTargetBankAccoName() == null ? other.getTargetBankAccoName() == null
                        : this.getTargetBankAccoName().equals(other.getTargetBankAccoName()))
                && (this.getTargetBankName() == null ? other.getTargetBankName() == null
                        : this.getTargetBankName().equals(other.getTargetBankName()))
                && (this.getDealDate() == null ? other.getDealDate() == null
                        : this.getDealDate().equals(other.getDealDate()))
                && (this.getPurpose() == null ? other.getPurpose() == null
                        : this.getPurpose().equals(other.getPurpose()))
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
                && (this.getCustNo() == null ? other.getCustNo() == null : this.getCustNo().equals(other.getCustNo()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        result = prime * result + ((getRequestNo() == null) ? 0 : getRequestNo().hashCode());
        result = prime * result + ((getBankAcco() == null) ? 0 : getBankAcco().hashCode());
        result = prime * result + ((getBankName() == null) ? 0 : getBankName().hashCode());
        result = prime * result + ((getCertCode() == null) ? 0 : getCertCode().hashCode());
        result = prime * result + ((getCurrency() == null) ? 0 : getCurrency().hashCode());
        result = prime * result + ((getPayType() == null) ? 0 : getPayType().hashCode());
        result = prime * result + ((getDebtorAmount() == null) ? 0 : getDebtorAmount().hashCode());
        result = prime * result + ((getLenderAmount() == null) ? 0 : getLenderAmount().hashCode());
        result = prime * result + ((getBalance() == null) ? 0 : getBalance().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getTargetBankAcco() == null) ? 0 : getTargetBankAcco().hashCode());
        result = prime * result + ((getTargetBankAccoName() == null) ? 0 : getTargetBankAccoName().hashCode());
        result = prime * result + ((getTargetBankName() == null) ? 0 : getTargetBankName().hashCode());
        result = prime * result + ((getDealDate() == null) ? 0 : getDealDate().hashCode());
        result = prime * result + ((getPurpose() == null) ? 0 : getPurpose().hashCode());
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
        return result;
    }

    public void initAddValue() {
        this.id = SerialGenerator.getLongValue("CustBankFlow.id");

        this.regOperId = UserUtils.getOperatorInfo().getId();
        this.regOperName = UserUtils.getOperatorInfo().getName();
        this.regDate = BetterDateUtils.getNumDate();
        this.regTime = BetterDateUtils.getNumTime();

        this.modiOperId = UserUtils.getOperatorInfo().getId();
        this.modiOperName = UserUtils.getOperatorInfo().getName();
        this.modiDate = BetterDateUtils.getNumDate();
        this.modiTime = BetterDateUtils.getNumTime();

        this.operOrg = UserUtils.getOperatorInfo().getOperOrg();
        this.businStatus = "0";
    }

    public void initModifyValue(final CustBankFlow anCustBankFlow) {
        this.id = anCustBankFlow.getId();
        /*
        this.regOperId = anCustMechBaseTmp.getRegOperId();
        this.regOperName = anCustMechBaseTmp.getRegOperName();
        this.regDate = anCustMechBaseTmp.getRegDate();
        this.regTime = anCustMechBaseTmp.getRegTime();
        */
        this.modiOperId = UserUtils.getOperatorInfo().getId();
        this.modiOperName = UserUtils.getOperatorInfo().getName();
        this.modiDate = BetterDateUtils.getNumDate();
        this.modiTime = BetterDateUtils.getNumTime();

        this.businStatus = anCustBankFlow.getBusinStatus();
        this.operOrg = anCustBankFlow.getOperOrg();
    }
}