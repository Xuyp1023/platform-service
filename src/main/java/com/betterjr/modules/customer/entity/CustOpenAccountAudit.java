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
@Table(name = "t_cust_open_account_audit")
public class CustOpenAccountAudit implements BetterjrEntity {

    /**
     * 流水号
     */
    @Id
    @Column(name = "ID", columnDefinition = "INTEGER")
    @MetaData(value = "流水号", comments = "流水号")
    private Long id;

    /**
     * 开户流水ID
     */
    @Column(name = "L_SOURCE_ID", columnDefinition = "INTEGER")
    @MetaData(value = "开户流水ID", comments = "开户流水ID")
    private Long sourceId;

    /**
     * 审核客户编号
     */
    @Column(name = "L_AUDIT_CUSTNO", columnDefinition = "INTEGER")
    @MetaData(value = "审核客户编号", comments = "审核客户编号")
    private Long auditCustNo;

    /**
     * 审核客户名称
     */
    @Column(name = "C_AUDIT_CUSTNAME", columnDefinition = "VARCHAR")
    @MetaData(value = "审核客户名称", comments = "审核客户编号")
    private String auditCustname;

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
    @MetaData(value = "审核人(操作员)ID号", comments = "创建人(操作员)ID号")
    private Long auditOperId;

    /**
     * 审核人(操作员)姓名
     */
    @Column(name = "C_AUDIT_OPERNAME", columnDefinition = "VARCHAR")
    @MetaData(value = "审核人(操作员)姓名", comments = "创建人(操作员)姓名")
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

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getAuditCustNo() {
        return auditCustNo;
    }

    public void setAuditCustNo(Long auditCustNo) {
        this.auditCustNo = auditCustNo;
    }

    public String getAuditCustname() {
        return auditCustname;
    }

    public void setAuditCustname(String auditCustname) {
        this.auditCustname = auditCustname;
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

    private void init(Long anSourceId, String anAuditOpinion, String anTaskName) {
        this.id = SerialGenerator.getLongValue("CustRelationAudit.id");
        this.auditOperId = UserUtils.getOperatorInfo().getId();
        this.auditOperName = UserUtils.getOperatorInfo().getName();
        this.auditDate = BetterDateUtils.getNumDate();
        this.auditTime = BetterDateUtils.getNumTime();
        this.operOrg = UserUtils.getOperatorInfo().getOperOrg();

        this.sourceId = anSourceId;
        this.auditOpinion = anAuditOpinion;// 处理意见
        this.taskName = anTaskName;
    }

    public void initAuditValue(Long anSourceId, String anAuditOpinion, String anTaskName) {
        init(anSourceId, anAuditOpinion, anTaskName);
        this.auditResult = "0";// 审核结果:0-审核通过;1-审核驳回;
    }

    public void initRefuseValue(Long anSourceId, String anAuditOpinion, String anTaskName) {
        init(anSourceId, anAuditOpinion, anTaskName);
        this.auditResult = "1";// 审核结果:0-审核通过;1-审核驳回;
    }

}