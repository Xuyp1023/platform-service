package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.modules.customer.ICustMechShareholderService;
import com.betterjr.modules.customer.service.CustChangeService;
import com.betterjr.modules.customer.service.CustInsteadService;
import com.betterjr.modules.customer.service.CustMechShareholderService;

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
    private CustChangeService changeService;

    @Resource
    private CustInsteadService insteadService;

    @Override
    public String webQueryCustShareholder(Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webFindCustShareholder(Long anId, Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webAddChangeApply(Map<String, Object> anParam, Long anCustNo, Long anId, Long anOperType) {
        return null;
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
