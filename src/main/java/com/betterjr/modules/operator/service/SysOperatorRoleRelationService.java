package com.betterjr.modules.operator.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterException;
import com.betterjr.common.service.BaseService;
import com.betterjr.modules.operator.dao.SysOperatorRoleRelationMapper;
import com.betterjr.modules.operator.entity.SysOperatorRoleRelation;
import com.betterjr.modules.role.entity.Role;
import com.betterjr.modules.role.service.RoleService;

/****
 * 操作员角色关系表
 * @author hubl
 *
 */
@Service
public class SysOperatorRoleRelationService extends BaseService<SysOperatorRoleRelationMapper, SysOperatorRoleRelation> {

    private static final Logger logger = LoggerFactory.getLogger(SysOperatorRoleRelationService.class);
    
    @Autowired
    private RoleService roleService;
    
    
    /***
     * 根据操作员编号查询对应的角色信息
     * @param operatorId 角色编号 
     * @return 逗号分隔的角色名称
     */
    public String findSysRoleByOperatorId(Long anOperatorId){
       StringBuffer sb=new StringBuffer();
       Map<String, Object> map=new HashMap<String, Object>();
       map.put("operId", anOperatorId);
       List<SysOperatorRoleRelation> relationList=this.selectByProperty(map);
       logger.info("relationList:"+relationList);
       for(int i=0;i<relationList.size();i++){
           SysOperatorRoleRelation roleRelation=relationList.get(i);
           if(i==0){
               sb.append(roleRelation.getRoleName());
           }else{
               sb.append(",").append(roleRelation.getRoleName());
           }
       }   
       return sb.toString();
    }
    
    /***
     * 修改角色信息
     * @param anOperatorId
     * @param ruleList
     * @return
     */
    public void saveSysOperatorRoleRelation(Long anOperatorId,String ruleList){        
       // 删除原来的角色信息
       this.deleteByProperty("operId", anOperatorId);
       
       logger.info("ruleList:"+ruleList);
       String[] ruleArr=ruleList.split(",");
       for(int i=0;i<ruleArr.length;i++){
           String roleName=ruleArr[i];
           Role role=roleService.findRoleByName(roleName);
           if(role==null){
               throw new BytterException("角色信息未找到");
           }
           SysOperatorRoleRelation roleRelation=new SysOperatorRoleRelation(role.getId(), anOperatorId, roleName);
           this.insert(roleRelation);
       }
    }
    
}
