package com.betterjr.modules.operator.entity;

import com.betterjr.common.entity.BetterjrEntity;
import com.betterjr.common.selectkey.SerialGenerator;
import com.betterjr.common.utils.BetterDateUtils;

import javax.persistence.*;

@Access(AccessType.FIELD)
@Entity
@Table(name = "T_CFG_OPERATOR_ROLE_RELATION")
public class SysOperatorRoleRelation implements BetterjrEntity {
    @Id
    @Column(name = "ID",  columnDefinition="INTEGER" )
    private Long id;

    @Column(name = "L_ROLE_ID",  columnDefinition="INTEGER" )
    private Long roleId;

    @Column(name = "L_OPERID",  columnDefinition="INTEGER" )
    private Long operId;

    @Column(name = "C_BUSIN_STATUS",  columnDefinition="VARCHAR" )
    private String businStatus;

    @Column(name = "D_DATE",  columnDefinition="VARCHAR" )
    private String regDate;

    @Column(name = "C_ROLE_NAME",  columnDefinition="VARCHAR" )
    private String roleName;

    private static final long serialVersionUID = 1470969669916L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getOperId() {
        return operId;
    }

    public void setOperId(Long operId) {
        this.operId = operId;
    }

    public String getBusinStatus() {
        return businStatus;
    }

    public void setBusinStatus(String businStatus) {
        this.businStatus = businStatus == null ? null : businStatus.trim();
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate == null ? null : regDate.trim();
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName == null ? null : roleName.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", roleId=").append(roleId);
        sb.append(", operId=").append(operId);
        sb.append(", businStatus=").append(businStatus);
        sb.append(", regDate=").append(regDate);
        sb.append(", roleName=").append(roleName);
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
        SysOperatorRoleRelation other = (SysOperatorRoleRelation) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getRoleId() == null ? other.getRoleId() == null : this.getRoleId().equals(other.getRoleId()))
            && (this.getOperId() == null ? other.getOperId() == null : this.getOperId().equals(other.getOperId()))
            && (this.getBusinStatus() == null ? other.getBusinStatus() == null : this.getBusinStatus().equals(other.getBusinStatus()))
            && (this.getRegDate() == null ? other.getRegDate() == null : this.getRegDate().equals(other.getRegDate()))
            && (this.getRoleName() == null ? other.getRoleName() == null : this.getRoleName().equals(other.getRoleName()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getRoleId() == null) ? 0 : getRoleId().hashCode());
        result = prime * result + ((getOperId() == null) ? 0 : getOperId().hashCode());
        result = prime * result + ((getBusinStatus() == null) ? 0 : getBusinStatus().hashCode());
        result = prime * result + ((getRegDate() == null) ? 0 : getRegDate().hashCode());
        result = prime * result + ((getRoleName() == null) ? 0 : getRoleName().hashCode());
        return result;
    }
    
    public SysOperatorRoleRelation() {
    }
    
    public SysOperatorRoleRelation(Long roleId,Long operId,String roleName){
        this.id = SerialGenerator.getLongValue("SysOperatorRoleRelation.id");
        this.roleId=roleId;
        this.operId=operId;
        this.roleName=roleName;
        this.businStatus="1";
        this.regDate=BetterDateUtils.getNumDate();
    }
}