package com.betterjr.modules.base.entity;

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
@Table(name = "t_sys_business_type")
public class BusinessType implements BetterjrEntity {
    /**
     * 编号
     */
    @Id
    @Column(name = "ID", columnDefinition = "INTEGER")
    @MetaData(value = "编号", comments = "编号")
    private Long id;

    /**
     * 业务类型编码
     */
    @Id
    @Column(name = "C_CODE", columnDefinition = "VARCHAR")
    @MetaData(value = "业务类型编码", comments = "业务类型编码")
    private String code;

    /**
     * 业务类型名称
     */
    @Column(name = "C_NAME", columnDefinition = "VARCHAR")
    @MetaData(value = "业务类型名称", comments = "业务类型名称")
    private String name;

    /**
     * 授信标志
     */
    @Column(name = "C_CREDIT_FLAG", columnDefinition = "CHAR")
    @MetaData(value = "授信标志", comments = "授信标志")
    private String creditFlag;

    /**
     * 备注
     */
    @Column(name = "C_COMMENTS", columnDefinition = "VARCHAR")
    @MetaData(value = "备注", comments = "备注")
    private String comments;

    /**
     * 预览数据编号
     */
    @Column(name = "L_MOCK_DATA_ID", columnDefinition = "INTEGER")
    @MetaData(value = "预览数据编号", comments = "预览数据编号")
    private Long mockDataId;

    /**
     * 业务状态
     */
    @Column(name = "C_BUSIN_STATUS", columnDefinition = "CHAR")
    @MetaData(value = "业务状态", comments = "业务状态")
    private String businStatus;

    /**
     * 文档状态
     */
    @Column(name = "C_DOC_STATUS", columnDefinition = "CHAR")
    @MetaData(value = "文档状态", comments = "文档状态")
    private String docStatus;

    /**
     * 数据版本
     */
    @Column(name = "N_VERSION", columnDefinition = "INTEGER")
    @MetaData(value = "数据版本", comments = "数据版本")
    private Long version;

    private static final long serialVersionUID = 1313331469273004690L;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getCreditFlag() {
        return creditFlag;
    }

    public void setCreditFlag(final String creditFlag) {
        this.creditFlag = creditFlag;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(final String comments) {
        this.comments = comments;
    }

    public Long getMockDataId() {
        return mockDataId;
    }

    public void setMockDataId(final Long mockDataId) {
        this.mockDataId = mockDataId;
    }

    public String getBusinStatus() {
        return businStatus;
    }

    public void setBusinStatus(final String businStatus) {
        this.businStatus = businStatus;
    }

    public String getDocStatus() {
        return docStatus;
    }

    public void setDocStatus(final String docStatus) {
        this.docStatus = docStatus;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(final Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", code=").append(code);
        sb.append(", name=").append(name);
        sb.append(", creditFlag=").append(creditFlag);
        sb.append(", comments=").append(comments);
        sb.append(", mockDataId=").append(mockDataId);
        sb.append(", businStatus=").append(businStatus);
        sb.append(", docStatus=").append(docStatus);
        sb.append(", version=").append(version);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        final BusinessType other = (BusinessType) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getCode() == null ? other.getCode() == null : this.getCode().equals(other.getCode()))
                && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
                && (this.getCreditFlag() == null ? other.getCreditFlag() == null
                        : this.getCreditFlag().equals(other.getCreditFlag()))
                && (this.getComments() == null ? other.getComments() == null
                        : this.getComments().equals(other.getComments()))
                && (this.getMockDataId() == null ? other.getMockDataId() == null
                        : this.getMockDataId().equals(other.getMockDataId()))
                && (this.getBusinStatus() == null ? other.getBusinStatus() == null
                        : this.getBusinStatus().equals(other.getBusinStatus()))
                && (this.getDocStatus() == null ? other.getDocStatus() == null
                        : this.getDocStatus().equals(other.getDocStatus()))
                && (this.getVersion() == null ? other.getVersion() == null
                        : this.getVersion().equals(other.getVersion()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getCode() == null) ? 0 : getCode().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getCreditFlag() == null) ? 0 : getCreditFlag().hashCode());
        result = prime * result + ((getComments() == null) ? 0 : getComments().hashCode());
        result = prime * result + ((getMockDataId() == null) ? 0 : getMockDataId().hashCode());
        result = prime * result + ((getBusinStatus() == null) ? 0 : getBusinStatus().hashCode());
        result = prime * result + ((getDocStatus() == null) ? 0 : getDocStatus().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        return result;
    }
}