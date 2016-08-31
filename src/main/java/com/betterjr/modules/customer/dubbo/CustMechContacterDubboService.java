package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;

import static com.betterjr.common.web.AjaxObject.*;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.customer.ICustMechContacterService;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustMechContacter;
import com.betterjr.modules.customer.entity.CustMechContacterTmp;
import com.betterjr.modules.customer.helper.ChangeDetailBean;
import com.betterjr.modules.customer.service.CustChangeService;
import com.betterjr.modules.customer.service.CustInsteadService;
import com.betterjr.modules.customer.service.CustMechContacterService;
import com.betterjr.modules.customer.service.CustMechContacterTmpService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

/**
 * 联系人
 * 
 * @author liuwl
 *
 */
@Service(interfaceClass = ICustMechContacterService.class)
public class CustMechContacterDubboService implements ICustMechContacterService {

    @Resource
    private CustMechContacterService contacterService;

    @Resource
    private CustMechContacterTmpService contacterTmpService;

    @Resource
    private CustChangeService changeService;

    @Resource
    private CustInsteadService insteadService;

    @Override
    public String webQueryContacter(Long anCustNo) {
        return newOk("查询公司联系人列表成功", contacterService.queryCustMechContacter(anCustNo)).toJson();
    }

    @Override
    public String webFindContacter(Long anId) {
        CustMechContacter contacter = contacterService.findContacter(anId);
        BTAssert.notNull(contacter, "没有找到联系人信息!");
        return newOk("查询公司联系人详情成功!", contacter).toJson();
    }

    @Override
    public String webFindContacterTmp(Long anId) {
        return newOk("查询公司联系人列表成功", contacterTmpService.findContacterTmp(anId)).toJson();
    }

