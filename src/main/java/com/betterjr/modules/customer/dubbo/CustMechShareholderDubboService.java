package com.betterjr.modules.customer.dubbo;

import static com.betterjr.common.web.AjaxObject.newOk;
import static com.betterjr.common.web.AjaxObject.newOkWithPage;

import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.customer.ICustMechShareholderService;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustMechShareholder;
import com.betterjr.modules.customer.entity.CustMechShareholderTmp;
import com.betterjr.modules.customer.helper.ChangeDetailBean;
import com.betterjr.modules.customer.service.CustChangeService;
import com.betterjr.modules.customer.service.CustInsteadService;
import com.betterjr.modules.customer.service.CustMechShareholderService;
import com.betterjr.modules.customer.service.CustMechShareholderTmpService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

/**
 * 股东
 * 
 * @author liuwl
 *
 */
@Service(interfaceClass = ICustMechShareholderService.class)
public class CustMechShareholderDubboService implements ICustMechShareholderService {

    @Resource
    private CustMechShareholderService shareholderService;

    @Resource
    private CustMechShareholderTmpService shareholderTmpService;

    @Resource
    private CustChangeService changeService;

    @Resource
    private CustInsteadService insteadService;

    @Override
    public String webQueryShareholder(Long anCustNo) {
        return newOk("查询公司股东列表成功", shareholderService.queryShareholder(anCustNo)).toJson();
    }

    @Override
    public String webFindShareholder(Long anId) {
        CustMechShareholder Shareholder = shareholderService.findShareholder(anId);
        BTAssert.notNull(Shareholder, "没有找到股东信息!");
        return newOk("查询公司股东详情成功!", Shareholder).toJson();
    }

    @Override
    public String webFindShareholderTmp(Long anId) {
        return newOk("查询公司股东列表成功", shareholderTmpService.findShareholderTmp(anId)).toJson();
    }

    @Override
    public String webSaveShareholderTmp(Map<String, Object> anParam, Long anId, String anFileList) {
        final CustMechShareholderTmp custMechShareholderTmp = RuleServiceDubboFilterInvoker.getInputObj();
        return newOk("公司股东-流水信息 修改成功", shareholderTmpService.saveShareholderTmp(custMechShareholderTmp, anId, anFileList)).toJson();
    }

    @Override
    public String webAddChangeShareholderTmp(Map<String, Object> anMap, String anFileList) {
        final CustMechShareholderTmp custMechShareholderTmp = RuleServiceDubboFilterInvoker.getInputObj();
        return newOk("公司股东-流水信息 变更添加成功", shareholderTmpService.addChangeShareholderTmp(custMechShareholderTmp, anFileList)).toJson();
    }

    @Override
    public String webSaveChangeShareholderTmp(Map<String, Object> anParam, String anFileList) {
        final CustMechShareholderTmp custMechShareholderTmp = RuleServiceDubboFilterInvoker.getInputObj();
        return newOk("公司股东-流水信息 变更修改成功", shareholderTmpService.saveSaveChangeShareholderTmp(custMechShareholderTmp, anFileList)).toJson();
    }

    @Override
    public String webDeleteChangeShareholderTmp(Long anRefId) {
        return newOk("公司股东-流水信息 变更删除成功", shareholderTmpService.saveDeleteChangeShareholderTmp(anRefId)).toJson();
    }

    @Override
    public String webCancelChangeShareholderTmp(Long anId) {
        return newOk("公司股东-流水信息 变更删除成功", shareholderTmpService.saveCancelChangeShareholderTmp(anId)).toJson();
    }

    @Override
    public String webQueryNewChangeShareholderTmp(Long anCustNo) {
        return newOk("公司股东-流水信息 列表查询成功", shareholderTmpService.queryNewChangeShareholderTmp(anCustNo)).toJson();
    }

    @Override
    public String webQueryChangeShareholderTmp(Long anApplyId) {
        return newOk("公司股东-流水信息 列表查询成功", shareholderTmpService.queryChangeShareholderTmp(anApplyId)).toJson();
    }

