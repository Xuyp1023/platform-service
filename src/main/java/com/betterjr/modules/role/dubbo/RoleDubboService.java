package com.betterjr.modules.role.dubbo;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.data.SimpleDataEntity;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.role.IRoleService;
import com.betterjr.modules.role.entity.Role;
import com.betterjr.modules.role.service.RoleService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

/***
 * 角色管理
 * @author hubl
 *
 */
@Service
public class RoleDubboService implements IRoleService {

    @Autowired
    private RoleService roleService;
    
    /***
     * 添加角色信息
     */
    @Override
    public String webAddRole(String anRoleName,String anRoleType,String anBusinStatus) {
        if(roleService.addRole(anRoleName,anRoleType,anBusinStatus)){
            return AjaxObject.newOk("角色添加成功").toJson();
        }else{
            return AjaxObject.newError("角色添加失败").toJson();
        }
    }

    /***
     * 编辑角色信息
     */
    @Override
    public String webUploadRole(String anRoleId,String anRoleName,String anRoleType,String anBusinStatus) {
        if(roleService.updateRole(anRoleId,anRoleName,anRoleType,anBusinStatus)){
            return AjaxObject.newOk("角色修改成功").toJson();
        }else{
            return AjaxObject.newError("角色修改失败").toJson();
        }
    }

    @Override
    public String webDelRole(Long anRoleId) {
        if(roleService.delRole(anRoleId)){
            return AjaxObject.newOk("删除成功").toJson();
        }else{
            return AjaxObject.newError("删除失败").toJson();
        }
    }

    @Override
    public String webQueryRole(Map<String, Object> anMap,int anPageNum, int anPageSize) {
        return AjaxObject.newOkWithPage("查询角色信息", roleService.queryRole(anMap, anPageNum, anPageSize)).toJson();
    }
    
    /***
     * 查询所有角色信息
     * @return
     */
    @Override
    public String webFindRole(){
        return AjaxObject.newOk("查询所有角色信息",roleService.findRole()).toJson();
    }
    
    /****
     * 查询默认角色
     * @return
     */
    @Override
    public String webQueryRoleDefault(){
        return AjaxObject.newOk("查询默认角色",roleService.queryRoleDefault()).toJson();
    }

}
