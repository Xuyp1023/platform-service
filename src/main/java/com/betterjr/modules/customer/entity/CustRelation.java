package com.betterjr.modules.customer.entity;

import com.betterjr.common.annotation.*;
import com.betterjr.common.entity.BetterjrEntity;
import javax.persistence.*;

@Access(AccessType.FIELD)
@Entity
@Table(name = "t_cust_relation")
public class CustRelation implements BetterjrEntity {
    /**
     * 编号
     */
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
     * 银行账号
     */
    @Column(name = "C_BANK_ACCO",  columnDefinition="VARCHAR" )
    @MetaData( value="银行账号", comments = "银行账号")
    private String bankAcco;

    /**
     * 银行户名
     */
    @Column(name = "C_BANK_ACCONAME",  columnDefinition="VARCHAR" )
    @MetaData( value="银行户名", comments = "银行户名")
    private String bankAccoName;

    /**
     * 客户类型：0：机构；1：个人
     */
    @Column(name = "C_CUSTTYPE",  columnDefinition="CHAR" )
    @MetaData( value="客户类型：0：机构", comments = "客户类型：0：机构；1：个人")
    private String custType;

    /**
     * 客户在资金管理系统中的客户号
     */
    @Column(name = "C_BT_NO",  columnDefinition="VARCHAR" )
    @MetaData( value="客户在资金管理系统中的客户号", comments = "客户在资金管理系统中的客户号")
    private String btNo;

    /**
     * 所属单位ID
     */
    @Column(name = "C_CORP_ID",  columnDefinition="VARCHAR" )
    @MetaData( value="所属单位ID", comments = "所属单位ID")
    private String corpId;

    /**
     * 关系客户编号
     */
    @Column(name = "L_RELATE_CUSTNO",  columnDefinition="INTEGER" )
    @MetaData( value="关系客户编号", comments = "关系客户编号")
    private Long relateCustno;

    /**
     * 关系客户名称
     */
    @Column(name = "C_RELATE_CUSTNAME",  columnDefinition="VARCHAR" )
    @MetaData( value="关系客户名称", comments = "关系客户名称")
    private String relateCustname;

    /**
     * 关系类型:0供应商与保理公司 1供应商与核心企业 2核心企业与保理公司 3经销商与保理公司 4经销商与核心企业
     */
    @Column(name = "C_RELATE_TYPE",  columnDefinition="CHAR" )
    @MetaData( value="关系类型:0供应商与保理公司 1供应商与核心企业 2核心企业与保理公司 3经销商与保理公司 4经销商与核心企业", comments = "关系类型:0供应商与保理公司 1供应商与核心企业 2核心企业与保理公司 3经销商与保理公司 4经销商与核心企业")
    private String relateType;

    /**
     * 操作员编号
     */
    @Column(name = "L_OPERID",  columnDefinition="INTEGER" )
    @MetaData( value="操作员编号", comments = "操作员编号")
    private Long operId;

    /**
     * 操作员姓名
     */
    @Column(name = "C_OPERNAME",  columnDefinition="VARCHAR" )
    @MetaData( value="操作员姓名", comments = "操作员姓名")
    private String operName;

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
     * 登陆机构
     */
    @Column(name = "C_OPERORG",  columnDefinition="VARCHAR" )
    @MetaData( value="登陆机构", comments = "登陆机构")
    private String operOrg;

    /**
     * 状态，0未处理，1正常，2申请中， 3取消中，4取消
     */
    @Column(name = "C_BUSIN_STATUS",  columnDefinition="CHAR" )
    @MetaData( value="状态", comments = "状态，0未处理，1正常，2申请中， 3取消中，4取消")
    private String businStatus;

    @Column(name = "C_LAST_STATUS",  columnDefinition="CHAR" )
    @MetaData( value="", comments = "")
    private String lastStatus;

    private static final long serialVersionUID = 1468812783874L;

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

    public String getCustType() {
        return custType;
    }

    public void setCustType(String custType) {
        this.custType = custType == null ? null : custType.trim();
    }

    public String getBtNo() {
        return btNo;
    }

    public void setBtNo(String btNo) {
        this.btNo = btNo == null ? null : btNo.trim();
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId == null ? null : corpId.trim();
    }

