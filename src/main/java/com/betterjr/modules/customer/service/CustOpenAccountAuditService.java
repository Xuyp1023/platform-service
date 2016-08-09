package com.betterjr.modules.customer.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.customer.dao.CustOpenAccountAuditMapper;
import com.betterjr.modules.customer.entity.CustOpenAccountAudit;
import com.betterjr.modules.customer.entity.CustOpenAccountTmp;

@Service
public class CustOpenAccountAuditService extends BaseService<CustOpenAccountAuditMapper, CustOpenAccountAudit> {

    @Autowired
    private CustAccountService custAccountService;

    @Autowired
    private CustOpenAccountTmpService custOpenAccountTmpService;

    /**
     * 客户开户审批流程
     * 
     * @param anCustNo
     * @param anFlag
     * @param anPageNum
     * @param anPageSize
     * @return
     */
    public Page<CustOpenAccountAudit> queryAuditWorkflow(Long anCustNo, String anFlag, int anPageNum, int anPageSize) {
        BTAssert.notNull(anCustNo, "请选择机构");
        CustOpenAccountTmp anOpenAccountInfo = Collections3.getFirst(custOpenAccountTmpService.selectByProperty("custNo", anCustNo));
        BTAssert.notNull(anOpenAccountInfo, "无法获取客户开户资料信息");
        Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("sourceId", anOpenAccountInfo.getId());
        return this.selectPropertyByPage(anMap, anPageNum, anPageSize, "1".equals(anFlag), "auditDate,auditTime");
    }

    public int addInitOpenAccountApplyLog(Long anSourceId, String anAuditOpinion, String anTaskName) {
        CustOpenAccountAudit anOpenAccountAudit = new CustOpenAccountAudit();
        anOpenAccountAudit.initAuditValue(anSourceId, anAuditOpinion, anTaskName);
        return this.insert(anOpenAccountAudit);
    }

    public int addAuditOpenAccountApplyLog(Long anSourceId, String anAuditOpinion, String anTaskName) {
        CustOpenAccountAudit anOpenAccountAudit = new CustOpenAccountAudit();
        anOpenAccountAudit.initAuditValue(anSourceId, anAuditOpinion, anTaskName);
        initCustInfo(anOpenAccountAudit);
        return this.insert(anOpenAccountAudit);
    }

    public int addRefuseOpenAccountApplyLog(Long anSourceId, String anAuditOpinion, String anTaskName) {
        CustOpenAccountAudit anOpenAccountAudit = new CustOpenAccountAudit();
        anOpenAccountAudit.initRefuseValue(anSourceId, anAuditOpinion, anTaskName);
        initCustInfo(anOpenAccountAudit);
        return this.insert(anOpenAccountAudit);
    }

    private void initCustInfo(CustOpenAccountAudit anOpenAccountAudit) {
        CustInfo custInfo = Collections3.getFirst(custAccountService.selectByProperty("operOrg", UserUtils.getOperatorInfo().getOperOrg()));
        anOpenAccountAudit.setAuditCustNo(custInfo.getCustNo());
        anOpenAccountAudit.setAuditCustname(custInfo.getCustName());
    }

}
