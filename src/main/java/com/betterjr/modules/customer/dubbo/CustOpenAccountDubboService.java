package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.customer.ICustOpenAccountService;
import com.betterjr.modules.customer.entity.CustOpenAccountTmp;
import com.betterjr.modules.customer.service.CustOpenAccountAuditService;
import com.betterjr.modules.customer.service.CustOpenAccountTmpService;
import com.betterjr.modules.document.entity.CustFileItem;
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
    
    @Override
    public String webCheckCustExistsByCustName(String anCustName) {
        return AjaxObject.newOk("检查申请机构名称是否存在成功", custOpenAccountTmpService.checkCustExistsByCustName(anCustName) ? "1" : "0").toJson();
    }

    @Override
    public String webCheckCustExistsByIdentNo(String anIdentNo) {
        return AjaxObject.newOk("检查组织机构代码证是否存在", custOpenAccountTmpService.checkCustExistsByOrgCode(anIdentNo) ? "1" : "0").toJson();
    }

    @Override
    public String webCheckCustExistsByBusinLicence(String anBusinLicence) {
        return AjaxObject.newOk("检查营业执照号码是否存在", custOpenAccountTmpService.checkCustExistsByBusinLicence(anBusinLicence) ? "1" : "0").toJson();
    }

    @Override
    public String webCheckCustExistsByBankAccount(String anBankAccount) {
        return AjaxObject.newOk("检查银行账号是否存在", custOpenAccountTmpService.checkCustExistsByBankAccount(anBankAccount) ? "1" : "0").toJson();
    }

    @Override
    public String webCheckCustExistsByEmail(String anEmail) {
        return AjaxObject.newOk("检查电子邮箱是否存在", custOpenAccountTmpService.checkCustExistsByEmail(anEmail) ? "1" : "0").toJson();
    }

    @Override
    public String webCheckCustExistsByMobileNo(String anMobileNo) {
        return AjaxObject.newOk("检查手机号码是否存在", custOpenAccountTmpService.checkCustExistsByMobileNo(anMobileNo) ? "1" : "0").toJson();
    }
    
    @Override
    public String webSaveOpenAccountApplySubmit(Map<String, Object> anMap, Long anOperId, String anFileList) {
        CustOpenAccountTmp anOpenAccountInfo = (CustOpenAccountTmp) RuleServiceDubboFilterInvoker.getInputObj();
        try {
            CustOpenAccountTmp info = custOpenAccountTmpService.saveOpenAccountApplySubmit(anOpenAccountInfo, anOperId, anFileList);
            return AjaxObject.newOk("提交成功", info).toJson();
        }
        catch (Exception e) {
            return AjaxObject.newError(e.getMessage()).toJson();
        }
    }
    
    @Override
    public String webFindAccountTmpInfo(String anOpenId) {
        try {
            CustOpenAccountTmp info = custOpenAccountTmpService.findAccountTmpInfo(anOpenId);
            return AjaxObject.newOk("开户资料查询成功", info).toJson();
        }
        catch (Exception e) {
            return AjaxObject.newError(e.getMessage()).toJson();
        }
    }

    @Override
    public String webSaveSingleFileLink(Long anId, String anFileTypeName, String anFileMediaId) {
        try {
            CustFileItem info = custOpenAccountTmpService.saveSingleFileLink(anId, anFileTypeName, anFileMediaId);
            return AjaxObject.newOk("开户资料附件保存成功", info).toJson();
        }
        catch (Exception e) {
            return AjaxObject.newError(e.getMessage()).toJson();
        }
    }

    @Override
    public String webFindAccountFileByBatChNo(Long anBatchNo) {
        try {
            Map<String, Object> info = custOpenAccountTmpService.findAccountFileByBatChNo(anBatchNo);
            return AjaxObject.newOk("开户信息附件查询成功", info).toJson();
        }
        catch (Exception e) {
            return AjaxObject.newError(e.getMessage()).toJson();
        }
    }

    @Override
    public String findOpenAccountStatus(String anOpenId) {
        return custOpenAccountTmpService.findOpenAccountStatus(anOpenId);
    }

    @Override
    public String webSendValidMessage(String anMobileNo) {
        return custOpenAccountTmpService.sendValidMessage(anMobileNo);
    }

    @Override
    public String webFindInsteadApplyStatus() {
        return AjaxObject.newOk("开户申请状态查询成功", custOpenAccountTmpService.findInsteadApplyStatus()).toJson();
    }

    @Override
    public String webFindOpenAccoutnTmp() {
        return AjaxObject.newOk("开户信息查询成功", custOpenAccountTmpService.findOpenAccoutnTmp()).toJson();
    }

    @Override
    public String webDeleteSingleFile(Long anId) {
        return AjaxObject.newOk("删除附件成功", custOpenAccountTmpService.saveDeleteSingleFile(anId)).toJson();
    }

    @Override
    public String webFindSuccessAccountInfo(String anOpenId) {
        return AjaxObject.newOk("开户资料读取成功", custOpenAccountTmpService.findSuccessAccountInfo(anOpenId)).toJson();
    }
    
    @Override 
    public CustOpenAccountTmp findAccountInfoByCustNo(Long anCustNo) {
        return custOpenAccountTmpService.findAccountInfoByCustNo(anCustNo);
    }
    
    @Override
    public String webQueryCustInfoByPlatform(final String anFlag, final int anPageNum, final int anPageSize) {
      return AjaxObject.newOkWithPage("保理公司查询客户信息成功", custOpenAccountTmpService.queryCustInfoByPlatform(anFlag, anPageNum, anPageSize)).toJson();  
    }
}
