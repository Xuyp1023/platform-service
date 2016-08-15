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
     * 获取菜单信息
     * @param menuId
     * @return
     */
    public String webFindSysMenuByMenuId(Integer anMenuId,String anRoleName){
        return AjaxObject.newOk("获取菜单信息",manuManagerService.findSysMenuByMenuId(anMenuId,anRoleName)).toJson();
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
}
