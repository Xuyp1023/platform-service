package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.customer.ICustMechLawService;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustMechBaseTmp;
import com.betterjr.modules.customer.entity.CustMechLaw;
import com.betterjr.modules.customer.entity.CustMechLawTmp;
import com.betterjr.modules.customer.service.CustChangeService;
import com.betterjr.modules.customer.service.CustInsteadService;
import com.betterjr.modules.customer.service.CustMechLawService;
import com.betterjr.modules.customer.service.CustMechLawTmpService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

/**
 * 法人
 * 
 * @author liuwl
 *
 */
@Service(interfaceClass = ICustMechLawService.class)
public class CustMechLawDubboService implements ICustMechLawService {
    private static Logger logger = LoggerFactory.getLogger(CustMechLawDubboService.class);

    @Resource
    private CustMechLawService lawService;

    @Resource
    private CustMechLawTmpService lawTmpService;

    @Resource
    private CustChangeService changeService;

    @Override
    public String webFindLawInfo(Long anCustNo) {
        final CustMechLaw custMechLaw = lawService.findCustMechLawByCustNo(anCustNo);
        return AjaxObject.newOk("法人信息-详情查询 成功", custMechLaw).toJson();
    }

    @Override
    public String webFindChangeApply(Long anId) {
        final CustChangeApply changeApply = changeService.findChangeApply(anId, CustomerConstants.ITEM_LAW);
        return AjaxObject.newOk("法人信息-变更详情查询 成功", changeApply).toJson();
    }

    @Override
    public String webQueryChangeApply(Long anCustNo, int anFlag, int anPageNum, int anPageSize) {
        final Page<CustChangeApply> changeApplys = changeService.queryChangeApply(anCustNo, CustomerConstants.ITEM_LAW, anFlag, anPageNum,
                anPageSize);
        return AjaxObject.newOkWithPage("法人信息-变更列表查询 成功", changeApplys).toJson();
    }

    @Override
    public String webAddChangeApply(Map<String, Object> anParam, String anFileList) {
        final CustMechLawTmp custMechLawTmp = (CustMechLawTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("法人信息-变更申请 成功", lawTmpService.addChangeApply(custMechLawTmp, anFileList)).toJson();
    }

    @Override
    public String webSaveChangeApply(Map<String, Object> anParam, Long anApplyId, String anFileList) {
        final CustMechLawTmp custMechLawTmp = (CustMechLawTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("法人信息-变更修改 成功", lawTmpService.saveChangeApply(custMechLawTmp, anApplyId, anFileList)).toJson();
    }

    @Override
    public String webAddInsteadRecord(Map<String, Object> anParam, Long anInsteadRecordId, String anFileList) {
        final CustMechLawTmp custMechLawTmp = (CustMechLawTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("法人信息-代录添加 成功", lawTmpService.addInsteadRecord(custMechLawTmp, anInsteadRecordId, anFileList)).toJson();
    }

    @Override
    public String webSaveInsteadRecord(Map<String, Object> anParam, Long anInsteadRecordId, String anFileList) {
        final CustMechLawTmp custMechLawTmp = (CustMechLawTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("法人信息-代录修改 成功", lawTmpService.saveInsteadRecord(custMechLawTmp, anInsteadRecordId, anFileList)).toJson();
    }

    @Override
    public String webFindInsteadRecord(Long anInsteadRecordId) {
        return AjaxObject.newOk("法人信息-代录详情 成功", lawTmpService.findCustMechLawTmpByInsteadRecordId(anInsteadRecordId)).toJson();
    }


}
