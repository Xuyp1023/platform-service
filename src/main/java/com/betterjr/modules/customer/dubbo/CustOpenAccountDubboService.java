package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.customer.ICustOpenAccountService;
import com.betterjr.modules.customer.entity.CustOpenAccountTmp;
import com.betterjr.modules.customer.service.CustOpenAccountAuditService;
import com.betterjr.modules.customer.service.CustOpenAccountTmpService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

/**
 * 开户流水
 *
 * @author liuwl
 *
 */
@Service(interfaceClass = ICustOpenAccountService.class)
public class CustOpenAccountDubboService implements ICustOpenAccountService {

    @Autowired
    private CustOpenAccountTmpService custOpenAccountTmpService;

    @Autowired
    private CustOpenAccountAuditService custOpenAccountAuditService;

    @Override
    public String webFindOpenAccountInfo() {

        return AjaxObject.newOk("开户资料读取成功", custOpenAccountTmpService.findOpenAccountInfo()).toJson();
    }

    @Override
    public String webFindOpenAccountInfo(Long anId) {

        return AjaxObject.newOk("开户资料读取成功", custOpenAccountTmpService.findOpenAccountInfo(anId)).toJson();
    }

    @Override
    public String webSaveOpenAccountInfo(Map<String, Object> anMap, Long anId, String anFileList) {

        CustOpenAccountTmp anOpenAccountInfo = (CustOpenAccountTmp) RuleServiceDubboFilterInvoker.getInputObj();

        return AjaxObject.newOk("开户资料暂存成功", custOpenAccountTmpService.saveOpenAccountInfo(anOpenAccountInfo, anId, anFileList)).toJson();
    }

    @Override
    public String webSaveOpenAccountApply(Map<String, Object> anMap, Long anId, String anFileList) {

        CustOpenAccountTmp anOpenAccountInfo = (CustOpenAccountTmp) RuleServiceDubboFilterInvoker.getInputObj();

        return AjaxObject.newOk("开户申请提交成功", custOpenAccountTmpService.saveOpenAccountApply(anOpenAccountInfo, anId, anFileList)).toJson();
    }

    @Override
    public String webQueryOpenAccountApply(String anFlag, int anPageNum, int anPageSize) {

        return AjaxObject.newOkWithPage("开户申请待审批列表查询成功", custOpenAccountTmpService.queryOpenAccountApply(anFlag, anPageNum, anPageSize)).toJson();
    }

    @Override
    public String webSaveAuditOpenAccountApply(Long anId, String anAuditOpinion) {

        return AjaxObject.newOk("开户审核生效", custOpenAccountTmpService.saveAuditOpenAccountApply(anId, anAuditOpinion)).toJson();
    }

    @Override
    public String webSaveRefuseOpenAccountApply(Long anId, String anAuditOpinion) {

        return AjaxObject.newOk("开户申请驳回", custOpenAccountTmpService.saveRefuseOpenAccountApply(anId, anAuditOpinion)).toJson();
    }

    @Override
    public String webSaveOpenAccountInfoByInstead(Map<String, Object> anMap, Long anInsteadRecordId, String anFileList) {

        CustOpenAccountTmp anOpenAccountInfo = (CustOpenAccountTmp) RuleServiceDubboFilterInvoker.getInputObj();

        return AjaxObject.newOk("代录开户资料提交成功", custOpenAccountTmpService.saveOpenAccountInfoByInstead(anOpenAccountInfo, anInsteadRecordId, anFileList))
                .toJson();
    }

    @Override
    public String webFindOpenAccountInfoByInsteadId(Long anInsteadId) {

        return AjaxObject.newOk("代录开户资料读取成功", custOpenAccountTmpService.findOpenAccountInfoByInsteadId(anInsteadId)).toJson();
    }

    @Override
    public String webQueryAuditWorkflow(Long anCustNo) {

        return AjaxObject.newOk("开户审批流程查询成功", custOpenAccountAuditService.queryAuditWorkflow(anCustNo)).toJson();
    }

    @Override
    public String webQueryAuditWorkflowById(Long anOpenAccountId) {

        return AjaxObject.newOk("开户审批流程查询成功", custOpenAccountAuditService.queryAuditWorkflowById(anOpenAccountId)).toJson();
    }

    @Override
    public Map<String, Object> findOpenTempAccountInfo(Long anCustNo){

        return custOpenAccountTmpService.findOpenTempAccountInfo(anCustNo);
    }
}
