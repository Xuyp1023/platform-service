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

    @Resource
    private CustInsteadService insteadService;

    @Override
    public String webFindBaseInfo(final Long anCustNo) {
        final CustMechBase custMechBase = baseService.findCustMechBase(anCustNo);
        return AjaxObject.newOk("查询公司基本信息成功", custMechBase).toJson();
    }

    @Override
    public CustMechBase addBaseInfo(CustMechBase anCustMechBase, Long anCustNo) {
        return baseService.addCustMechBase(anCustMechBase, anCustNo);
    }

    @Override
    public CustMechBase saveBaseInfo(CustMechBase anCustMechBase, Long anCustNo) {
        return baseService.saveCustMechBase(anCustMechBase, anCustNo);
    }

    @Override
    public String webFindChangeApply(Long anId) {
        final CustChangeApply changeApply = changeService.findChangeApply(anId, CustomerConstants.ITEM_BASE);
        return AjaxObject.newOk("公司基本信息-变更申请-查询 成功", changeApply).toJson();
    }

    @Override
    public String webQueryChangeApply(Long anCustNo, int anFlag, int anPageNum, int anPageSize) {
        final Page<CustChangeApply> changeApplys = changeService.queryChangeApplyList(anCustNo, CustomerConstants.ITEM_BASE, anFlag, anPageNum,
                anPageSize);
        return AjaxObject.newOkWithPage("公司基本信息-变更申请-查询列表 成功", changeApplys).toJson();
    }

    @Override
    public String webAddChangeApply(Map<String, Object> anParam, Long anCustNo) {
        final CustMechBaseTmp custMechBaseTmp = (CustMechBaseTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("公司基本信息-变更-添加 成功", changeService.addCustChangeApply(custMechBaseTmp)).toJson();
    }

    @Override
    public String webFindInsteadRecord(Long anId) {
        return AjaxObject.newOk("公司基本信息-代录-查询 成功", baseTmpService.findCustMechBaseTmp(anId)).toJson();
    }

    @Override
    public String webAddInsteadRecord(Map<String, Object> anParam, Long anInsteadRecordId) {
        final CustMechBaseTmp custMechBaseTmp = (CustMechBaseTmp) RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("公司基本信息-代录-添加 成功", baseTmpService.addCustMechBaseTmpByInstead(custMechBaseTmp, anInsteadRecordId, CustomerConstants.ITEM_BASE)).toJson();
    }

}
