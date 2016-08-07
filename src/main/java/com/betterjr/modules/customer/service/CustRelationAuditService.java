package com.betterjr.modules.customer.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.customer.dao.CustRelationAuditMapper;
import com.betterjr.modules.customer.entity.CustRelation;
import com.betterjr.modules.customer.entity.CustRelationAudit;

@Service
public class CustRelationAuditService extends BaseService<CustRelationAuditMapper, CustRelationAudit> {

    @Autowired
    private CustAccountService custAccountService;

    /**
     * 开通保理融资业务审批流程
     * 
     * @param anCustNo
     * @param anFlag
     * @param anPageNum
     * @param anPageSize
     * @return
     */
    public Page<CustRelationAudit> queryAuditWorkflow(Long anCustNo, String anFlag, int anPageNum, int anPageSize) {
        BTAssert.notNull(anCustNo, "请选择机构");
        Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("custNo", anCustNo);
        Page<CustRelationAudit> result = this.selectPropertyByPage(anMap, anPageNum, anPageSize, "1".equals(anFlag), "relateType,auditDate,auditTime");
        for (CustRelationAudit relation : result) {
            relation.setCustName(custAccountService.queryCustName(relation.getCustNo()));
        }
        return result;
    }

    public int addAuditCustRelation(CustRelation anCustRelation, String anAuditOpinion, String anTaskName) {
        CustRelationAudit relationAudit = new CustRelationAudit();
        relationAudit.initAuditValue(anCustRelation, anAuditOpinion, anTaskName);
        return this.insert(relationAudit);
    }

    public int addRefuseCustRelation(CustRelation anCustRelation, String anAuditOpinion, String anTaskName) {
        CustRelationAudit relationAudit = new CustRelationAudit();
        relationAudit.initRefuseValue(anCustRelation, anAuditOpinion, anTaskName);
        return this.insert(relationAudit);
    }

}
