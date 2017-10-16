package com.betterjr.modules.wechat.entity;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

import com.betterjr.common.annotation.MetaData;
import com.betterjr.common.entity.BetterjrEntity;
import com.betterjr.common.mapper.BeanMapper;
import com.betterjr.common.selectkey.SerialGenerator;
import com.betterjr.common.utils.AreaUtils;
import com.betterjr.common.utils.BetterDateUtils;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.modules.account.entity.SaleAccoRequestInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Access(AccessType.FIELD)
@Entity
@Table(name = "t_cust_mech_bankacco")
public class SaleAccoBankInfo implements BetterjrEntity {
    /**
     * 资金账户
     */
    @Id
    @Column(name = "ID", columnDefinition = "INTEGER")
    @MetaData(value = "资金账户", comments = "资金账户")
    private Long moneyAccount;

    /**
     * 客户编号
     */
    @Column(name = "L_CUSTNO", columnDefinition = "INTEGER")
    @MetaData(value = "客户编号", comments = "客户编号")
    private Long custNo;

    /**
     * 交易账户
     */
    @Column(name = "C_TRADE_ACCO", columnDefinition = "VARCHAR")
    @MetaData(value = "交易账户", comments = "交易账户")
    private String tradeAccount;

    /**
     * 银行编码
     */
    @Column(name = "C_BANK_NO", columnDefinition = "VARCHAR")
    @MetaData(value = "银行编码", comments = "银行编码")
    private String bankNo;

    /**
     * 银行全称
     */
    @Column(name = "C_BANK_NAME", columnDefinition = "VARCHAR")
    @MetaData(value = "银行全称", comments = "银行全称")
    private String bankName;

    /**
     * 银行账户
     */
    @Column(name = "C_BANK_ACCO", columnDefinition = "VARCHAR")
    @MetaData(value = "银行账户", comments = "银行账户")
    private String bankAccount;

    /**
     * 银行户名；个人户的信息必须和客户信息一致，机构户可以不一致
     */
    @Column(name = "C_BANK_ACCONAME", columnDefinition = "VARCHAR")
    @MetaData(value = "银行户名", comments = "银行户名；个人户的信息必须和客户信息一致，机构户可以不一致")
    private String bankAcountName;

    /**
     * 联行号
     */
    @Column(name = "C_BANK_BRANCH", columnDefinition = "VARCHAR")
    @MetaData(value = "联行号", comments = "联行号")
    private String branchBank;

    /**
     * 账户状态（0 正常 1 销户中 2 销户，3冻结）
     */
    @Column(name = "C_BUSIN_STATUS", columnDefinition = "VARCHAR")
    @MetaData(value = "账户状态（0 正常  1 销户中  2 销户", comments = "账户状态（0 正常  1 销户中  2 销户，3冻结）")
    private String status;

    /**
     * 账户上一状态（0 正常 1 销户中 2 销户，3冻结）
     */
    @Column(name = "C_LAST_STATUS", columnDefinition = "VARCHAR")
    @MetaData(value = "账户上一状态（0 正常  1 销户中  2 销户", comments = "账户上一状态（0 正常  1 销户中  2 销户，3冻结）")
    private String lastStatus;

    /**
     * 网点编码
     */
    @Column(name = "C_NET_NO", columnDefinition = "VARCHAR")
    @MetaData(value = "网点编码", comments = "网点编码")
    private String netNo;

    /**
     * 分中心
     */
    @Column(name = "C_PAY_CENTER", columnDefinition = "VARCHAR")
    @MetaData(value = "分中心", comments = "分中心")
    private String payCenterNo;

    /**
     * 城市地区代码
     */
    @Column(name = "C_CITYNO", columnDefinition = "VARCHAR")
    @MetaData(value = "城市地区代码", comments = "城市地区代码")
    private String cityNo;

    /**
     * 冻结截止日期
     */
    @Column(name = "D_END_FROZEN_DATE", columnDefinition = "VARCHAR")
    @MetaData(value = "冻结截止日期", comments = "冻结截止日期")
    private String endFrozenDate;

    /**
     * 签约协议号
     */
    @Column(name = "C_CONTRACT_NO", columnDefinition = "VARCHAR")
    @MetaData(value = "签约协议号", comments = "签约协议号")
    private String contractNo;

    /**
     * 鉴权标志(0 未鉴权，1 已鉴权)
     */
    @Column(name = "C_AUTH_STATUS", columnDefinition = "VARCHAR")
    @MetaData(value = "鉴权标志(0 未鉴权", comments = "鉴权标志(0 未鉴权，1 已鉴权)")
    private String authStatus;

    /**
     * 签约状态
     */
    @Column(name = "C_SIGN_STATUS", columnDefinition = "VARCHAR")
    @MetaData(value = "签约状态", comments = "签约状态")
    private Boolean signStatus;

