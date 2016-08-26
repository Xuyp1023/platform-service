package com.betterjr.modules.customer.dubbo;

import static com.betterjr.common.web.AjaxObject.newOk;
import static com.betterjr.common.web.AjaxObject.newOkWithPage;

import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.customer.ICustMechManagerService;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustMechManager;
import com.betterjr.modules.customer.entity.CustMechManagerTmp;
import com.betterjr.modules.customer.helper.ChangeDetailBean;
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
        return newOk("查询公司高管列表成功", managerService.queryCustMechManager(anCustNo)).toJson();
    }

    @Override
    public String webFindManager(Long anId) {
        CustMechManager manager = managerService.findCustMechManager(anId);
        BTAssert.notNull(manager, "没有找到高管信息!");
        return newOk("查询公司高管详情成功!", manager).toJson();
    }

    @Override
    public String webFindManagerTmp(Long anId) {
        return newOk("查询公司高管列表成功", managerTmpService.findManagerTmp(anId)).toJson();
    }

    @Override
    public String webSaveManagerTmp(Map<String, Object> anParam, Long anId, String anFileList) {
        final CustMechManagerTmp custMechManagerTmp = RuleServiceDubboFilterInvoker.getInputObj();
        return newOk("公司高管-流水信息 修改成功", managerTmpService.saveManagerTmp(custMechManagerTmp, anId, anFileList)).toJson();
    }

    @Override
    public String webAddChangeManagerTmp(Map<String, Object> anMap, String anFileList) {
        final CustMechManagerTmp custMechManagerTmp = (CustMechManagerTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return newOk("公司高管-流水信息 变更添加成功", managerTmpService.addChangeManagerTmp(custMechManagerTmp, anFileList)).toJson();
    }

    @Override
    public String webSaveChangeManagerTmp(Map<String, Object> anParam, String anFileList) {
        final CustMechManagerTmp custMechManagerTmp = (CustMechManagerTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return newOk("公司高管-流水信息 变更修改成功", managerTmpService.saveSaveChangeManagerTmp(custMechManagerTmp, anFileList)).toJson();
    }

    @Override
    public String webDeleteChangeManagerTmp(Long anRefId) {
        return newOk("公司高管-流水信息 变更删除成功", managerTmpService.saveDeleteChangeManagerTmp(anRefId)).toJson();
    }

    @Override
    public String webCancelChangeManagerTmp(Long anId) {
        return newOk("公司高管-流水信息 变更删除成功", managerTmpService.saveCancelChangeManagerTmp(anId)).toJson();
    }

    @Override
    public String webQueryNewChangeManagerTmp(Long anCustNo) {
        return newOk("公司高管-流水信息 列表查询成功", managerTmpService.queryNewChangeManagerTmp(anCustNo)).toJson();
    }

    @Override
    public String webQueryChangeManagerTmp(Long anApplyId) {
        return newOk("公司高管-流水信息 列表查询成功", managerTmpService.queryChangeManagerTmp(anApplyId)).toJson();
    }

    @Override
    public String webAddChangeApply(Map<String, Object> anParam, Long anCustNo) {
        return newOk("公司高管-变更申请 成功", managerTmpService.addChangeApply(anParam, anCustNo)).toJson();
    }

    @Override
    public String webSaveChangeApply(Map<String, Object> anParam, Long anApplyId) {
        return newOk("公司高管-变更申请 成功", managerTmpService.saveChangeApply(anParam, anApplyId)).toJson();
    }

    @Override
    public String webQueryChangeApply(Long anCustNo, int anFlag, int anPageNum, int anPageSize) {
        final Page<CustChangeApply> changeApplys = changeService.queryChangeApply(anCustNo, CustomerConstants.ITEM_MANAGER, anFlag, anPageNum,
                anPageSize);
        return newOkWithPage("高管信息-变更列表查询 成功", changeApplys).toJson();
    }

    @Override
    public String webFindChangeApply(Long anApplyId, Long anTmpId) {
        final CustChangeApply changeApply = changeService.findChangeApply(anApplyId, CustomerConstants.ITEM_MANAGER);

        final CustMechManagerTmp nowData = managerTmpService.findManagerTmp(anTmpId);
        final CustMechManagerTmp befData = managerTmpService.findManagerTmpPrevVersion(nowData);

        ChangeDetailBean<CustMechManagerTmp> changeDetailBean = new ChangeDetailBean<>();
        changeDetailBean.setChangeApply(changeApply);
        if (nowData.getTmpOperType() != CustomerConstants.TMP_OPER_TYPE_DELETE) {
            changeDetailBean.setNowData(nowData);
        }
        changeDetailBean.setBefData(befData);

        return newOk("公司高管-变更详情查询 成功", changeDetailBean).toJson();
    }

    @Override
    public String webAddInsteadManagerTmp(Map<String, Object> anMap, Long anInsteadRecordId, String anFileList) {
        final CustMechManagerTmp custMechManagerTmp = (CustMechManagerTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return newOk("公司高管-流水信息 代录添加成功", managerTmpService.addInsteadManagerTmp(custMechManagerTmp, anInsteadRecordId, anFileList)).toJson();
    }

    @Override
    public String webSaveInsteadManagerTmp(Map<String, Object> anParam, Long anInsteadRecordId, String anFileList) {
        final CustMechManagerTmp custMechManagerTmp = (CustMechManagerTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return newOk("公司高管-流水信息 代录修改成功", managerTmpService.saveSaveInsteadManagerTmp(custMechManagerTmp, anInsteadRecordId, anFileList)).toJson();
    }

    @Override
    public String webDeleteInsteadManagerTmp(Long anRefId, Long anInsteadRecordId) {
        return newOk("公司高管-流水信息 代录删除成功", managerTmpService.saveDeleteInsteadManagerTmp(anRefId, anInsteadRecordId)).toJson();
    }

    @Override
    public String webCancelInsteadManagerTmp(Long anId, Long anInsteadRecordId) {
        return newOk("公司高管-流水信息 代录删除成功", managerTmpService.saveCancelInsteadManagerTmp(anId, anInsteadRecordId)).toJson();
    }

    @Override
    public String webQueryInsteadManagerTmp(Long anInsteadRecordId) {
        return newOk("公司高管-流水信息 列表查询成功", managerTmpService.queryInsteadManagerTmp(anInsteadRecordId)).toJson();
    }

    @Override
    public String webAddInsteadRecord(Map<String, Object> anParam, Long anInsteadRecordId) {
        return newOk("公司高管-添加代录 成功", managerTmpService.addInsteadRecord(anParam, anInsteadRecordId)).toJson();
    }

    @Override
    public String webFindInsteadRecord(Long anId) {
        return null;
    }

    @Override
    public String webSaveInsteadRecord(Map<String, Object> anParam, Long anInsteadRecordId) {
        return newOk("公司高管-修改代录 成功", managerTmpService.saveInsteadRecord(anParam, anInsteadRecordId)).toJson();
    }
}