    @Override
    public String webAddChangeApply(Map<String, Object> anParam, Long anCustNo) {
        return newOk("公司股东-变更申请 成功", shareholderTmpService.addChangeApply(anParam, anCustNo)).toJson();
    }

    @Override
    public String webSaveChangeApply(Map<String, Object> anParam, Long anApplyId) {
        return newOk("公司股东-变更申请 成功", shareholderTmpService.saveChangeApply(anParam, anApplyId)).toJson();
    }

    @Override
    public String webQueryChangeApply(Long anCustNo, int anFlag, int anPageNum, int anPageSize) {
        final Page<CustChangeApply> changeApplys = changeService.queryChangeApply(anCustNo, CustomerConstants.ITEM_SHAREHOLDER, anFlag, anPageNum,
                anPageSize);
        return newOkWithPage("股东信息-变更列表查询 成功", changeApplys).toJson();
    }

    @Override
    public String webFindChangeApply(Long anApplyId, Long anTmpId) {
        final CustChangeApply changeApply = changeService.findChangeApply(anApplyId, CustomerConstants.ITEM_SHAREHOLDER);

        final CustMechShareholderTmp nowData = shareholderTmpService.findShareholderTmp(anTmpId);
        final CustMechShareholderTmp befData = shareholderTmpService.findShareholderTmpPrevVersion(nowData);

        ChangeDetailBean<CustMechShareholderTmp> changeDetailBean = new ChangeDetailBean<>();
        changeDetailBean.setChangeApply(changeApply);
        if (BetterStringUtils.equals(nowData.getTmpOperType(), CustomerConstants.TMP_OPER_TYPE_DELETE) == false) {
            changeDetailBean.setNowData(nowData);
        }
        changeDetailBean.setBefData(befData);

        return newOk("公司股东-变更详情查询 成功", changeDetailBean).toJson();
    }

    @Override
    public String webAddInsteadShareholderTmp(Map<String, Object> anMap, Long anInsteadRecordId, String anFileList) {
        final CustMechShareholderTmp custMechShareholderTmp = RuleServiceDubboFilterInvoker.getInputObj();
        return newOk("公司股东-流水信息 代录添加成功", shareholderTmpService.addInsteadShareholderTmp(custMechShareholderTmp, anInsteadRecordId, anFileList)).toJson();
    }

    @Override
    public String webSaveInsteadShareholderTmp(Map<String, Object> anParam, Long anInsteadRecordId, String anFileList) {
        final CustMechShareholderTmp custMechShareholderTmp = RuleServiceDubboFilterInvoker.getInputObj();
        return newOk("公司股东-流水信息 代录修改成功", shareholderTmpService.saveSaveInsteadShareholderTmp(custMechShareholderTmp, anInsteadRecordId, anFileList)).toJson();

    }

    @Override
    public String webDeleteInsteadShareholderTmp(Long anRefId, Long anInsteadRecordId) {
        return newOk("公司股东-流水信息 代录删除成功", shareholderTmpService.saveDeleteInsteadShareholderTmp(anRefId, anInsteadRecordId)).toJson();
    }

    @Override
    public String webCancelInsteadShareholderTmp(Long anId, Long anInsteadRecordId) {
        return newOk("公司股东-流水信息 代录删除成功", shareholderTmpService.saveCancelInsteadShareholderTmp(anId, anInsteadRecordId)).toJson();
    }

    @Override
    public String webQueryInsteadShareholderTmp(Long anInsteadRecordId) {
        return newOk("公司股东-流水信息 列表查询成功", shareholderTmpService.queryInsteadShareholderTmp(anInsteadRecordId)).toJson();
    }

    @Override
    public String webAddInsteadRecord(Map<String, Object> anParam, Long anInsteadRecordId) {
        return newOk("公司股东-添加代录 成功", shareholderTmpService.addInsteadRecord(anParam, anInsteadRecordId)).toJson();
    }

    @Override
    public String webFindInsteadRecord(Long anId) {
        return null;
    }

    @Override
    public String webSaveInsteadRecord(Map<String, Object> anParam, Long anInsteadRecordId) {
        return newOk("公司股东-修改代录 成功", shareholderTmpService.saveInsteadRecord(anParam, anInsteadRecordId)).toJson();
    }
}