    /**
     * 客户在银行证件类型
     */
    @Column(name = "C_IDENTTYPE", columnDefinition = "VARCHAR")
    @MetaData(value = "客户在银行证件类型", comments = "客户在银行证件类型")
    private String identType;

    /**
     * 客户在银行的证件号码，主要解决15位18位身份证的问题
     */
    @Column(name = "C_IDENTNO", columnDefinition = "VARCHAR")
    @MetaData(value = "客户在银行的证件号码", comments = "客户在银行的证件号码，主要解决15位18位身份证的问题")
    private String identNo;

    /**
     * 特殊控制标志
     */
    @Column(name = "C_FLAG", columnDefinition = "VARCHAR")
    @MetaData(value = "特殊控制标志", comments = "特殊控制标志")
    private String flag;

    /**
     * 旧卡信息
     */
    @Column(name = "C_BAKUP_ACCO", columnDefinition = "VARCHAR")
    @MetaData(value = "旧卡信息", comments = "旧卡信息")
    private String backupAccount;

    /**
     * 开户日期
     */
    @Column(name = "D_REG_DATE", columnDefinition = "VARCHAR")
    @MetaData(value = "开户日期", comments = "开户日期")
    private String regDate;

    /**
     * 最后更新日期
     */
    @Column(name = "D_MODI_DATE", columnDefinition = "VARCHAR")
    @MetaData(value = "最后更新日期", comments = "最后更新日期")
    private String modiDate;

    /**
     * 区/县名称
     */
    @Column(name = "C_COUNTY_NAME", columnDefinition = "VARCHAR")
    @MetaData(value = "区/县名称", comments = "区/县名称")
    private String countyName;

    /**
     * 操作机构
     */
    @JsonIgnore
    @Column(name = "C_OPERORG", columnDefinition = "VARCHAR")
    @MetaData(value = "操作机构", comments = "操作机构")
    private String operOrg;

    /**
     * 城市地区名称
     */
    @Column(name = "C_CITY_NAME", columnDefinition = "VARCHAR")
    @MetaData(value = "城市地区名称", comments = "城市地区名称")
    private String cityName;

    /**
     * 修改时间
     */
    @JsonIgnore
    @Column(name = "T_MODI_TIME", columnDefinition = "VARCHAR")
    @MetaData(value = "修改时间", comments = "修改时间")
    private String modiTime;

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
    private static final long serialVersionUID = 1440724942692L;

    public Long getMoneyAccount() {
        return this.moneyAccount;
    }

    public void setMoneyAccount(Long anMoneyAccount) {
        this.moneyAccount = anMoneyAccount;
    }

    public Long getCustNo() {
        return custNo;
    }

    public void setCustNo(Long custNo) {
        this.custNo = custNo;
    }

    public String getTradeAccount() {
        return tradeAccount;
    }

    public void setTradeAccount(String tradeAccount) {
        this.tradeAccount = tradeAccount == null ? null : tradeAccount.trim();
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo == null ? null : bankNo.trim();
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName == null ? null : bankName.trim();
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount == null ? null : bankAccount.trim();
    }

    public String getBankAcountName() {
        return bankAcountName;
    }

    public void setBankAcountName(String bankAcountName) {
        this.bankAcountName = bankAcountName == null ? null : bankAcountName.trim();
    }

    public String getBranchBank() {
        return branchBank;
    }

