package com.betterjr.modules.customer.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.betterjr.common.annotation.MetaData;
import com.betterjr.common.entity.BetterjrEntity;

@Entity
@Table(name = "t_sys_naps_bankcode")
public class SysNapsBankCode implements BetterjrEntity {

    /**
     * 
     */
    private static final long serialVersionUID = 7957736554654526L;

    @Id
    @Column(name = "c_pay_sys_num", columnDefinition = "VARCHAR")
    @MetaData(value = "支付系统行号(联行号)", comments = "支付系统行号(联行号)")
    private String paySysNum;

    @Column(name = "c_participant_line_num", columnDefinition = "VARCHAR")
    @MetaData(value = "所属直接参与者行号", comments = "所属直接参与者行号")
    private String participantLineNum;

    @Column(name = "c_invalid_flag", columnDefinition = "VARCHAR")
    @MetaData(value = "失效标识", comments = "失效标识")
    private String invalidFlag;

    @Column(name = "c_city_code", columnDefinition = "VARCHAR")
    @MetaData(value = "城市代码", comments = "城市代码")
    private String cityCode;

    @Column(name = "c_org_fullname", columnDefinition = "VARCHAR")
    @MetaData(value = "机构全称", comments = "机构全称")
    private String orgFullName;

    @Column(name = "c_org_name", columnDefinition = "VARCHAR")
    @MetaData(value = "机构简称", comments = "机构简称")
    private String orgName;

    @Column(name = "c_line_code", columnDefinition = "VARCHAR")
    @MetaData(value = "行别代码", comments = "行别代码")
    private String lineCode;

    @Column(name = "c_pay_sys_code", columnDefinition = "VARCHAR")
    @MetaData(value = "支付系统代码", comments = "支付系统代码")
    private String paySysCode;

    @Column(name = "d_effective_date", columnDefinition = "VARCHAR")
    @MetaData(value = "生效日期", comments = "生效日期")
    private String effectiveDate;

    @Column(name = "d_invalid_date", columnDefinition = "VARCHAR")
    @MetaData(value = "失效日期", comments = "失效日期")
    private String invalidDate;

    @Column(name = "d_start_date", columnDefinition = "VARCHAR")
    @MetaData(value = "开始日期", comments = "开始日期")
    private String startDate;

    @Column(name = "d_end_date", columnDefinition = "VARCHAR")
    @MetaData(value = "结束日期", comments = "结束日期")
    private String endDate;

    public String getPaySysNum() {
        return this.paySysNum;
    }

    public void setPaySysNum(String anPaySysNum) {
        this.paySysNum = anPaySysNum;
    }

    public String getParticipantLineNum() {
        return this.participantLineNum;
    }

    public void setParticipantLineNum(String anParticipantLineNum) {
        this.participantLineNum = anParticipantLineNum;
    }

    public String getInvalidFlag() {
        return this.invalidFlag;
    }

    public void setInvalidFlag(String anInvalidFlag) {
        this.invalidFlag = anInvalidFlag;
    }

    public String getCityCode() {
        return this.cityCode;
    }

    public void setCityCode(String anCityCode) {
        this.cityCode = anCityCode;
    }

    public String getOrgFullName() {
        return this.orgFullName;
    }

    public void setOrgFullName(String anOrgFullName) {
        this.orgFullName = anOrgFullName;
    }

    public String getOrgName() {
        return this.orgName;
    }

    public void setOrgName(String anOrgName) {
        this.orgName = anOrgName;
    }

    public String getLineCode() {
        return this.lineCode;
    }

    public void setLineCode(String anLineCode) {
        this.lineCode = anLineCode;
    }

    public String getPaySysCode() {
        return this.paySysCode;
    }

    public void setPaySysCode(String anPaySysCode) {
        this.paySysCode = anPaySysCode;
    }

    public String getEffectiveDate() {
        return this.effectiveDate;
    }

    public void setEffectiveDate(String anEffectiveDate) {
        this.effectiveDate = anEffectiveDate;
    }

    public String getInvalidDate() {
        return this.invalidDate;
    }

    public void setInvalidDate(String anInvalidDate) {
        this.invalidDate = anInvalidDate;
    }

    public String getStartDate() {
        return this.startDate;
    }

    public void setStartDate(String anStartDate) {
        this.startDate = anStartDate;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public void setEndDate(String anEndDate) {
        this.endDate = anEndDate;
    }

    @Override
    public String toString() {
        return "SysNapsBankCode [paySysNum=" + this.paySysNum + ", participantLineNum=" + this.participantLineNum
                + ", invalidFlag=" + this.invalidFlag + ", cityCode=" + this.cityCode + ", orgFullName="
                + this.orgFullName + ", orgName=" + this.orgName + ", lineCode=" + this.lineCode + ", paySysCode="
                + this.paySysCode + ", effectiveDate=" + this.effectiveDate + ", invalidDate=" + this.invalidDate
                + ", startDate=" + this.startDate + ", endDate=" + this.endDate + "]";
    }

}
