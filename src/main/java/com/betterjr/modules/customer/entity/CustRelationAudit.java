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
@Table(name = "T_CUST_RELATION_AUDIT")
public class CustRelationAudit implements BetterjrEntity {
    /**
     * 流水号
     */
    @Id
    @Column(name = "ID", columnDefinition = "INTEGER")
    @MetaData(value = "流水号", comments = "流水号")
    private Long id;

    /**
     * 客户编号
     */
    @Column(name = "L_CUSTNO", columnDefinition = "INTEGER")
    @MetaData(value = "客户编号", comments = "客户编号")
    private Long custNo;

    /**
     * 客户全称
     */
    @Column(name = "C_CUSTNAME", columnDefinition = "VARCHAR")
    @MetaData(value = "客户全称", comments = "客户全称")
    private String custName;

    /**
     * 关系ID
     */
    @Column(name = "L_RELATE_ID", columnDefinition = "INTEGER")
    @MetaData(value = "关系ID", comments = "关系ID")
    private Long relateId;

    /**
     * 关系客户编号
     */
    @Column(name = "L_RELATE_CUSTNO", columnDefinition = "INTEGER")
    @MetaData(value = "关系客户编号", comments = "关系客户编号")
    private Long relateCustno;

    /**
     * 关系客户名称
     */
    @Column(name = "C_RELATE_CUSTNAME", columnDefinition = "VARCHAR")
    @MetaData(value = "关系客户名称", comments = "关系客户名称")
    private String relateCustname;

    /**
     * 关系类型:0供应商与保理公司 1供应商与核心企业 2核心企业与保理公司 3经销商与保理公司 4经销商与核心企业
     */
    @Column(name = "C_RELATE_TYPE", columnDefinition = "CHAR")
    @MetaData(value = "关系类型:0供应商与保理公司 1供应商与核心企业 2核心企业与保理公司 3经销商与保理公司 4经销商与核心企业", comments = "关系类型:0供应商与保理公司 1供应商与核心企业 2核心企业与保理公司 3经销商与保理公司 4经销商与核心企业")
    private String relateType;

    /**
     * 任务名称
     */
    @Column(name = "C_TASK_NAME", columnDefinition = "VARCHAR")
    @MetaData(value = "任务名称", comments = "任务名称")
    private String taskName;

    /**
     * 审核人(操作员)ID号
     */
    @JsonIgnore
    @Column(name = "L_AUDIT_OPERID", columnDefinition = "INTEGER")
    @MetaData(value = "创建人(操作员)ID号", comments = "创建人(操作员)ID号")
    private Long auditOperId;

    /**
     * 审核人(操作员)姓名
     */
    @Column(name = "C_AUDIT_OPERNAME", columnDefinition = "VARCHAR")
    @MetaData(value = "创建人(操作员)姓名", comments = "创建人(操作员)姓名")
    private String auditOperName;

    /**
     * 审核日期
     */
    @JsonSerialize(using = CustDateJsonSerializer.class)
    @Column(name = "D_AUDIT_DATE", columnDefinition = "VARCHAR")
    @MetaData(value = "审核日期", comments = "审核日期")
    private String auditDate;

    /**
     * 审核时间
     */
    @Column(name = "T_AUDIT_TIME", columnDefinition = "VARCHAR")
    @MetaData(value = "审核时间", comments = "审核时间")
    private String auditTime;

    /**
     * 审核结果:0-审核通过;1-审核驳回;
     */
    @Column(name = "C_AUDIT_RESULT", columnDefinition = "CHAR")
    @MetaData(value = "审核结果:0-审核通过;1-审核驳回;", comments = "审核结果:0-审核通过;1-审核驳回;")
    private String auditResult;

    /**
     * 审批意见
     */
    @Column(name = "C_AUDIT_OPINION", columnDefinition = "VARCHAR")
    @MetaData(value = "审批意见", comments = "审批意见")
    private String auditOpinion;

    /**
     * 操作机构
     */
    @JsonIgnore
    @Column(name = "C_OPERORG", columnDefinition = "VARCHAR")
    @MetaData(value = "操作机构", comments = "操作机构")
    private String operOrg;

