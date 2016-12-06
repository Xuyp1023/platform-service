package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.customer.ICustOpenAccountService2;
import com.betterjr.modules.customer.entity.CustOpenAccountTmp;
import com.betterjr.modules.customer.service.CustOpenAccountTmp2Service;
import com.betterjr.modules.document.entity.CustFileItem;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

@Service(interfaceClass = ICustOpenAccountService2.class)
public class CustOpenAccountDubboService2 implements ICustOpenAccountService2 {

    @Autowired
    private CustOpenAccountTmp2Service custOpenAccountTmpService;

    @Override
    public String webSaveOpenAccountApply(Map<String, Object> anMap, Long anOperId, String anFileList) {
        CustOpenAccountTmp anOpenAccountInfo = (CustOpenAccountTmp) RuleServiceDubboFilterInvoker.getInputObj();
        try {
            CustOpenAccountTmp info = custOpenAccountTmpService.saveOpenAccountApply(anOpenAccountInfo, anOperId, anFileList);
            return AjaxObject.newOk("提交成功", info).toJson();
        }
        catch (Exception e) {
            return AjaxObject.newError(e.getMessage()).toJson();
        }

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
    public String webSaveModifyOpenAccount(Map<String, Object> anAnMap, Long anId, String anFileList) {
        CustOpenAccountTmp anOpenAccountInfo = (CustOpenAccountTmp) RuleServiceDubboFilterInvoker.getInputObj();
        try {
            CustOpenAccountTmp info = custOpenAccountTmpService.saveModifyOpenAccount(anOpenAccountInfo, anId, anFileList);
            return AjaxObject.newOk("开户信息修改成功", info).toJson();
        }
        catch (Exception e) {
            return AjaxObject.newError(e.getMessage()).toJson();
        }
    }

    @Override
    public String webSaveOpenAccountInfoByInstead(Map<String, Object> anMap, Long anInsteadRecordId, String anFileList) {
        CustOpenAccountTmp anOpenAccountInfo = (CustOpenAccountTmp) RuleServiceDubboFilterInvoker.getInputObj();
        try {
            CustOpenAccountTmp info = custOpenAccountTmpService.saveOpenAccountInfoByInstead(anOpenAccountInfo, anInsteadRecordId, anFileList);
            return AjaxObject.newOk("代录开户资料提交成功", info).toJson();
        }
        catch (Exception e) {
            return AjaxObject.newError(e.getMessage()).toJson();
        }
    }

    @Override
    public String webSaveOpenAccountInfo(Map<String, Object> anMap, Long anId, String anFileList) {

        CustOpenAccountTmp anOpenAccountInfo = (CustOpenAccountTmp) RuleServiceDubboFilterInvoker.getInputObj();

        try {
            CustOpenAccountTmp info = custOpenAccountTmpService.saveOpenAccountInfo(anOpenAccountInfo, anId, anFileList);
            return AjaxObject.newOk("开户资料暂存成功", info).toJson();
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
}
