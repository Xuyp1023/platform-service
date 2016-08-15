package com.betterjr.modules.role.entity;

import com.betterjr.common.entity.BetterjrEntity;
import com.betterjr.common.selectkey.SerialGenerator;

import javax.persistence.*;

@Access(AccessType.FIELD)
@Entity
@Table(name = "T_CFG_ROLE")
public class Role implements BetterjrEntity {
    @Id
    @Column(name = "ID",  columnDefinition="INTEGER" )
    private Long id;

    @Column(name = "C_ROLE_NAME",  columnDefinition="VARCHAR" )
    private String roleName;

    @Column(name = "C_ROLE_TYPE",  columnDefinition="VARCHAR" )
    private String roleType;

    @Column(name = "C_BUSIN_STATUS",  columnDefinition="VARCHAR" )
    private String businStatus;

    @Column(name = "C_OPERORG",  columnDefinition="VARCHAR" )
    private String operOrg;

    private static final long serialVersionUID = 7232514578015396419L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName == null ? null : roleName.trim();
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType == null ? null : roleType.trim();
    }

    public String getBusinStatus() {
        return businStatus;
    }

    public void setBusinStatus(String businStatus) {
        this.businStatus = businStatus == null ? null : businStatus.trim();
    }

    public String getOperOrg() {
        return operOrg;
    }

    public void setOperOrg(String operOrg) {
        this.operOrg = operOrg == null ? null : operOrg.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", roleName=").append(roleName);
        sb.append(", roleType=").append(roleType);
        sb.append(", businStatus=").append(businStatus);
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
        Role other = (Role) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getRoleName() == null ? other.getRoleName() == null : this.getRoleName().equals(other.getRoleName()))
            && (this.getRoleType() == null ? other.getRoleType() == null : this.getRoleType().equals(other.getRoleType()))
            && (this.getBusinStatus() == null ? other.getBusinStatus() == null : this.getBusinStatus().equals(other.getBusinStatus()))
            && (this.getOperOrg() == null ? other.getOperOrg() == null : this.getOperOrg().equals(other.getOperOrg()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getRoleName() == null) ? 0 : getRoleName().hashCode());
        result = prime * result + ((getRoleType() == null) ? 0 : getRoleType().hashCode());
        result = prime * result + ((getBusinStatus() == null) ? 0 : getBusinStatus().hashCode());
        result = prime * result + ((getOperOrg() == null) ? 0 : getOperOrg().hashCode());
        return result;
    }
    public Role(){
        
    }
    
    public Role(String roleName,String roleType,String businStatus){
        this.id=SerialGenerator.getLongValue("Role.id");
        this.roleName=roleName;
        this.roleType=roleType;
        this.businStatus=businStatus;
    }
    
    public Role(String roleId,String roleName,String roleType,String businStatus){
        this.id=Long.parseLong(roleId);
        this.roleName=roleName;
        this.roleType=roleType;
        this.businStatus=businStatus;
    }
}