    public void setBranchBank(String branchBank) {
        this.branchBank = branchBank == null ? null : branchBank.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(String lastStatus) {
        this.lastStatus = lastStatus == null ? null : lastStatus.trim();
    }

    public String getNetNo() {
        return netNo;
    }

    public void setNetNo(String netNo) {
        this.netNo = netNo == null ? null : netNo.trim();
    }

    public String getPayCenterNo() {
        return payCenterNo;
    }

    public void setPayCenterNo(String payCenterNo) {
        this.payCenterNo = payCenterNo == null ? null : payCenterNo.trim();
    }

    public String getCityNo() {
        return cityNo;
    }

    public void setCityNo(String cityNo) {
        this.cityNo = cityNo == null ? null : cityNo.trim();
    }

    public String getEndFrozenDate() {
        return endFrozenDate;
    }

    public void setEndFrozenDate(String endFrozenDate) {
        this.endFrozenDate = endFrozenDate == null ? null : endFrozenDate.trim();
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo == null ? null : contractNo.trim();
    }

    public String getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(String authStatus) {
        this.authStatus = authStatus == null ? null : authStatus.trim();
    }

    public Boolean getSignStatus() {
        return signStatus;
    }

    public void setSignStatus(Boolean signStatus) {
        this.signStatus = signStatus;
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

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag == null ? null : flag.trim();
    }

    public String getBackupAccount() {
        return backupAccount;
    }

    public void setBackupAccount(String backupAccount) {
        this.backupAccount = backupAccount == null ? null : backupAccount.trim();
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate == null ? null : regDate.trim();
    }

    public String getModiDate() {
        return modiDate;
    }

    public void setModiDate(String modiDate) {
        this.modiDate = modiDate == null ? null : modiDate.trim();
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName == null ? null : countyName.trim();
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName == null ? null : cityName.trim();
    }

    public String getModiTime() {
        return this.modiTime;
    }

    public void setModiTime(String anModiTime) {
        this.modiTime = anModiTime;
    }

    public String getRegTime() {
        return this.regTime;
    }

    public void setRegTime(String anRegTime) {
        this.regTime = anRegTime;
    }

    public Long getModiOperId() {
        return this.modiOperId;
    }

    public void setModiOperId(Long anModiOperId) {
        this.modiOperId = anModiOperId;
    }

    public String getModiOperName() {
        return this.modiOperName;
    }

    public void setModiOperName(String anModiOperName) {
        this.modiOperName = anModiOperName;
    }

    public Long getRegOperId() {
        return this.regOperId;
    }

    public void setRegOperId(Long anRegOperId) {
        this.regOperId = anRegOperId;
    }

    public String getRegOperName() {
        return this.regOperName;
    }

    public void setRegOperName(String anRegOperName) {
        this.regOperName = anRegOperName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", moneyAccount=").append(moneyAccount);
        sb.append(", custNo=").append(custNo);
        sb.append(", tradeAccount=").append(tradeAccount);
        sb.append(", bankNo=").append(bankNo);
        sb.append(", bankName=").append(bankName);
        sb.append(", bankAccount=").append(bankAccount);
        sb.append(", bankAcountName=").append(bankAcountName);
        sb.append(", branchBank=").append(branchBank);
        sb.append(", status=").append(status);
        sb.append(", lastStatus=").append(lastStatus);
        sb.append(", netNo=").append(netNo);
        sb.append(", payCenterNo=").append(payCenterNo);
        sb.append(", cityNo=").append(cityNo);
        sb.append(", endFrozenDate=").append(endFrozenDate);
        sb.append(", contractNo=").append(contractNo);
        sb.append(", authStatus=").append(authStatus);
        sb.append(", signStatus=").append(signStatus);
        sb.append(", identType=").append(identType);
        sb.append(", identNo=").append(identNo);
        sb.append(", flag=").append(flag);
        sb.append(", backupAccount=").append(backupAccount);
        sb.append(", regDate=").append(regDate);
        sb.append(", modiDate=").append(modiDate);
        sb.append(", countyName=").append(countyName);
        sb.append(", cityName=").append(cityName);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

    public String getOperOrg() {
        return this.operOrg;
    }

    public void setOperOrg(String anOperOrg) {
        this.operOrg = anOperOrg;
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
        SaleAccoBankInfo other = (SaleAccoBankInfo) that;
        return (this.getMoneyAccount() == null ? other.getMoneyAccount() == null
                : this.getMoneyAccount().equals(other.getMoneyAccount()))
                && (this.getCustNo() == null ? other.getCustNo() == null : this.getCustNo().equals(other.getCustNo()))
                && (this.getTradeAccount() == null ? other.getTradeAccount() == null
                        : this.getTradeAccount().equals(other.getTradeAccount()))
                && (this.getBankNo() == null ? other.getBankNo() == null : this.getBankNo().equals(other.getBankNo()))
                && (this.getBankName() == null ? other.getBankName() == null
                        : this.getBankName().equals(other.getBankName()))
                && (this.getBankAccount() == null ? other.getBankAccount() == null
                        : this.getBankAccount().equals(other.getBankAccount()))
                && (this.getBankAcountName() == null ? other.getBankAcountName() == null
                        : this.getBankAcountName().equals(other.getBankAcountName()))
                && (this.getBranchBank() == null ? other.getBranchBank() == null
                        : this.getBranchBank().equals(other.getBranchBank()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
                && (this.getLastStatus() == null ? other.getLastStatus() == null
                        : this.getLastStatus().equals(other.getLastStatus()))
                && (this.getNetNo() == null ? other.getNetNo() == null : this.getNetNo().equals(other.getNetNo()))
                && (this.getPayCenterNo() == null ? other.getPayCenterNo() == null
                        : this.getPayCenterNo().equals(other.getPayCenterNo()))
                && (this.getCityNo() == null ? other.getCityNo() == null : this.getCityNo().equals(other.getCityNo()))
                && (this.getEndFrozenDate() == null ? other.getEndFrozenDate() == null
                        : this.getEndFrozenDate().equals(other.getEndFrozenDate()))
                && (this.getContractNo() == null ? other.getContractNo() == null
                        : this.getContractNo().equals(other.getContractNo()))
                && (this.getAuthStatus() == null ? other.getAuthStatus() == null
                        : this.getAuthStatus().equals(other.getAuthStatus()))
                && (this.getSignStatus() == null ? other.getSignStatus() == null
                        : this.getSignStatus().equals(other.getSignStatus()))
                && (this.getIdentType() == null ? other.getIdentType() == null
                        : this.getIdentType().equals(other.getIdentType()))
                && (this.getIdentNo() == null ? other.getIdentNo() == null
                        : this.getIdentNo().equals(other.getIdentNo()))
                && (this.getFlag() == null ? other.getFlag() == null : this.getFlag().equals(other.getFlag()))
                && (this.getBackupAccount() == null ? other.getBackupAccount() == null
                        : this.getBackupAccount().equals(other.getBackupAccount()))
                && (this.getRegDate() == null ? other.getRegDate() == null
                        : this.getRegDate().equals(other.getRegDate()))
                && (this.getModiDate() == null ? other.getModiDate() == null
                        : this.getModiDate().equals(other.getModiDate()))
                && (this.getCountyName() == null ? other.getCountyName() == null
                        : this.getCountyName().equals(other.getCountyName()))
                && (this.getCityName() == null ? other.getCityName() == null
                        : this.getCityName().equals(other.getCityName()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getMoneyAccount() == null) ? 0 : getMoneyAccount().hashCode());
        result = prime * result + ((getCustNo() == null) ? 0 : getCustNo().hashCode());
        result = prime * result + ((getTradeAccount() == null) ? 0 : getTradeAccount().hashCode());
        result = prime * result + ((getBankNo() == null) ? 0 : getBankNo().hashCode());
        result = prime * result + ((getBankName() == null) ? 0 : getBankName().hashCode());
        result = prime * result + ((getBankAccount() == null) ? 0 : getBankAccount().hashCode());
        result = prime * result + ((getBankAcountName() == null) ? 0 : getBankAcountName().hashCode());
        result = prime * result + ((getBranchBank() == null) ? 0 : getBranchBank().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getLastStatus() == null) ? 0 : getLastStatus().hashCode());
        result = prime * result + ((getNetNo() == null) ? 0 : getNetNo().hashCode());
        result = prime * result + ((getPayCenterNo() == null) ? 0 : getPayCenterNo().hashCode());
        result = prime * result + ((getCityNo() == null) ? 0 : getCityNo().hashCode());
        result = prime * result + ((getEndFrozenDate() == null) ? 0 : getEndFrozenDate().hashCode());
        result = prime * result + ((getContractNo() == null) ? 0 : getContractNo().hashCode());
        result = prime * result + ((getAuthStatus() == null) ? 0 : getAuthStatus().hashCode());
        result = prime * result + ((getSignStatus() == null) ? 0 : getSignStatus().hashCode());
        result = prime * result + ((getIdentType() == null) ? 0 : getIdentType().hashCode());
        result = prime * result + ((getIdentNo() == null) ? 0 : getIdentNo().hashCode());
        result = prime * result + ((getFlag() == null) ? 0 : getFlag().hashCode());
        result = prime * result + ((getBackupAccount() == null) ? 0 : getBackupAccount().hashCode());
        result = prime * result + ((getRegDate() == null) ? 0 : getRegDate().hashCode());
        result = prime * result + ((getModiDate() == null) ? 0 : getModiDate().hashCode());
        result = prime * result + ((getCountyName() == null) ? 0 : getCountyName().hashCode());
        result = prime * result + ((getCityName() == null) ? 0 : getCityName().hashCode());
        return result;
    }

    public SaleAccoBankInfo() {

    }

    public SaleAccoBankInfo(SaleAccoRequestInfo request) {
        BeanMapper.copy(request, this);
        this.setRegDate(BetterDateUtils.getNumDate());
        this.setModiDate(BetterDateUtils.getNumDate());
        this.regOperId = UserUtils.getOperatorInfo().getId();
        this.regOperName = UserUtils.getOperatorInfo().getName();
        this.regTime = BetterDateUtils.getNumTime();

        this.modiOperId = UserUtils.getOperatorInfo().getId();
        this.modiOperName = UserUtils.getOperatorInfo().getName();
        this.modiTime = BetterDateUtils.getNumTime();

        this.setStatus("4");
        this.setLastStatus(this.status);
        this.setAuthStatus("0");
        this.setSignStatus(Boolean.FALSE);
        this.setFlag("0");
        this.operOrg = request.getOperOrg();
        this.setMoneyAccount(SerialGenerator.getMoneyAccountID());
        request.setMoneyAccount(this.moneyAccount);
        String tmpStr = AreaUtils.findAreaName(request.getCityNo());
        if (StringUtils.isNotBlank(tmpStr) && StringUtils.isBlank(this.cityName)) {
            this.cityName = tmpStr;
        }
    }
}