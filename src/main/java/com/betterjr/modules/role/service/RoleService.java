package com.betterjr.modules.role.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterDeclareException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.role.dao.RoleMapper;
import com.betterjr.modules.role.entity.Role;

/****
 * 角色管理
 * @author hubl
 *
 */
@Service
public class RoleService extends BaseService<RoleMapper, Role> {

    /***
     * 添加角色信息
     * @param role
     * @return
     */
    public boolean addRole(String anRoleName,String anRoleType,String anBusinStatus){
        Role anRole=new Role(anRoleName,anRoleType,anBusinStatus);
        if(checkRoleName(anRole.getRoleName())){
            throw new BytterDeclareException("角色名称已存在");
        }
        return this.insert(anRole)==1;
    }
    
    /***
     * 修改角色信息
     * @param role
     * @return
     */
    public boolean updateRole(String anRoleId,String anRoleName,String anRoleType,String anBusinStatus){
        if(BetterStringUtils.isBlank(anRoleId)){
            throw new BytterDeclareException("要修改的角色ID不存在");
        }
        Role anRole=new Role(anRoleId,anRoleName,anRoleType,anBusinStatus);
        return this.updateByPrimaryKey(anRole)==1;
    }
    
    /***
     * 检查角色名称是否存在
     * @param roleName
     * @return
     */
    public boolean checkRoleName(String roleName){
       List<Role> roleList= this.selectByProperty("roleName", roleName);
       if(roleList.size()>0){
           return true;
       }
       return false;
    }
    
    /***
     * 删除
     * @param anRoleId
     * @return
     */
    public boolean delRole(String anRoleId){
        Role role=this.selectByPrimaryKey(anRoleId);
        return this.delete(role)==1;
    }
    
    /***
     * 分页查询
     * @param anMap
     * @param anPageNum
     * @param anPageSize
     * @return
     */
    public Page<Role> queryRole(Map<String, Object> anMap,int anPageNum,int anPageSize){
        Map<String, Object> map=new HashMap<String, Object>();
        map.put("businStatus", new String[]{"0","1"});
        if(BetterStringUtils.isNotBlank((String)anMap.get("roleName"))){
            map.put("roleName", anMap.get("roleName"));
        }
        
        return this.selectPropertyByPage(Role.class,map, anPageNum, anPageSize, false);
    }
    
    /***
     * 查询所有角色信息
     * @return
     */
    public List<Role> findRole(){
        
        return this.selectByProperty("businStatus", "1");
    }
    
    /***
     * 根据名称获取对象
     * @param roleName
     * @return
     */
    public Role findRoleByName(String roleName){
        Map roleMp=new HashMap();
        roleMp.put("roleName", roleName);
        roleMp.put("businStatus", "1");
        List<Role> roleList= this.selectByProperty(roleMp);
        return Collections3.getFirst(roleList);
    }
}
