package com.betterjr.modules.cert.dubbo;

import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.modules.account.service.CustCertService;
import com.betterjr.modules.cert.ICustomerCertificateService;

@Service(interfaceClass = ICustomerCertificateService.class)
public class CustomerCertificateDubboService implements ICustomerCertificateService {
    @Resource
    private CustCertService custCertService;

    @Override
    public String webFindCustCertificate(Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webQueryCustCertificate(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webAddCustCertificate(Map<String, Object> anParam) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webSaveCustCertificate(Map<String, Object> anParam) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webPublishCustCertificate(Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webCancelCustCertificate(Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

}
