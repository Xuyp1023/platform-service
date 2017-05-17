// Copyright (c) 2014-2017 Bytter. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.0 : 2017年5月10日, liuwl, creation
// ============================================================================
package com.betterjr.modules.cert.service;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterException;
import com.betterjr.common.security.KeyReader;
import com.betterjr.common.security.SignHelper;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.QueryTermBuilder;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.cert.dao.VerifySignCertMapper;
import com.betterjr.modules.cert.entity.VerifySignCert;
import com.betterjr.modules.document.service.DataStoreService;

/**
 * @author liuwl
 *
 */
@Service
public class VerifySignCertService extends BaseService<VerifySignCertMapper, VerifySignCert> {
    @Resource
    private CustAccountService custAccountService;

    @Resource
    private DataStoreService dataStoreService;

    /**
     *
     * @param anParam
     * @param anFlag
     * @param anPageNum
     * @param anPageSize
     * @return
     */
    public Page<VerifySignCert> queryCertList(final Map<String, Object> anParam, final int anFlag, final int anPageNum, final int anPageSize) {
        BTAssert.isTrue(UserUtils.platformUser(), "操作失败！");
        final Map<String, Object> param = Collections3.filterMapEmptyObject(anParam);

        final String custName = (String) param.get("LIKEcustName");
        if (BetterStringUtils.isNotBlank(custName)) {
            param.put("LIKEcustName", "%" + custName + "%");
        }

        return this.selectPropertyByPage(param, anPageNum, anPageSize, anFlag == 1);
    }

    /**
     *
     * @param anVerifySignCert
     * @return
     */
    public VerifySignCert saveAddCert(final VerifySignCert anVerifySignCert) {
        BTAssert.isTrue(UserUtils.platformUser(), "操作失败！");
        final Long custNo = anVerifySignCert.getCustNo();
        final String name = anVerifySignCert.getName();
        final String serialNo = anVerifySignCert.getSerialNo();
        final String certInfo = anVerifySignCert.getCertInfo();

        BTAssert.notNull(custNo, "公司不允许为空！");
        BTAssert.isTrue(BetterStringUtils.isNotBlank(name), "证书名称不允许为空！");
        BTAssert.isTrue(BetterStringUtils.isNotBlank(serialNo), "证书序列号不允许为空！");
        BTAssert.isTrue(BetterStringUtils.isNotBlank(certInfo), "证书内容不允许为空！");

        final CustInfo custInfo = custAccountService.findCustInfo(custNo);
        BTAssert.notNull(custInfo, "没有找到公司信息！");

        final Map<String, Object> conditionMap = QueryTermBuilder.newInstance().put("custNo", anVerifySignCert.getCustNo()).build();
        final VerifySignCert verifySignCert = Collections3.getFirst(this.selectByProperty(conditionMap));

        BTAssert.isNull(verifySignCert, "该公司验签证书已经存在！");

        final CustOperatorInfo operator = UserUtils.getOperatorInfo();

        anVerifySignCert.init(operator);
		anVerifySignCert.setBusinStatus("0");

        anVerifySignCert.setCustName(custInfo.getCustName());
        anVerifySignCert.setOperOrg(custInfo.getOperOrg());

        final int result = this.insert(anVerifySignCert);
        BTAssert.isTrue(result == 1, "证书保存失败！");

        return anVerifySignCert;
    }

    /**
     *
     * @param anVerifySignCert
     * @return
     */
    public VerifySignCert saveEditCert(final Long anId, final VerifySignCert anVerifySignCert) {
        BTAssert.isTrue(UserUtils.platformUser(), "操作失败！");
        BTAssert.notNull(anId, "编号不允许为空！");
        final VerifySignCert verifySignCert = this.selectByPrimaryKey(anId);

        BTAssert.notNull(verifySignCert, "没有找到验签证书！");

        final String name = anVerifySignCert.getName();
        final String serialNo = anVerifySignCert.getSerialNo();
        final String certInfo = anVerifySignCert.getCertInfo();

        BTAssert.isTrue(BetterStringUtils.isNotBlank(name), "证书名称不允许为空！");
        BTAssert.isTrue(BetterStringUtils.isNotBlank(serialNo), "证书序列号不允许为空！");
        BTAssert.isTrue(BetterStringUtils.isNotBlank(certInfo), "证书内容不允许为空！");

        verifySignCert.setName(name);
        verifySignCert.setSerialNo(serialNo);
        verifySignCert.setCertInfo(certInfo);
        final CustOperatorInfo operator = UserUtils.getOperatorInfo();
        verifySignCert.modify(operator);

        final int result = this.updateByPrimaryKeySelective(verifySignCert);
        BTAssert.isTrue(result == 1, "证书保存失败！");

        return verifySignCert;
    }

