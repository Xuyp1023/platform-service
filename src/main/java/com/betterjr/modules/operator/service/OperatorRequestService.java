package com.betterjr.modules.operator.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.betterjr.common.data.CustPasswordType;
import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.mapper.BeanMapper;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.account.dao.CustOperatorInfoMapper;
import com.betterjr.modules.account.data.CustOptData;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.entity.CustOperatorInfoRequest;
import com.betterjr.modules.account.service.CustPassService;

/***
 * 操作员管理
 * @author hubl
 *
 */
@Service
public class OperatorRequestService extends BaseService<CustOperatorInfoMapper, CustOperatorInfo> {
    
    private static String[] queryConds = new String[] { "name", "operCode", "phone", "status" };

    private final Logger logger = LoggerFactory.getLogger(OperatorRequestService.class);
    
    @Autowired
    private OperatorService custOptService;
    @Autowired
    private SysOperatorRoleRelationService operatorRoleRelationService;
    @Autowired
    private CustPassService custPassService;
    
    /**
     * 新增操作员
     * 
     * @param anMap
     * @return
     */
    public CustOptData saveCustOperator(CustOperatorInfoRequest request) {
        boolean optExists = this.custOptService.checkOperatorExists(request.getContIdentType(), request.getContIdentNo());
        if (optExists) {
            throw new BytterTradeException(40001, "抱歉，该证件号码已存在");
        }
        // 判断该操作员是否存在
        boolean operCodeExists = this.custOptService.checkExistsByOperCodeAndOperOrg(request.getOperCode(), request.getOperOrg());
        if (operCodeExists) {
            throw new BytterTradeException(401, "抱歉，该操作员用户名存在【" + request.getOperCode() + "】");
        }
        if (BetterStringUtils.isBlank(request.getRuleList())) {
            logger.error("角色不能为空");
            throw new BytterTradeException(40001, "抱歉，角色不能为空");
        }
        CustOperatorInfo custOperator = (CustOperatorInfo) UserUtils.getPrincipal().getUser();
        String operOrg = custOperator.getOperOrg();
        request.setOperOrg(operOrg);
        int res=custOptService.addCustOperator(request);
        if(res==0){
            throw new BytterTradeException(40001, "新增操作员失败");
        }
        CustOptData workData = BeanMapper.map(request, CustOptData.class);
        logger.info("新增操作员对象："+workData);
        return workData;
    }
    
    /**
     * 修改操作员
     * 
     * @param anMap
     * @return
     */
    public CustOptData updateCustOperator(CustOperatorInfoRequest request) {
        CustOperatorInfo operator = BeanMapper.map(request, CustOperatorInfo.class);
        if (operator.getId() == null) {
            throw new BytterTradeException(40001, "抱歉，操作员编号不能为空");
        }
        // 操作员角色信息绑定修改
        operatorRoleRelationService.saveSysOperatorRoleRelation(operator.getId(), operator.getRuleList());
        operator.setRuleList("");
        this.updateByPrimaryKeySelective(operator);
        CustOptData workData = BeanMapper.map(operator, CustOptData.class);
        return workData;
    }
    
    /***
     * 操作员分页查询
     * @param anParam
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<CustOptData> queryCustOperator(Map<String, String> anParam, int pageNum, int pageSize) {
        Map<String, Object> map = new HashMap<String, Object>();
        String tmpValue;
        for (String tmpKey : queryConds) {
            tmpValue = anParam.get(tmpKey);
            if (BetterStringUtils.isNotBlank(tmpValue)) {
                map.put(tmpKey, tmpValue);
            }
        }
        CustOperatorInfo custOperator = (CustOperatorInfo) UserUtils.getPrincipal().getUser();
        String operOrg = custOperator.getOperOrg();
        map.put("operOrg", operOrg);
        Page page = this.selectPropertyByPage(CustOperatorInfo.class, map, pageNum, pageSize, false);
        List list = page.getResult();
        List result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CustOperatorInfo custOperatorInfo = (CustOperatorInfo) list.get(i);
            String ruleList = operatorRoleRelationService.findSysRoleByOperatorId(custOperatorInfo.getId());
            if (BetterStringUtils.isNotBlank(ruleList)) {
                custOperatorInfo.setRuleList(ruleList);
            }
            result.add(custOperatorInfo);
        }
        return Page.listToPage(result);
    }
    
    /***
     * 查询操作员信息
     * @param operatorId
     * @return
     */
    public CustOperatorInfo findOperatorById(Long operatorId){
        CustOperatorInfo custOperatorInfo=this.selectByPrimaryKey(operatorId);
        String ruleList = operatorRoleRelationService.findSysRoleByOperatorId(custOperatorInfo.getId());
        if (BetterStringUtils.isNotBlank(ruleList)) {
            custOperatorInfo.setRuleList(ruleList);
        }
        return custOperatorInfo;
    }
    

    /***
     * 查找当前登录机构操作员
     * @return
     */
    public List<CustOptData> findCustOperator() {
        List result = new ArrayList<>();
        Map<String, Object> map = new HashMap<String, Object>();
        CustOperatorInfo custOperator = (CustOperatorInfo) UserUtils.getPrincipal().getUser();
        String operOrg = custOperator.getOperOrg();
        map.put("operOrg", operOrg);
        List<CustOperatorInfo> list=this.selectByProperty(map);
        for (int i = 0; i < list.size(); i++) {
            CustOperatorInfo custOperatorInfo = (CustOperatorInfo) list.get(i);
            String ruleList = operatorRoleRelationService.findSysRoleByOperatorId(custOperatorInfo.getId());
            if (BetterStringUtils.isNotBlank(ruleList)) {
                custOperatorInfo.setRuleList(ruleList);
            }
            result.add(custOperatorInfo);
        }
        return result;
    }
    
    /***
     * 修改密码
     * @param anNewPasswd
     * @param anOkPasswd
     * @param anPasswd
     * @return
     */
    public boolean updatePasword(String anNewPasswd,String anOkPasswd,String anPasswd){
        try {
            return custPassService.savePassword(CustPasswordType.ORG, anNewPasswd, anOkPasswd, anPasswd);
        }
        catch (Exception e) {
            throw new BytterTradeException(e.getMessage());
        }
    }
    
    /***
     * 密码重置
     * @param anOperId
     * @param anPassword
     * @param anOkPasswd
     * @return
     */
    public boolean saveChangePassword(Long anOperId, String anPassword, String anOkPasswd) {
        if (checkOperator(anOperId, null)) {
            if (BetterStringUtils.isNotBlank(anPassword) && anPassword.equals(anOkPasswd)) {

                return this.custPassService.saveChangePassword(anOperId, anPassword, CustPasswordType.ORG);
            }
        }
        return false;
    }
    
    /**
     * 检查管理员是否有权限修改操作员信息，只有同机构的管理员才能修改自己的操作员信息
     * @param anOperId 操作员ID号
     * @param anOperOrg 操作机构
     * @return
     */
    protected boolean checkOperator(Long anOperId, String anOperOrg) {
        CustOperatorInfo user = UserUtils.getOperatorInfo();
        if (BetterStringUtils.isNotBlank(anOperOrg) && user.getOperOrg().equals(anOperOrg)) {

            return true;
        }
        CustOperatorInfo tmpUser = this.selectByPrimaryKey(anOperId);
        if (tmpUser != null){
            
            return tmpUser.getOperOrg().equals(user.getOperOrg());
        }
        
        return false;
    }
}
