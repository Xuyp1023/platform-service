package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.customer.ICustMechLawService;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.entity.CustChangeApply;
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

    @Resource
    private CustInsteadService insteadService;

    @Override
    public String webFindLawInfo(Long anCustNo) {
        return AjaxObject.newOk("法人信息查询成功", lawService.findCustMechLawByCustNo(anCustNo)).toJson();
    }

    @Override
    public Object saveLawInfo(Object anCustMechLaw, Long anId) {
        return AjaxObject.newOk("公司法人信息修改成功", lawService.saveCustMechLaw((CustMechLaw) anCustMechLaw, anId)).toJson();
    }

    @Override
    public Object addLawInfo(Object anCustMechLaw, Long anId) {
        return AjaxObject.newOk("公司法人信息添加成功", lawService.addCustMechLaw((CustMechLaw) anCustMechLaw)).toJson();
    }

    @Override
    public String webAddChangeApply(Map<String, Object> anParam, Long anCustNo, Long anId) {
        final CustMechLawTmp custMechLawTmp = (CustMechLawTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("公司法人信息-变更-添加 成功", changeService.addCustChangeApply(custMechLawTmp)).toJson();
    }

    @Override
    public String webFindChangeApply(Long anId) {
        final CustChangeApply changeApply = changeService.findChangeApply(anId, CustomerConstants.ITEM_LAW);
        return AjaxObject.newOk("公司法人信息-变更申请-查询 成功", changeApply).toJson();
    }

    @Override
    public String webConfirmChangeApply(Long anChangeId, Long anCustNo, Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webCancelChangeApply(Long anChangeId, Long anCustNo, Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webQueryChangeApply(Long anCustNo, int anFlag, int anPageNum, int anPageSize) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webAddInsteadRecord(Map<String, Object> anParam, Long anCustNo, Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webConfirmInsteadRecord(Long anInsteadId, Long anCustNo, Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webCancelInsteadRecord(Long anInsteadId, Long anCustNo, Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

}
