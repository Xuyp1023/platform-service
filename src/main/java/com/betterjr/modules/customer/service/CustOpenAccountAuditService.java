package com.betterjr.modules.customer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
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
     * 开户审批流程查询
     * 
     * @param anCustNo
     * @return
     */
    public List<CustOpenAccountAudit> queryAuditWorkflow(Long anCustNo) {
        BTAssert.notNull(anCustNo, "请选择机构");
        CustOpenAccountTmp anOpenAccountInfo = Collections3
                .getFirst(custOpenAccountTmpService.selectByProperty("custNo", anCustNo));
        BTAssert.notNull(anOpenAccountInfo, "无法获取客户开户资料信息");
        return queryAuditWorkflow(anOpenAccountInfo.getId());
    }

    /**
     * 开户审批流程查询
     * 
     * @param anOpenAccountId:开户资料流水号
     * @return
     */
    public List<CustOpenAccountAudit> queryAuditWorkflowById(Long anOpenAccountId) {
        BTAssert.notNull(anOpenAccountId, "开户资料流水号不能为空");
        return this.selectByProperty("sourceId", anOpenAccountId, "auditDate,auditTime");
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
        CustInfo custInfo = Collections3
                .getFirst(custAccountService.selectByProperty("operOrg", UserUtils.getOperatorInfo().getOperOrg()));
        anOpenAccountAudit.setAuditCustNo(custInfo.getCustNo());
        anOpenAccountAudit.setAuditCustname(custInfo.getCustName());
    }

}
