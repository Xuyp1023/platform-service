package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.customer.ICustMechManagerService;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.entity.CustMechManagerTmp;
import com.betterjr.modules.customer.service.CustChangeService;
import com.betterjr.modules.customer.service.CustInsteadService;
import com.betterjr.modules.customer.service.CustMechManagerService;
import com.betterjr.modules.customer.service.CustMechManagerTmpService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

/**
 * 高管
 * 
 * @author liuwl
 *
 */
@Service(interfaceClass = ICustMechManagerService.class)
public class CustMechManagerDubboService implements ICustMechManagerService {

    @Resource
    private CustMechManagerService managerService;
    
    @Resource
    private CustMechManagerTmpService managerTmpService;

    @Resource
    private CustChangeService changeService;

    @Resource
    private CustInsteadService insteadService;

    @Override
    public String webAddInsteadManagerTmp(Map<String, Object> anMap) {
        final CustMechManagerTmp custMechManagerTmp = (CustMechManagerTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("公司高管-流水信息 代录添加成功", managerTmpService.addCustMechManagerTmp(custMechManagerTmp, CustomerConstants.TMP_TYPE_INSTEAD)).toJson();
    }
    
    @Override
    public String webSaveInsteadManagerTmp(Map<String, Object> anParam, Long anId) {
        final CustMechManagerTmp custMechManagerTmp = (CustMechManagerTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("公司高管-流水信息 代录修改成功", managerTmpService.saveCustMechManagerTmp(custMechManagerTmp)).toJson();
    }
    
    @Override
    public String webDelInsteadManagerTmp(Map<String, Object> anParam) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String webAddChangeManagerTmp(Map<String, Object> anMap) {
        final CustMechManagerTmp custMechManagerTmp = (CustMechManagerTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("公司高管-流水信息 变更添加成功", managerTmpService.addCustMechManagerTmp(custMechManagerTmp, CustomerConstants.TMP_TYPE_CHANGE)).toJson();
    }
    
    @Override
    public String webSaveChangeManagerTmp(Map<String, Object> anParam, Long anId) {
        final CustMechManagerTmp custMechManagerTmp = (CustMechManagerTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("公司高管-流水信息 添加成功", managerTmpService.saveCustMechManagerTmp(custMechManagerTmp)).toJson();
    }
    
    @Override
    public String webDelChangeManagerTmp(Map<String, Object> anParam) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String webAddChangeApply(Map<String, Object> anParam, Long anCustNo) {
        return AjaxObject.newOk("公司高管-变更申请 成功", managerTmpService.addChangeApply(anParam, anCustNo)).toJson();
    }

    @Override
    public String webQueryManagerTmpList(Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webDelManagerTmpList(Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webCheckManagerTmpList(Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public String webQueryManager(Long anCustNo) {
        return AjaxObject.newOk("查询公司高管列表成功", managerService.queryCustMechManager(anCustNo)).toJson();
    }

    @Override
    public String webFindManager(Long anId) {
        return AjaxObject.newOk("查询公司高管详情成功", managerService.findCustMechManager(anId)).toJson();
    }

    @Override
    public String webFindChangeApply(Long anCustNo, Long anId) {
        return null;
    }

    @Override
    public String webQueryChangeApply(Long anCustNo) {
        return null;
    }

    @Override
    public String webConfirmChangeApply(Long anChangeId, Long anCustNo) {
        return null;
    }

    @Override
    public String webCancelChangeApply(Long anChangeId, Long anCustNo) {
        return null;
    }

    @Override
    public String webAddInsteadRecord(Map<String, Object> anParam, Long anCustNo, Long anId, Long anOperType) {
        return null;
    }

    @Override
    public String webQueryInsteadRecord(Long anInsteadId, Long anCustNo) {
        return null;
    }

    @Override
    public String webFindInsteadRecord(Long anCustNo, Long anId) {
        return null;
    }

    @Override
    public String webConfirmInsteadRecord(Long anInsteadId, Long anCustNo) {
        return null;
    }

    @Override
    public String webCancelInsteadRecord(Long anInsteadId, Long anCustNo) {
        return null;
    }
    
    


}
