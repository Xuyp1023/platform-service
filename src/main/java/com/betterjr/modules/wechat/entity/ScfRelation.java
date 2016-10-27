package com.betterjr.modules.wechat.entity;

import com.betterjr.common.annotation.*;
import com.betterjr.common.selectkey.SerialGenerator;
import com.betterjr.common.utils.BetterDateUtils;
import com.betterjr.common.utils.MathExtend;
import com.betterjr.modules.wechat.data.ScfClientDataParentFace;

import javax.persistence.*;

@Access(AccessType.FIELD)
@Entity
@Table(name = "T_SCF_RELATION")
public class ScfRelation implements ScfClientDataParentFace {
    /**
     * 编号
     */
    @Id
    @Column(name = "ID",  columnDefinition="INTEGER" )
    @MetaData( value="编号", comments = "编号")
    private Long id;

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

    /**
     * 核心企额客户编号
     */
    @Column(name = "L_CORE_CUSTNO",  columnDefinition="INTEGER" )
    @MetaData( value="核心企额客户编号", comments = "核心企额客户编号")
    private Long coreCustNo;

    /**
     * 银行账户
     */
    @Column(name = "C_BANKACCO",  columnDefinition="VARCHAR" )
    @MetaData( value="银行账户", comments = "银行账户")
    private String bankAccount;

    /**
     * 银行户名
     */
    @Column(name = "C_BANKACCONAME",  columnDefinition="VARCHAR" )
    @MetaData( value="银行户名", comments = "银行户名")
    private String bankAccountName;

    /**
     * 登记日期
     */
    @Column(name = "D_REGDATE",  columnDefinition="VARCHAR" )
    @MetaData( value="登记日期", comments = "登记日期")
    private String regDate;

    /**
     * 状态，0未处理，1正常，2申请中， 3取消中，4取消
     */
    @Column(name = "C_STATUS",  columnDefinition="VARCHAR" )
    @MetaData( value="状态", comments = "状态，0未处理，1正常，2申请中， 3取消中，4取消")
    private String relaStatus;

    /**
     * 修改日期
     */
    @Column(name = "D_MODIDATE",  columnDefinition="VARCHAR" )
    @MetaData( value="修改日期", comments = "修改日期")
    private String modiDate;

    /**
     * 客户类型：0：机构；1：个人
     */
    @Column(name = "C_CUSTTYPE",  columnDefinition="VARCHAR" )
    @MetaData( value="客户类型：0：机构", comments = "客户类型：0：机构；1：个人")
    private String custType;

    /**
     * 操作员所在机构，证书登录，则是证书的企业名称O字段
     */
    @Column(name = "C_OPERORG",  columnDefinition="VARCHAR" )
    @MetaData( value="操作员所在机构", comments = "操作员所在机构，证书登录，则是证书的企业名称O字段")
    private String operOrg;

    /**
     * 客户在资金管理系统中的客户号
     */
    @Column(name = "C_BTNO", columnDefinition = "VARCHAR")
    @MetaData(value = "客户在资金管理系统中的客户号", comments = "客户在资金管理系统中的客户号")
    private String btNo;

    /**
     * 所属单位Id
     */
    @Column(name = "C_CORPID", columnDefinition = "VARCHAR")
    @MetaData(value = "所属单位Id", comments = "所属单位Id")
    private String corpNo;
    
    private static final long serialVersionUID = 6590580938702096880L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getCoreCustNo() {
        return coreCustNo;
    }

    public void setCoreCustNo(Long coreCustNo) {
        this.coreCustNo = coreCustNo;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount == null ? null : bankAccount.trim();
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName == null ? null : bankAccountName.trim();
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate == null ? null : regDate.trim();
    }
 
    public String getRelaStatus() {
        return this.relaStatus;
    }

    public void setRelaStatus(String anRelaStatus) {
        this.relaStatus = anRelaStatus;
    }

    public String getModiDate() {
        return modiDate;
    }

    public void setModiDate(String modiDate) {
        this.modiDate = modiDate == null ? null : modiDate.trim();
    }

    public String getCustType() {
        return custType;
    }

    public void setCustType(String custType) {
        this.custType = custType == null ? null : custType.trim();
    }

    public String getOperOrg() {
        return operOrg;
    }

    public void setOperOrg(String operOrg) {
        this.operOrg = operOrg == null ? null : operOrg.trim();
    }

    public String getBtNo() {
        return this.btNo;
    }

    public void setBtNo(String anBtNo) {
        this.btNo = anBtNo;
    }

