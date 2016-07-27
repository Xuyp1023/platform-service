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

    public String webSaveOpenAccount(Map<String, Object> anMap, Long anId, String anFileList) {

        CustOpenAccountTmp anOpenAccountData = (CustOpenAccountTmp) RuleServiceDubboFilterInvoker.getInputObj();

        return AjaxObject.newOk("开户成功", custOpenAccountTmpService.saveOpenAccount(anOpenAccountData, anId, anFileList)).toJson();
    }

    @Override
    public String webFindOpenAccountTemp() {

        return AjaxObject.newOk("开户资料读取成功", custOpenAccountTmpService.findOpenAccountTemp()).toJson();
    }

    @Override
    public String webFindOpenAccountTempByInsteadId(Long anInsteadRecordId) {

        return AjaxObject.newOk("开户资料读取成功", custOpenAccountTmpService.findOpenAccountTempByInsteadId(anInsteadRecordId)).toJson();
    }

    @Override
    public String webSaveOpenAccountTemp(Map<String, Object> anMap, String anFileList) {

        CustOpenAccountTmp anOpenAccountData = (CustOpenAccountTmp) RuleServiceDubboFilterInvoker.getInputObj();

        return AjaxObject.newOk("开户资料暂存成功", custOpenAccountTmpService.saveOpenAccountTemp(anOpenAccountData, anFileList)).toJson();
    }

    @Override
    public String webSaveOpenAccountInsteadTemp(Map<String, Object> anMap, Long anInsteadRecordId, String anFileList) {

        CustOpenAccountTmp anOpenAccountData = (CustOpenAccountTmp) RuleServiceDubboFilterInvoker.getInputObj();

        return AjaxObject.newOk("开户资料暂存成功", custOpenAccountTmpService.saveOpenAccountInsteadTemp(anOpenAccountData, anInsteadRecordId, anFileList))
                .toJson();
    }

}
