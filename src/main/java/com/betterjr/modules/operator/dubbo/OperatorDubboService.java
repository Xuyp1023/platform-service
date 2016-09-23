package com.betterjr.modules.operator.dubbo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.account.entity.CustOperatorInfoRequest;
import com.betterjr.modules.operator.IOperatorService;
import com.betterjr.modules.operator.service.OperatorRequestService;
import com.betterjr.modules.operator.service.SysMenuManagerService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;
/****
 * 操作员管理
 * @author hubl
 *
 */
@Service(interfaceClass=IOperatorService.class)
public class OperatorDubboService implements IOperatorService {

    @Autowired
    private OperatorRequestService operatorRequestService;
    @Autowired
    private SysMenuManagerService manuManagerService;
    
    /***
     * 新增操作员
     */
    @Override
    public String webAddCustOperator(Map<String, Object> anMap) {
        CustOperatorInfoRequest request=(CustOperatorInfoRequest)RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("新增操作员",operatorRequestService.saveCustOperator(request)).toJson();
    }

    /****
     * 编辑操作员
     */
    @Override
    public String webUpdateCustOperator(Map<String, Object> anMap) {
        CustOperatorInfoRequest request=(CustOperatorInfoRequest)RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("编辑操作员",operatorRequestService.updateCustOperator(request)).toJson();
    }

    /****
     * 操作员分页查询
     */
    @Override
    public String webQueryCustOperator(Map<String, String> anMap, int anPageNum, int anPageSize) {
        return AjaxObject.newOkWithPage("操作员分页查询", operatorRequestService.queryCustOperator(anMap, anPageNum, anPageSize)).toJson();
    }
    

    /****
     * 获取左侧菜单信息
     * @param menuId
     * @return
     */
    public String webFindSysMenuByMenuId(Integer anMenuId){
        return AjaxObject.newOk("获取菜单信息",manuManagerService.findSysMenuByMenuId(anMenuId)).toJson();
    }
    
    /****
     * 根据角色获取菜单信息
     * @param menuId
     * @return
     */
    public String webFindSysMenuByMenuRole(String anRoleId){
        return AjaxObject.newOk("获取菜单信息",manuManagerService.findSysMenuByRoleMenu(anRoleId)).toJson();
    }

    /****
     * 获取所有菜单信息
     * @return
     */
    public String webFindAllSysMenu(){
        return AjaxObject.newOk("获取所有菜单信息",manuManagerService.findAllSysMenu()).toJson();
    }
    
    /****
     * 添加菜单角色信息
     * @param anRoleId 角色ID
     * @param anRoleName 角色名称
     * @param anMenuIdArr 菜单列表，前端传来 "，"分隔
     * @return
     */
    public String webAddMenuRole(String anRoleId,String anRoleName,String anMenuIdArr){
        manuManagerService.addMenuRole(anRoleId,anRoleName,anMenuIdArr);
        return AjaxObject.newOk("成功绑定菜单角色信息").toJson();
    }
    
    /***
     * 查询操作员
     * @param operatorId
     * @return
     */
    public String webFindOperatorById(Long operatorId){
        
        return AjaxObject.newOk("查询操作员",operatorRequestService.findOperatorById(operatorId)).toJson();
    }
    
    /****
     * 获取当前登录机构下的所有操作员
     */
    @Override
    public String webFindCustOperator() {
        return AjaxObject.newOk("查询当前登录机构下的操作员", operatorRequestService.findCustOperator()).toJson();
    }
    
    /****
     * 修改密码
     * @param anNewPasswd 新密码
     * @param anOkPasswd  确认密码
     * @param anPasswd 原密码
     * @return
     */
    public String webUpdatePassword(String anNewPasswd,String anOkPasswd,String anPasswd){
        if(operatorRequestService.updatePasword(anNewPasswd,anOkPasswd,anPasswd)){
            return AjaxObject.newOk("密码修改成功").toJson();
        }else{
            return AjaxObject.newError("密码修改失败").toJson();
        }
    }
    
    /***
     * 密码重置
     * @param id
     * @param password
     * @param okPasswd
     * @return
     */
    public String webChangeUserPassword(Long anId, String anPassword, String anOkPasswd){
        if(operatorRequestService.saveChangePassword(anId,anPassword,anOkPasswd)){
            return AjaxObject.newOk("密码修改成功").toJson();
        }else{
            return AjaxObject.newError("密码修改失败").toJson();
        }
    }
}
