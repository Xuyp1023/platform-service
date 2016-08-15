package com.betterjr.modules.operator.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterException;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.sys.entity.SysMenuInfo;
import com.betterjr.modules.sys.service.SysMenuRuleService;
import com.betterjr.modules.sys.service.SysMenuService;

/***
 * 菜单管理
 * @author hubl
 *
 */ 
@Service
public class SysMenuManagerService {
    
    private final Logger logger = LoggerFactory.getLogger(SysMenuManagerService.class);

    @Autowired
    private SysOperatorRoleRelationService operatorRoleRelationService;
    @Autowired
    private SysMenuRuleService sysMenuRuleService;
    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 根据当前用户类型获取对应角色的菜单列表
     * 
     * @param anMenuId
     *            父菜单编号
     * @return
     */
    public List findSysMenuByMenuId(Integer anMenuId,String anRoleName) {
        List<String> menuIds = sysMenuRuleService.findAllByRuleAndMenu(findRoleByOperator(anRoleName), anMenuId);
        return sysMenuService.findMenuList(menuIds);
    }
    
    /****
     * 根据当前操作员获取角色信息
     * @return
     */
    public List findRoleByOperator(String anRoleName){
        List tmpList = new ArrayList();
        if(BetterStringUtils.isNotBlank(anRoleName)){
            tmpList.add(anRoleName);
        }else{
            if (UserUtils.isBytterUser()){
                tmpList.add("BYTTER_USER");
            }
            else {
                CustOperatorInfo operator=UserUtils.getOperatorInfo();
                String[] arrRule = BetterStringUtils.split(operatorRoleRelationService.findSysRoleByOperatorId(operator.getId()), ";|,");
                tmpList=Arrays.asList(arrRule);
                
            } 
        }
        logger.info("this worker Rule is :" + tmpList);
        return tmpList;
    }
    
    /**
     * 根据当前用户类型获取对应角色的菜单列表
     * 
     * @param anMenuId
     *            父菜单编号
     * @return
     */
    public List findAllSysMenu() {
        return sysMenuService.findMenuList(null);
    }
    
    /***
     * 菜单角色添加
     * @param anRoleId
     * @param anRoleName
     * @param anMenuIdArr
     * @return
     */
    public void addMenuRole(String anRoleId,String anRoleName,String anMenuIdArr){
        if(BetterStringUtils.isNotBlank(anMenuIdArr)){
            String[] menuArr= BetterStringUtils.split(anMenuIdArr, ";|,");
            for(int i=0;i<menuArr.length;i++){
                String menuId=menuArr[i];
                SysMenuInfo menuInfo=sysMenuService.findMenuById(Integer.parseInt(menuId));
                if(menuInfo==null){
                    throw new BytterException("菜单信息未找到");
                }
                sysMenuRuleService.addMenuRole(anRoleId, anRoleName, menuId, menuInfo.getMenuName()); 
            }
        }
    }
    
}