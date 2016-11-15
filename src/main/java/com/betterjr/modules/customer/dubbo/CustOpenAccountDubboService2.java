package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.customer.ICustOpenAccountService2;
import com.betterjr.modules.customer.entity.CustOpenAccountTmp;
import com.betterjr.modules.customer.service.CustOpenAccountTmpService2;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

@Service(interfaceClass = ICustOpenAccountService2.class)
public class CustOpenAccountDubboService2 implements ICustOpenAccountService2{
    
    @Autowired
    private CustOpenAccountTmpService2 custOpenAccountTmpService2;

    @Override
    public String webSaveOpenAccountApply(Map<String, Object> anMap, String anFileList) {
        CustOpenAccountTmp anOpenAccountInfo = (CustOpenAccountTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("开户申请提交成功", custOpenAccountTmpService2.saveOpenAccountApply(anOpenAccountInfo, anFileList)).toJson();
    }
    
    @Override
    public String webCheckCustExistsByCustName(String anCustName) {
        return AjaxObject.newOk("检查申请机构名称是否存在成功", custOpenAccountTmpService2.checkCustExistsByCustName(anCustName)? "1" : "0").toJson();
    }

    @Override
    public String webCheckCustExistsByIdentNo(String anIdentNo) {
       return AjaxObject.newOk("检查组织机构代码证是否存在", custOpenAccountTmpService2.checkCustExistsByIdentNo(anIdentNo)? "1" : "0").toJson();
    }

    @Override
    public String webCheckCustExistsByBusinLicence(String anBusinLicence) {
        return AjaxObject.newOk("检查营业执照号码是否存在", custOpenAccountTmpService2.checkCustExistsByBusinLicence(anBusinLicence)? "1" : "0").toJson();
    }

    @Override
    public String webCheckCustExistsByBankAccount(String anBankAccount) {
        return AjaxObject.newOk("检查银行账号是否存在", custOpenAccountTmpService2.checkCustExistsByBankAccount(anBankAccount)? "1" : "0").toJson();
    }
    
    @Override
    public String webCheckCustExistsByEmail(String anEmail) {
        return AjaxObject.newOk("检查电子邮箱是否存在", custOpenAccountTmpService2.checkCustExistsByEmail(anEmail)? "1" : "0").toJson();
    }
    
    @Override
    public String webCheckCustExistsByMobileNo(String anMobileNo) {
        return AjaxObject.newOk("检查手机号码是否存在", custOpenAccountTmpService2.checkCustExistsByMobileNo(anMobileNo)? "1" : "0").toJson();
    }

    @Override
    public String webSaveModifyOpenAccount(Map<String, Object> anAnMap, Long anId, String anFileList) {
        CustOpenAccountTmp anOpenAccountInfo = (CustOpenAccountTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("开户信息修改成功", custOpenAccountTmpService2.saveModifyOpenAccount(anOpenAccountInfo, anId, anFileList)).toJson();
    }

}
