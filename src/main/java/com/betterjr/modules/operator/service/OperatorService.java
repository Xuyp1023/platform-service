package com.betterjr.modules.operator.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.modules.account.dao.CustOperatorInfoMapper;
import com.betterjr.modules.account.data.CustContextInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.entity.CustOperatorInfoRequest;
import com.betterjr.modules.account.entity.CustPassInfo;
import com.betterjr.modules.account.service.CustOperatorHelper;
import com.betterjr.modules.account.service.CustPassService;
import com.betterjr.common.utils.UserUtils;

@Service
public class OperatorService extends BaseService<CustOperatorInfoMapper, CustOperatorInfo> {

    @Autowired
    private CustOperatorHelper requestHelper;
    @Autowired
    private CustPassService custPassService;
    @Autowired
    private SysOperatorRoleRelationService operatorRoleRelationService;

    public boolean isFirstOperator(String anOperOrg) {

        int kk = this.selectCountByProperty("operOrg", anOperOrg);
        return kk == 0;
    }

    public boolean isManager(Long anOperID) {
        CustOperatorInfo custOper = this.selectByPrimaryKey(anOperID);
        if (custOper != null && custOper.getDefOper() != null) {
            return custOper.getDefOper();
        }
        else {
            return false;
        }
    }

    public static Object workForCustNo(String anValue) {
        if (BetterStringUtils.isNotBlank(anValue)) {
            return new Long(anValue);
        }
        else {
            return findCustNoList();
        }
    }

    public static List findCustNoList() {
        CustContextInfo contextInfo = UserUtils.getOperatorContextInfo();
        List custList; 
        if (contextInfo == null) {
            custList = new ArrayList(1);
        }
        else {
            custList = contextInfo.findCustList();
        }
        if (custList.size() == 0){
            custList.add(-1L);
        }
        return custList;
    }

    public CustOperatorInfo findCustOperatorByIndentInfo(String anIndentType, String anUserIdentNo) {
        Map<String, Object> map = new HashMap();
        map.put("identNo", anUserIdentNo);
        map.put("identType", anIndentType);
        map.put("status", "1");
        // return operatorMapper.findCustOperatorByIndentInfo(anIndentType, anUserIdentNo);
        List<CustOperatorInfo> list = this.selectByProperty(map);
        if (list.size() > 0) {

            return list.get(0);
        }

        return null;
    }

    public CustOperatorInfo findCustOperatorByOperCode(String anOperOrg, String anOperCode) {
        Map<String, Object> map = new HashMap();
        map.put("operOrg", anOperOrg);
        map.put("operCode", anOperCode);
        // return operatorMapper.findCustOperatorByOperCode(anOperOrg, anOperCode);
        List<CustOperatorInfo> list = this.selectByProperty(map);
        if (list.size() > 0) {

            return list.get(0);
        }
        return null;
    }

    public int addCustOperator(CustOperatorInfoRequest request) {
        CustOperatorInfo operator = new CustOperatorInfo(request);
        CustPassInfo custPassInfo = new CustPassInfo(operator, request.getPassword());
        // 操作员角色信息绑定修改
        operatorRoleRelationService.saveSysOperatorRoleRelation(operator.getId(), operator.getRuleList());
        this.custPassService.insert(custPassInfo);
        operator.setRuleList("");
        return this.insert(operator);
    }

    public void insertCustPass(CustPassInfo custPssInfo) {
        this.custPassService.insert(custPssInfo);
    }

    public boolean checkOperatorExists(String identType, String identNo) {
        if (BetterStringUtils.isNotBlank(identType) && BetterStringUtils.isNotBlank(identType)) {
            Map<String, Object> map = new HashMap();
            map.put("identType", identType);
            map.put("identNo", identNo);
            List list = this.selectByProperty(map);

            return list.size() > 0;
        }

        return false;
    }

    public boolean checkExistsByOperCodeAndOperOrg(String operCode, String operOrg) {
        if (BetterStringUtils.isNotBlank(operCode) && BetterStringUtils.isNotBlank(operOrg)) {
            Map<String, Object> map = new HashMap();
            map.put("operCode", operCode);
            map.put("operOrg", operOrg);
            List list = this.selectByProperty(map);

            return list.size() > 0;
        }

        return false;
    }
    
    public CustOperatorInfo queryCustOperatorInfo(Long operId){
        return this.selectByPrimaryKey(operId);
    }
}

