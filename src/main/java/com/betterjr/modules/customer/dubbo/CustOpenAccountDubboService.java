package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.customer.ICustOpenAccountService;
import com.betterjr.modules.customer.entity.CustOpenAccountTmp;
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

    @Override
    public String webFindOpenAccountInfo() {

        return AjaxObject.newOk("开户资料读取成功", custOpenAccountTmpService.findOpenAccountInfoByOperator()).toJson();
    }

    @Override
    public String webSaveOpenAccountInfo(Map<String, Object> anMap) {

        CustOpenAccountTmp anOpenAccountInfo = (CustOpenAccountTmp) RuleServiceDubboFilterInvoker.getInputObj();

        return AjaxObject.newOk("开户资料暂存成功", custOpenAccountTmpService.saveOpenAccountInfo(anOpenAccountInfo)).toJson();
    }

    public String webSaveOpenAccountApply(Map<String, Object> anMap, Long anId) {

        CustOpenAccountTmp anOpenAccountInfo = (CustOpenAccountTmp) RuleServiceDubboFilterInvoker.getInputObj();

        return AjaxObject.newOk("开户申请提交成功", custOpenAccountTmpService.saveOpenAccountApply(anOpenAccountInfo, anId)).toJson();
    }

    public String webQueryOpenAccountApply(String anFlag, int anPageNum, int anPageSize) {

        return AjaxObject.newOk("开户申请待审批列表查询成功", custOpenAccountTmpService.queryOpenAccountApply(anFlag, anPageNum, anPageSize)).toJson();
    }

    public String webSaveAuditOpenAccountApply(Long anId, String anAuditOpinion) {

        return AjaxObject.newOk("开户审核生效", custOpenAccountTmpService.saveAuditOpenAccountApply(anId, anAuditOpinion)).toJson();
    }

    public String webSaveRefuseOpenAccountApply(Long anId, String anAuditOpinion) {

        return AjaxObject.newOk("开户申请驳回", custOpenAccountTmpService.saveRefuseOpenAccountApply(anId, anAuditOpinion)).toJson();
    }

    @Override
    public String webSaveOpenAccountInfoByInstead(Map<String, Object> anMap, Long anInsteadId) {

        CustOpenAccountTmp anOpenAccountInfo = (CustOpenAccountTmp) RuleServiceDubboFilterInvoker.getInputObj();

        return AjaxObject.newOk("代录开户资料提交成功", custOpenAccountTmpService.saveOpenAccountInfoByInstead(anOpenAccountInfo, anInsteadId)).toJson();
    }

    @Override
    public String webFindOpenAccountInfoByInsteadId(Long anInsteadId) {

        return AjaxObject.newOk("代录开户资料读取成功", custOpenAccountTmpService.findOpenAccountInfoByInsteadId(anInsteadId)).toJson();
    }

}
