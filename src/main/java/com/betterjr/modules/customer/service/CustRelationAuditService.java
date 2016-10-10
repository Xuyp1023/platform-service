package com.betterjr.modules.customer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.modules.customer.constants.CustomerConstants;
import com.betterjr.modules.customer.dao.CustRelationAuditMapper;
import com.betterjr.modules.customer.entity.CustRelation;
import com.betterjr.modules.customer.entity.CustRelationAudit;

@Service
public class CustRelationAuditService extends BaseService<CustRelationAuditMapper, CustRelationAudit> {

    /**
     * 开通保理融资业务审批流程
     * 
     * @param anCustNo
     * @return
     */
    public List<CustRelationAudit> queryAuditWorkflow(Long anCustNo) {
        BTAssert.notNull(anCustNo, "请选择机构");
        Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("custNo", anCustNo);
        anMap.put("relateType", new String[] { CustomerConstants.RELATE_TYPE_SUPPLIER_FACTOR, CustomerConstants.RELATE_TYPE_CORE_FACTOR,
                CustomerConstants.RELATE_TYPE_SELLER_FACTOR });
        return this.selectByProperty(anMap, "relateType,relateCustno,auditDate,auditTime");
    }

    public int addAuditCustRelation(CustRelation anCustRelation, String anAuditAgency, String anAuditOpinion, String anTaskName) {
        CustRelationAudit relationAudit = new CustRelationAudit();
        relationAudit.initAuditValue(anCustRelation, anAuditOpinion, anTaskName);
        relationAudit.setAuditAgency(anAuditAgency);
        return this.insert(relationAudit);
    }

    public int addRefuseCustRelation(CustRelation anCustRelation, String anAuditAgency, String anAuditOpinion, String anTaskName) {
        CustRelationAudit relationAudit = new CustRelationAudit();
        relationAudit.initRefuseValue(anCustRelation, anAuditOpinion, anTaskName);
        relationAudit.setAuditAgency(anAuditAgency);
        return this.insert(relationAudit);
    }

}
