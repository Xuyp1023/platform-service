package com.betterjr.modules.cert.dubbo;

import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.cert.ICertificateService;
import com.betterjr.modules.cert.entity.BetterX509CertInfo;
import com.betterjr.modules.cert.service.BetterX509CertService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

@Service(interfaceClass = ICertificateService.class)
public class CertificateDubboService implements ICertificateService {
    
    @Resource
    private BetterX509CertService betterX509CertService;
   
    @Override
    public String webFindCertificateInfo(Long anId, String anSerialNo) {
        return AjaxObject.newOk("查询证书成功", betterX509CertService.findX509CertInfo(anId, anSerialNo)).toJson();
    }
    
    @Override
    public String webQuerySignerList() {
        return AjaxObject.newOk("查询签发人成功", betterX509CertService.findMiddleX509Cert()).toJson();
    }

    @Override
    public String webQueryCertificateInfo(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        Map<String, Object> param = RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOkWithPage("查询证书列表成功", betterX509CertService.queryX509CertInfo(param, anPageNum, anPageSize, anFlag)).toJson();
    }

    @Override
    public String webAddCertificateInfo(Map<String, Object> anParam) {
        BetterX509CertInfo certInfo = RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("添加证书成功", betterX509CertService.saveX509CertFromWeb(certInfo)).toJson();
    }

    @Override
    public String webSaveCertificateInfo(Map<String, Object> anParam) {
        BetterX509CertInfo certInfo = RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("修改证书成功", betterX509CertService.saveX509CertFromWeb(certInfo)).toJson();
    }

    @Override
    public String webMakeCertificateInfo(Long anId, String anSerialNo) {
        betterX509CertService.saveMakeCertificate(anId, anSerialNo);
        return AjaxObject.newOk("证书制作成功").toJson();
    }
    
    @Override
    public String webCancelCertificateInfo(Long anId, String anSerialNo) {
        betterX509CertService.saveCancelCertificate(anId, anSerialNo);
        return AjaxObject.newOk("证书作废成功").toJson();
    }

    @Override
    public String webRevokeCertificateInfo(Long anId, String anSerialNo, String anReason) {
        betterX509CertService.saveRevokeCert(anId, anSerialNo, anReason);
        return AjaxObject.newOk("证书回收成功").toJson();
    }

    
}
