package com.betterjr.modules.customer.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.dao.CustRelationAuditMapper;
import com.betterjr.modules.customer.entity.CustRelation;
import com.betterjr.modules.customer.entity.CustRelationAudit;

@Service
public class CustRelationAuditService extends BaseService<CustRelationAuditMapper, CustRelationAudit> {

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
        anMap.put("relateType", new String[] { CustomerConstants.RELATE_TYPE_SUPPLIER_FACTOR, CustomerConstants.RELATE_TYPE_CORE_FACTOR,
                CustomerConstants.RELATE_TYPE_SELLER_FACTOR });
        return this.selectPropertyByPage(anMap, anPageNum, anPageSize, "1".equals(anFlag), "relateType,auditDate,auditTime");
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