    private static final long serialVersionUID = 5850899639943399016L;

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
        this.custName = custName;
    }

    public Long getRelateId() {
        return relateId;
    }

    public void setRelateId(Long relateId) {
        this.relateId = relateId;
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
        this.relateCustname = relateCustname;
    }

    public String getRelateType() {
        return relateType;
    }

    public void setRelateType(String relateType) {
        this.relateType = relateType;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Long getAuditOperId() {
        return auditOperId;
    }

    public void setAuditOperId(Long auditOperId) {
        this.auditOperId = auditOperId;
    }

    public String getAuditOperName() {
        return auditOperName;
    }

    public void setAuditOperName(String auditOperName) {
        this.auditOperName = auditOperName;
    }

    public String getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(String auditDate) {
        this.auditDate = auditDate;
    }

    public String getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime;
    }

    public String getAuditResult() {
        return auditResult;
    }

    public void setAuditResult(String auditResult) {
        this.auditResult = auditResult;
    }

    public String getAuditOpinion() {
        return auditOpinion;
    }

    public void setAuditOpinion(String auditOpinion) {
        this.auditOpinion = auditOpinion;
    }

    public String getOperOrg() {
        return operOrg;
    }

    public void setOperOrg(String operOrg) {
        this.operOrg = operOrg;
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
        sb.append(", relateId=").append(relateId);
        sb.append(", relateCustno=").append(relateCustno);
        sb.append(", relateCustname=").append(relateCustname);
        sb.append(", relateType=").append(relateType);
        sb.append(", taskName=").append(taskName);
        sb.append(", auditOperId=").append(auditOperId);
        sb.append(", auditOperName=").append(auditOperName);
        sb.append(", auditDate=").append(auditDate);
        sb.append(", auditTime=").append(auditTime);
        sb.append(", auditResult=").append(auditResult);
        sb.append(", auditOpinion=").append(auditOpinion);
        sb.append(", operOrg=").append(operOrg);
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
        CustRelationAudit other = (CustRelationAudit) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getCustNo() == null ? other.getCustNo() == null : this.getCustNo().equals(other.getCustNo()))
                && (this.getCustName() == null ? other.getCustName() == null : this.getCustName().equals(other.getCustName()))
                && (this.getRelateId() == null ? other.getRelateId() == null : this.getRelateId().equals(other.getRelateId()))
                && (this.getRelateCustno() == null ? other.getRelateCustno() == null : this.getRelateCustno().equals(other.getRelateCustno()))
                && (this.getRelateCustname() == null ? other.getRelateCustname() == null : this.getRelateCustname().equals(other.getRelateCustname()))
                && (this.getRelateType() == null ? other.getRelateType() == null : this.getRelateType().equals(other.getRelateType()))
                && (this.getTaskName() == null ? other.getTaskName() == null : this.getTaskName().equals(other.getTaskName()))
                && (this.getAuditOperId() == null ? other.getAuditOperId() == null : this.getAuditOperId().equals(other.getAuditOperId()))
                && (this.getAuditOperName() == null ? other.getAuditOperName() == null : this.getAuditOperName().equals(other.getAuditOperName()))
                && (this.getAuditDate() == null ? other.getAuditDate() == null : this.getAuditDate().equals(other.getAuditDate()))
                && (this.getAuditTime() == null ? other.getAuditTime() == null : this.getAuditTime().equals(other.getAuditTime()))
                && (this.getAuditResult() == null ? other.getAuditResult() == null : this.getAuditResult().equals(other.getAuditResult()))
                && (this.getAuditOpinion() == null ? other.getAuditOpinion() == null : this.getAuditOpinion().equals(other.getAuditOpinion()))
                && (this.getOperOrg() == null ? other.getOperOrg() == null : this.getOperOrg().equals(other.getOperOrg()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getCustNo() == null) ? 0 : getCustNo().hashCode());
        result = prime * result + ((getCustName() == null) ? 0 : getCustName().hashCode());
        result = prime * result + ((getRelateId() == null) ? 0 : getRelateId().hashCode());
        result = prime * result + ((getRelateCustno() == null) ? 0 : getRelateCustno().hashCode());
        result = prime * result + ((getRelateCustname() == null) ? 0 : getRelateCustname().hashCode());
        result = prime * result + ((getRelateType() == null) ? 0 : getRelateType().hashCode());
        result = prime * result + ((getTaskName() == null) ? 0 : getTaskName().hashCode());
        result = prime * result + ((getAuditOperId() == null) ? 0 : getAuditOperId().hashCode());
        result = prime * result + ((getAuditOperName() == null) ? 0 : getAuditOperName().hashCode());
        result = prime * result + ((getAuditDate() == null) ? 0 : getAuditDate().hashCode());
        result = prime * result + ((getAuditTime() == null) ? 0 : getAuditTime().hashCode());
        result = prime * result + ((getAuditResult() == null) ? 0 : getAuditResult().hashCode());
        result = prime * result + ((getAuditOpinion() == null) ? 0 : getAuditOpinion().hashCode());
        result = prime * result + ((getOperOrg() == null) ? 0 : getOperOrg().hashCode());
        return result;
    }

    private void init(CustRelation anCustRelation, String anAuditOpinion, String anTaskName) {
        this.id = SerialGenerator.getLongValue("CustRelationAudit.id");
        this.auditOperId = UserUtils.getOperatorInfo().getId();
        this.auditOperName = UserUtils.getOperatorInfo().getName();
        this.auditDate = BetterDateUtils.getNumDate();
        this.auditTime = BetterDateUtils.getNumTime();
        this.operOrg = UserUtils.getOperatorInfo().getOperOrg();

        this.custNo = anCustRelation.getCustNo();
        this.custName = anCustRelation.getCustName();
        this.relateId = anCustRelation.getId();
        this.relateCustno = anCustRelation.getRelateCustno();
        this.relateCustname = anCustRelation.getRelateCustname();
        this.relateType = anCustRelation.getRelateType();
        
        this.auditOpinion = anAuditOpinion;// 处理意见
        this.taskName = anTaskName;
    }

    public void initAuditValue(CustRelation anCustRelation, String anAuditOpinion, String anTaskName) {
        init(anCustRelation, anAuditOpinion, anTaskName);
        this.auditResult = "0";// 审核结果:0-审核通过;1-审核驳回;
    }

    public void initRefuseValue(CustRelation anCustRelation, String anAuditOpinion, String anTaskName) {
        init(anCustRelation, anAuditOpinion, anTaskName);
        this.auditResult = "1";// 审核结果:0-审核通过;1-审核驳回;
    }

}