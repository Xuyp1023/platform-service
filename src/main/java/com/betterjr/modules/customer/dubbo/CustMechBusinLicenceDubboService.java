package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.modules.customer.ICustMechBusinLicenceService;
import com.betterjr.modules.customer.service.CustChangeService;
import com.betterjr.modules.customer.service.CustInsteadService;
import com.betterjr.modules.customer.service.CustMechBusinLicenceService;

/**
 * 营业执照
 * 
 * @author liuwl
 *
 */
@Service(interfaceClass = ICustMechBusinLicenceService.class)
public class CustMechBusinLicenceDubboService implements ICustMechBusinLicenceService {

    @Resource
    private CustChangeService changeService;

    @Resource
    private CustInsteadService insteadService;

    @Resource
    private CustMechBusinLicenceService businLicenceService;

    @Override
    public String webFindBusinLicence(String anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String addBusinLicence(Map<String, Object> anParam, Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String saveBusinLicence(Map<String, Object> anParam, Long anCustNo) {
        return null;
    }

    @Override
    public String webAddChangeApply(Map<String, Object> anParam, Long anCustNo, Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webFindChangeApply(Long anCustNo, Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webConfirmChangeApply(Long anChangeId, Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webCancelChangeApply(Long anChangeId, Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webAddInsteadRecord(Map<String, Object> anParam, Long anCustNo, Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webSaveInsteadRecord(Map<String, Object> anParam, Long anCustNo, Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webFindInsteadRecord(Map<String, Object> anParam, Long anCustNo, Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webConfirmInsteadRecord(Long anInsteadId, Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webCancelInsteadRecord(Long anInsteadId, Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

}