    @Override
    public String webSaveContacterTmp(Map<String, Object> anParam, Long anId, String anFileList) {
        final CustMechContacterTmp custMechContacterTmp = (CustMechContacterTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return newOk("公司联系人-流水信息 修改成功", contacterTmpService.saveContacterTmp(custMechContacterTmp, anId, anFileList)).toJson();
    }

    @Override
    public String webAddChangeContacterTmp(Map<String, Object> anMap, String anFileList) {
        final CustMechContacterTmp custMechContacterTmp = (CustMechContacterTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return newOk("公司联系人-流水信息 变更添加成功", contacterTmpService.addChangeContacterTmp(custMechContacterTmp, anFileList)).toJson();
    }

    @Override
    public String webSaveChangeContacterTmp(Map<String, Object> anParam, String anFileList) {
        final CustMechContacterTmp custMechContacterTmp = (CustMechContacterTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return newOk("公司联系人-流水信息 变更修改成功", contacterTmpService.saveSaveChangeContacterTmp(custMechContacterTmp, anFileList)).toJson();
    }

    @Override
    public String webDeleteChangeContacterTmp(Long anRefId) {
        return newOk("公司联系人-流水信息 变更删除成功", contacterTmpService.saveDeleteChangeContacterTmp(anRefId)).toJson();
    }

    @Override
    public String webCancelChangeContacterTmp(Long anId) {
        return newOk("公司联系人-流水信息 变更删除成功", contacterTmpService.saveCancelChangeContacterTmp(anId)).toJson();
    }

    @Override
    public String webQueryNewChangeContacterTmp(Long anCustNo) {
        return newOk("公司联系人-流水信息 列表查询成功", contacterTmpService.queryNewChangeCustMechContacterTmp(anCustNo)).toJson();
    }

    @Override
    public String webQueryChangeContacterTmp(Long anApplyId) {
        return newOk("公司联系人-流水信息 列表查询成功", contacterTmpService.queryChangeCustMechContacterTmp(anApplyId)).toJson();
    }

    @Override
    public String webAddChangeApply(Map<String, Object> anParam, Long anCustNo) {
        return newOk("公司联系人-变更申请 成功", contacterTmpService.addChangeApply(anParam, anCustNo)).toJson();
    }

    @Override
    public String webSaveChangeApply(Map<String, Object> anParam, Long anApplyId) {
        return newOk("公司联系人-变更申请 成功", contacterTmpService.saveChangeApply(anParam, anApplyId)).toJson();
    }

    @Override
    public String webQueryChangeApply(Long anCustNo, int anFlag, int anPageNum, int anPageSize) {
        final Page<CustChangeApply> changeApplys = changeService.queryChangeApply(anCustNo, CustomerConstants.ITEM_CONTACTER, anFlag, anPageNum,
                anPageSize);
        return newOkWithPage("联系人信息-变更列表查询 成功", changeApplys).toJson();
    }

    @Override
    public String webFindChangeApply(Long anApplyId, Long anTmpId) {
        final CustChangeApply changeApply = changeService.findChangeApply(anApplyId, CustomerConstants.ITEM_CONTACTER);

        final CustMechContacterTmp nowData = contacterTmpService.findContacterTmp(anTmpId);
        final CustMechContacterTmp befData = contacterTmpService.findContacterTmpPrevVersion(nowData);

        ChangeDetailBean<CustMechContacterTmp> changeDetailBean = new ChangeDetailBean<>();
        changeDetailBean.setChangeApply(changeApply);
        if (BetterStringUtils.equals(nowData.getTmpOperType(), CustomerConstants.TMP_OPER_TYPE_DELETE) == false) {
            changeDetailBean.setNowData(nowData);
        }
        changeDetailBean.setBefData(befData);

        return newOk("公司联系人-变更详情查询 成功", changeDetailBean).toJson();
    }

    @Override
    public String webAddInsteadContacterTmp(Map<String, Object> anMap, Long anInsteadRecordId, String anFileList) {
        final CustMechContacterTmp custMechContacterTmp = (CustMechContacterTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return newOk("公司联系人-流水信息 代录添加成功", contacterTmpService.addInsteadContacterTmp(custMechContacterTmp, anInsteadRecordId, anFileList)).toJson();
    }

    @Override
    public String webSaveInsteadContacterTmp(Map<String, Object> anParam, Long anInsteadRecordId, String anFileList) {
        final CustMechContacterTmp custMechContacterTmp = (CustMechContacterTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return newOk("公司联系人-流水信息 代录修改成功", contacterTmpService.saveSaveInsteadContacterTmp(custMechContacterTmp, anInsteadRecordId, anFileList)).toJson();

    }

    @Override
    public String webDeleteInsteadContacterTmp(Long anRefId, Long anInsteadRecordId) {
        return newOk("公司联系人-流水信息 代录删除成功", contacterTmpService.saveDeleteInsteadContacterTmp(anRefId, anInsteadRecordId)).toJson();
    }

    @Override
    public String webCancelInsteadContacterTmp(Long anId, Long anInsteadRecordId) {
        return newOk("公司联系人-流水信息 代录删除成功", contacterTmpService.saveCancelInsteadContacterTmp(anId, anInsteadRecordId)).toJson();
    }

    @Override
    public String webQueryInsteadContacterTmp(Long anInsteadRecordId) {
        return newOk("公司联系人-流水信息 列表查询成功", contacterTmpService.queryInsteadContacterTmp(anInsteadRecordId)).toJson();
    }

    @Override
    public String webAddInsteadRecord(Map<String, Object> anParam, Long anInsteadRecordId) {
        return newOk("公司联系人-添加代录 成功", contacterTmpService.addInsteadRecord(anParam, anInsteadRecordId)).toJson();
    }

    @Override
    public String webFindInsteadRecord(Long anId) {
        return null;
    }

    @Override
    public String webSaveInsteadRecord(Map<String, Object> anParam, Long anInsteadRecordId) {
        return newOk("公司联系人-修改代录 成功", contacterTmpService.saveInsteadRecord(anParam, anInsteadRecordId)).toJson();
    }

}
