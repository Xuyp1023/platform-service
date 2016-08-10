package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.customer.ICustMechManagerService;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.entity.CustChangeApply;
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
    public String webQueryManager(Long anCustNo) {
        return AjaxObject.newOk("查询公司高管列表成功", managerService.queryCustMechManager(anCustNo)).toJson();
    }

    @Override
    public String webFindManager(Long anId) {
        return AjaxObject.newOk("查询公司高管详情成功", managerService.findCustMechManager(anId)).toJson();
    }
    
    @Override
    public String webFindManagerTmp(Long anId) {
        return AjaxObject.newOk("查询公司高管列表成功", managerTmpService.findCustMechManagerTmp(anId)).toJson();
    }
    
    @Override
    public String webSaveManagerTmp(Map<String, Object> anParam, Long anId) {
        final CustMechManagerTmp custMechManagerTmp = (CustMechManagerTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("公司高管-流水信息 修改成功", managerTmpService.saveManagerTmp(custMechManagerTmp, anId)).toJson();
    }

    @Override
    public String webAddChangeManagerTmp(Map<String, Object> anMap) {
        final CustMechManagerTmp custMechManagerTmp = (CustMechManagerTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("公司高管-流水信息 变更添加成功", managerTmpService.addChangeManagerTmp(custMechManagerTmp)).toJson();
    }
    
    @Override
    public String webSaveChangeManagerTmp(Map<String, Object> anParam) {
        final CustMechManagerTmp custMechManagerTmp = (CustMechManagerTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("公司高管-流水信息 变更修改成功", managerTmpService.saveSaveChangeManagerTmp(custMechManagerTmp)).toJson();
    }
    
    @Override
    public String webDelChangeManagerTmp(Long anRefId) {
        return AjaxObject.newOk("公司高管-流水信息 变更删除成功", managerTmpService.saveDelChangeManagerTmp(anRefId)).toJson();
    }
    
    @Override
    public String webCancelChangeManagerTmp(Long anId) {
        return AjaxObject.newOk("公司高管-流水信息 变更删除成功", managerTmpService.saveCancelChangeManagerTmp(anId)).toJson();
    }
    
    @Override
    public String webQueryNewChangeManagerTmp(Long anCustNo) {
        return AjaxObject.newOk("公司高管-流水信息 列表查询成功", managerTmpService.queryNewChangeCustMechManagerTmp(anCustNo)).toJson();
    }
    
    @Override
    public String webQueryChangeManagerTmp(Long anApplyId) {
        return AjaxObject.newOk("公司高管-流水信息 列表查询成功", managerTmpService.queryChangeCustMechManagerTmp(anApplyId)).toJson();
    }
    
    
    @Override
    public String webAddChangeApply(Map<String, Object> anParam, Long anCustNo) {
        return AjaxObject.newOk("公司高管-变更申请 成功", managerTmpService.addChangeApply(anParam, anCustNo)).toJson();
    }
    
    @Override
    public String webSaveChangeApply(Map<String, Object> anParam, Long anApplyId) {
        return AjaxObject.newOk("公司高管-变更申请 成功", managerTmpService.saveChangeApply(anParam, anApplyId)).toJson();
    }
    
    @Override
    public String webQueryChangeApply(Long anCustNo, int anFlag, int anPageNum, int anPageSize) {
        final Page<CustChangeApply> changeApplys = changeService.queryChangeApply(anCustNo, CustomerConstants.ITEM_MANAGER, anFlag, anPageNum,
                anPageSize);
        return AjaxObject.newOkWithPage("高管信息-变更列表查询 成功", changeApplys).toJson();
    }
    
    @Override
    public String webFindChangeApply(Long anId) {
        return null;
    }
    
    @Override
    public String webAddInsteadManagerTmp(Map<String, Object> anMap) {
        final CustMechManagerTmp custMechManagerTmp = (CustMechManagerTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("公司高管-流水信息 代录添加成功", managerTmpService.addInsteadManagerTmp(custMechManagerTmp)).toJson();
    }
    
    @Override
    public String webSaveInsteadManagerTmp(Map<String, Object> anParam, Long anId) {
        final CustMechManagerTmp custMechManagerTmp = (CustMechManagerTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("公司高管-流水信息 代录修改成功", managerTmpService.saveSaveInsteadManagerTmp(custMechManagerTmp, anId)).toJson();

    }
    
    @Override
    public String webDelInsteadManagerTmp(Long anRefId) {
        return AjaxObject.newOk("公司高管-流水信息 代录删除成功", managerTmpService.saveDelInsteadManagerTmp(anRefId)).toJson();
    }
    
    @Override
    public String webCancelInsteadManagerTmp(Long anId) {
        return AjaxObject.newOk("公司高管-流水信息 代录删除成功", managerTmpService.saveCancelInsteadManagerTmp(anId)).toJson();
    }
    
    @Override
    public String webQueryInsteadManagerTmp(Long anInsteadRecordId) {
        return AjaxObject.newOk("公司高管-流水信息 列表查询成功", managerTmpService.queryInsteadManagerTmp(anInsteadRecordId))
                .toJson();
    }

    @Override
    public String webAddInsteadRecord(Map<String, Object> anParam, Long anInsteadRecordId) {
        return AjaxObject.newOk("公司高管-添加代录 成功", managerTmpService.addInsteadRecord(anParam, anInsteadRecordId)).toJson();
    }

    @Override
    public String webFindInsteadRecord(Long anId) {
        return null;
    }

    @Override
    public String webSaveInsteadRecord(Map<String, Object> anParam, Long anInsteadRecordId) {
        return AjaxObject.newOk("公司高管-修改代录 成功", managerTmpService.saveInsteadRecord(anParam, anInsteadRecordId)).toJson();
    }
}