    /**
     *
     * @param anId
     * @return
     */
    public VerifySignCert saveEnableCert(final Long anId) {
        BTAssert.isTrue(UserUtils.platformUser(), "操作失败！");
        BTAssert.notNull(anId, "编号不允许为空！");
        final VerifySignCert verifySignCert = this.selectByPrimaryKey(anId);

        BTAssert.notNull(verifySignCert, "没有找到验签证书！");
        final String businStatus = verifySignCert.getBusinStatus();
        BTAssert.isTrue(BetterStringUtils.equals("0", businStatus), "状态不正确！");
        verifySignCert.setBusinStatus("1");

        final CustOperatorInfo operator = UserUtils.getOperatorInfo();
        verifySignCert.modify(operator);

        final int result = this.updateByPrimaryKeySelective(verifySignCert);
        BTAssert.isTrue(result == 1, "证书保存失败！");

        return verifySignCert;
    }

    /**
     *
     * @param anId
     * @return
     */
    public VerifySignCert saveDisableCert(final Long anId) {
        BTAssert.isTrue(UserUtils.platformUser(), "操作失败！");
        BTAssert.notNull(anId, "编号不允许为空！");
        final VerifySignCert verifySignCert = this.selectByPrimaryKey(anId);

        BTAssert.notNull(verifySignCert, "没有找到验签证书！");

        final String businStatus = verifySignCert.getBusinStatus();
        BTAssert.isTrue(BetterStringUtils.equals("1", businStatus), "状态不正确！");
        verifySignCert.setBusinStatus("0");

        final CustOperatorInfo operator = UserUtils.getOperatorInfo();
        verifySignCert.modify(operator);

        final int result = this.updateByPrimaryKeySelective(verifySignCert);
        BTAssert.isTrue(result == 1, "证书保存失败！");

        return verifySignCert;
    }

    /**
     *
     * @param anId
     * @return
     */
    public VerifySignCert findCert(final Long anId) {
        BTAssert.isTrue(UserUtils.platformUser(), "操作失败！");
        BTAssert.notNull(anId, "编号不允许为空！");
        final VerifySignCert verifySignCert = this.selectByPrimaryKey(anId);

        BTAssert.notNull(verifySignCert, "没有找到验签证书！");

        return verifySignCert;
    }

    /**
     *
     * @param anFileId
     * @param anSignData
     * @return
     */
    public boolean verifyFile(final Long anCustNo, final Long anFileId, final String anSignData) {
        final Map<String, Object> conditionMap = QueryTermBuilder.newInstance().put("custNo", anCustNo).build();
        final VerifySignCert verifySignCert = Collections3.getFirst(this.selectByProperty(conditionMap));

        BTAssert.notNull(verifySignCert, "没有找到该证书！");

        BTAssert.isTrue(BetterStringUtils.equals("1", verifySignCert.getBusinStatus()), "证书状态不正确！");

        try (final InputStream inputStream = dataStoreService.loadFromStore(anFileId)) {
            BTAssert.notNull(inputStream, "没有找到文件内容！");

            final Certificate certificate = KeyReader.fromCerBase64String(verifySignCert.getCertInfo());

            final boolean result = SignHelper.verifySignStream(inputStream, anSignData, certificate.getPublicKey());

            return result;
        }
        catch (final IOException e) {
            throw new BytterException(e);
        }
    }
}