    public String getCorpNo() {
        return this.corpNo;
    }

    public void setCorpNo(String anCorpNo) {
        this.corpNo = anCorpNo;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", custNo=").append(custNo);
        sb.append(", custName=").append(custName);
        sb.append(", coreCustNo=").append(coreCustNo);
        sb.append(", bankAccount=").append(bankAccount);
        sb.append(", bankAccountName=").append(bankAccountName);
        sb.append(", regDate=").append(regDate);
        sb.append(", relaStatus=").append(relaStatus);
        sb.append(", modiDate=").append(modiDate);
        sb.append(", custType=").append(custType);
        sb.append(", operOrg=").append(operOrg);
        sb.append(", btNo=").append(btNo);
        sb.append(", corpNo=").append(corpNo);
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
        ScfRelation other = (ScfRelation) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getCustNo() == null ? other.getCustNo() == null : this.getCustNo().equals(other.getCustNo()))
            && (this.getCustName() == null ? other.getCustName() == null : this.getCustName().equals(other.getCustName()))
            && (this.getCoreCustNo() == null ? other.getCoreCustNo() == null : this.getCoreCustNo().equals(other.getCoreCustNo()))
            && (this.getBankAccount() == null ? other.getBankAccount() == null : this.getBankAccount().equals(other.getBankAccount()))
            && (this.getBankAccountName() == null ? other.getBankAccountName() == null : this.getBankAccountName().equals(other.getBankAccountName()))
            && (this.getRegDate() == null ? other.getRegDate() == null : this.getRegDate().equals(other.getRegDate()))
            && (this.getRelaStatus() == null ? other.getRelaStatus() == null : this.getRelaStatus().equals(other.getRelaStatus()))
            && (this.getModiDate() == null ? other.getModiDate() == null : this.getModiDate().equals(other.getModiDate()))
            && (this.getCustType() == null ? other.getCustType() == null : this.getCustType().equals(other.getCustType()))
            && (this.getOperOrg() == null ? other.getOperOrg() == null : this.getOperOrg().equals(other.getOperOrg()))
            && (this.getBtNo() == null ? other.getBtNo() == null : this.getBtNo().equals(other.getBtNo()))
        && (this.getCorpNo() == null ? other.getCorpNo() == null : this.getCorpNo().equals(other.getCorpNo()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getCustNo() == null) ? 0 : getCustNo().hashCode());
        result = prime * result + ((getCustName() == null) ? 0 : getCustName().hashCode());
        result = prime * result + ((getCoreCustNo() == null) ? 0 : getCoreCustNo().hashCode());
        result = prime * result + ((getBankAccount() == null) ? 0 : getBankAccount().hashCode());
        result = prime * result + ((getBankAccountName() == null) ? 0 : getBankAccountName().hashCode());
        result = prime * result + ((getRegDate() == null) ? 0 : getRegDate().hashCode());
        result = prime * result + ((getRelaStatus() == null) ? 0 : getRelaStatus().hashCode());
        result = prime * result + ((getModiDate() == null) ? 0 : getModiDate().hashCode());
        result = prime * result + ((getCustType() == null) ? 0 : getCustType().hashCode());
        result = prime * result + ((getOperOrg() == null) ? 0 : getOperOrg().hashCode());
        result = prime * result + ((getBtNo() == null) ? 0 : getBtNo().hashCode());
        result = prime * result + ((getCorpNo() == null) ? 0 : getCorpNo().hashCode());
        return result;
    }
    
    public void initWeChatValue(){
        this.id = SerialGenerator.getLongValue("ScfRelation.id");
        this.custType = "0";
        this.relaStatus = "1";
        this.regDate = BetterDateUtils.getNumDate();
        this.modiDate = BetterDateUtils.getNumDateTime();        
    }
    
    public void fillDefaultValue(){
        this.id = MathExtend.defaultLongValue(this.id, SerialGenerator.getLongValue("ScfRelation.id"));
        this.custType = "0";
        this.regDate = BetterDateUtils.getNumDate();
        this.modifytValue();
        this.custNo = MathExtend.defaultLongZero(this.custNo);
    }

    @Override
    public void modifytValue() {
        if (MathExtend.smallValue(this.custNo)){
            
            this.relaStatus = "2";
        }
        else{            
            this.relaStatus = "1";
        }
        
       this.modiDate = BetterDateUtils.getNumDateTime();        
    }

    @Override
    public String findBankAccountName() {
        return this.bankAccountName;
    }

}