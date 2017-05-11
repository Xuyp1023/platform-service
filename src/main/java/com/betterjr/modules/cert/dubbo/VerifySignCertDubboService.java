// Copyright (c) 2014-2017 Bytter. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.0 : 2017年5月10日, liuwl, creation
// ============================================================================
package com.betterjr.modules.cert.dubbo;

import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.cert.dubbo.interfaces.IVerifySignCertService;
import com.betterjr.modules.cert.entity.VerifySignCert;
import com.betterjr.modules.cert.service.VerifySignCertService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

/**
 * @author liuwl
 *
 */
@Service(interfaceClass=IVerifySignCertService.class)
public class VerifySignCertDubboService implements IVerifySignCertService {
    @Resource
    private VerifySignCertService verifySignCertService;

    /* (non-Javadoc)
     * @see com.betterjr.modules.cert.dubbo.interfaces.IVerifySignCertService#webQueryCertList(java.util.Map, int, int, int)
     */
    @Override
    public String webQueryCertList(final Map<String, Object> anParam, final int anFlag, final int anPageNum, final int anPageSize) {
        final Map<String, Object> param = RuleServiceDubboFilterInvoker.getInputObj();

        return AjaxObject.newOkWithPage("证书列表查询成功！", verifySignCertService.queryCertList(param, anFlag, anPageNum, anPageSize)).toJson();
    }

    /* (non-Javadoc)
     * @see com.betterjr.modules.cert.dubbo.interfaces.IVerifySignCertService#webSaveAddCert(java.util.Map)
     */
    @Override
    public String webSaveAddCert(final Map<String, Object> anParam) {
        final VerifySignCert verifySignCert = RuleServiceDubboFilterInvoker.getInputObj();

        return AjaxObject.newOk("证书添加成功！", verifySignCertService.saveAddCert(verifySignCert)).toJson();
    }

    /* (non-Javadoc)
     * @see com.betterjr.modules.cert.dubbo.interfaces.IVerifySignCertService#webSaveEditCert(java.util.Map)
     */
    @Override
    public String webSaveEditCert(final Map<String, Object> anParam, final Long anId) {
        final VerifySignCert verifySignCert = RuleServiceDubboFilterInvoker.getInputObj();

        return AjaxObject.newOk("证书修改成功！", verifySignCertService.saveEditCert(anId, verifySignCert)).toJson();
    }

    /* (non-Javadoc)
     * @see com.betterjr.modules.cert.dubbo.interfaces.IVerifySignCertService#webSaveEnableCert(java.lang.Long)
     */
    @Override
    public String webSaveEnableCert(final Long anId) {
        return AjaxObject.newOk("启用证书成功！", verifySignCertService.saveEnableCert(anId)).toJson();
    }

    /* (non-Javadoc)
     * @see com.betterjr.modules.cert.dubbo.interfaces.IVerifySignCertService#webSaveDisableCert(java.lang.Long)
     */
    @Override
    public String webSaveDisableCert(final Long anId) {
        return AjaxObject.newOk("禁用用证书成功！", verifySignCertService.saveDisableCert(anId)).toJson();
    }

    /* (non-Javadoc)
     * @see com.betterjr.modules.cert.dubbo.interfaces.IVerifySignCertService#webFindCert(java.lang.Long)
     */
    @Override
    public String webFindCert(final Long anId) {
        return AjaxObject.newOk("查询证书详情成功！", verifySignCertService.findCert(anId)).toJson();
    }

    /* (non-Javadoc)
     * @see com.betterjr.modules.cert.dubbo.interfaces.IVerifySignCertService#verifyFile(java.lang.Long, java.lang.Long, java.lang.String)
     */
    @Override
    public boolean verifyFile(final Long anCustNo, final Long anFileId, final String anSignData) {
        return verifySignCertService.verifyFile(anCustNo, anFileId, anSignData);
    }

}
