package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.customer.ICustMechBaseService;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustMechBase;
import com.betterjr.modules.customer.entity.CustMechBaseTmp;
import com.betterjr.modules.customer.service.CustChangeService;
import com.betterjr.modules.customer.service.CustInsteadService;
import com.betterjr.modules.customer.service.CustMechBaseService;
import com.betterjr.modules.customer.service.CustMechBaseTmpService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

/**
 * 公司基本信息
 * 
 * @author liuwl
 *
 */
@Service(interfaceClass = ICustMechBaseService.class)
public class CustMechBaseDubboService implements ICustMechBaseService {
    private static Logger logger = LoggerFactory.getLogger(CustMechBaseDubboService.class);

    @Resource
    private CustMechBaseService baseService;

    @Resource
    private CustMechBaseTmpService baseTmpService;

    @Resource
    private CustChangeService changeService;

    @Override
    public String webFindBaseInfo(final Long anCustNo) {
        final CustMechBase custMechBase = baseService.findCustMechBaseByCustNo(anCustNo);
        return AjaxObject.newOk("公司基本信息-详情查询 成功", custMechBase).toJson();
    }

    @Override
    public String webFindChangeApply(Long anId) {
        final CustChangeApply changeApply = changeService.findChangeApply(anId, CustomerConstants.ITEM_BASE);
        return AjaxObject.newOk("公司基本信息-变更详情查询 成功", changeApply).toJson();
    }

    @Override
    public String webQueryChangeApply(Long anCustNo, int anFlag, int anPageNum, int anPageSize) {
        final Page<CustChangeApply> changeApplys = changeService.queryChangeApply(anCustNo, CustomerConstants.ITEM_BASE, anFlag, anPageNum,
                anPageSize);
        return AjaxObject.newOkWithPage("公司基本信息-变更列表 成功", changeApplys).toJson();
    }

    @Override
    public String webAddChangeApply(Map<String, Object> anParam, String anFileList) {
        final CustMechBaseTmp custMechBaseTmp = (CustMechBaseTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("公司基本信息-变更申请 成功", baseTmpService.addChangeApply(custMechBaseTmp, anFileList)).toJson();
    }

    @Override
    public String webSaveChangeApply(Map<String, Object> anParam, Long anApplyId, String anFileList) {
        final CustMechBaseTmp custMechBaseTmp = (CustMechBaseTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("公司基本信息-变更修改 成功", baseTmpService.saveChangeApply(custMechBaseTmp, anApplyId, anFileList)).toJson();
    }

    @Override
    public String webFindInsteadRecord(Long anInsteadRecordId) {
        return AjaxObject.newOk("公司基本信息-代录详情 成功", baseTmpService.findCustMechBaseTmp(anInsteadRecordId)).toJson();
    }

    @Override
    public String webAddInsteadRecord(Map<String, Object> anParam, Long anInsteadRecordId, String anFileList) {
        final CustMechBaseTmp custMechBaseTmp = (CustMechBaseTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("公司基本信息-添加代录 成功", baseTmpService.addInsteadRecord(custMechBaseTmp, anInsteadRecordId, anFileList)).toJson();
    }

    @Override
    public String webSaveInsteadRecord(Map<String, Object> anParam, Long anInsteadRecordId, String anFileList) {
        final CustMechBaseTmp custMechBaseTmp = (CustMechBaseTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("公司基本信息-代录修改 成功", baseTmpService.saveInsteadRecord(custMechBaseTmp, anInsteadRecordId, anFileList)).toJson();
    }
}