    public Long getRelateCustno() {
        return relateCustno;
    }

    public void setRelateCustno(Long relateCustno) {
        this.relateCustno = relateCustno;
    }

    public String getRelateCustname() {
        return relateCustname;
    }

    public void setRelateCustname(String relateCustname) {
        this.relateCustname = relateCustname == null ? null : relateCustname.trim();
    }

    public String getRelateType() {
        return relateType;
    }

    public void setRelateType(String relateType) {
        this.relateType = relateType == null ? null : relateType.trim();
    }

    public Long getOperId() {
        return operId;
    }

    public void setOperId(Long operId) {
        this.operId = operId;
    }

    public String getOperName() {
        return operName;
    }

    public void setOperName(String operName) {
        this.operName = operName == null ? null : operName.trim();
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", custNo=").append(custNo);
        sb.append(", custName=").append(custName);
        sb.append(", bankAcco=").append(bankAcco);
        sb.append(", bankAccoName=").append(bankAccoName);
        sb.append(", custType=").append(custType);
        sb.append(", btNo=").append(btNo);
        sb.append(", corpId=").append(corpId);
        sb.append(", relateCustno=").append(relateCustno);
        sb.append(", relateCustname=").append(relateCustname);
        sb.append(", relateType=").append(relateType);
        sb.append(", operId=").append(operId);
        sb.append(", operName=").append(operName);
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
        CustRelation other = (CustRelation) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getCustNo() == null ? other.getCustNo() == null : this.getCustNo().equals(other.getCustNo()))
            && (this.getCustName() == null ? other.getCustName() == null : this.getCustName().equals(other.getCustName()))
            && (this.getBankAcco() == null ? other.getBankAcco() == null : this.getBankAcco().equals(other.getBankAcco()))
            && (this.getBankAccoName() == null ? other.getBankAccoName() == null : this.getBankAccoName().equals(other.getBankAccoName()))
            && (this.getCustType() == null ? other.getCustType() == null : this.getCustType().equals(other.getCustType()))
            && (this.getBtNo() == null ? other.getBtNo() == null : this.getBtNo().equals(other.getBtNo()))
            && (this.getCorpId() == null ? other.getCorpId() == null : this.getCorpId().equals(other.getCorpId()))
            && (this.getRelateCustno() == null ? other.getRelateCustno() == null : this.getRelateCustno().equals(other.getRelateCustno()))
            && (this.getRelateCustname() == null ? other.getRelateCustname() == null : this.getRelateCustname().equals(other.getRelateCustname()))
            && (this.getRelateType() == null ? other.getRelateType() == null : this.getRelateType().equals(other.getRelateType()))
            && (this.getOperId() == null ? other.getOperId() == null : this.getOperId().equals(other.getOperId()))
            && (this.getOperName() == null ? other.getOperName() == null : this.getOperName().equals(other.getOperName()))
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
            && (this.getLastStatus() == null ? other.getLastStatus() == null : this.getLastStatus().equals(other.getLastStatus()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getCustNo() == null) ? 0 : getCustNo().hashCode());
        result = prime * result + ((getCustName() == null) ? 0 : getCustName().hashCode());
        result = prime * result + ((getBankAcco() == null) ? 0 : getBankAcco().hashCode());
        result = prime * result + ((getBankAccoName() == null) ? 0 : getBankAccoName().hashCode());
        result = prime * result + ((getCustType() == null) ? 0 : getCustType().hashCode());
        result = prime * result + ((getBtNo() == null) ? 0 : getBtNo().hashCode());
        result = prime * result + ((getCorpId() == null) ? 0 : getCorpId().hashCode());
        result = prime * result + ((getRelateCustno() == null) ? 0 : getRelateCustno().hashCode());
        result = prime * result + ((getRelateCustname() == null) ? 0 : getRelateCustname().hashCode());
        result = prime * result + ((getRelateType() == null) ? 0 : getRelateType().hashCode());
        result = prime * result + ((getOperId() == null) ? 0 : getOperId().hashCode());
        result = prime * result + ((getOperName() == null) ? 0 : getOperName().hashCode());
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
        return result;
    }
}