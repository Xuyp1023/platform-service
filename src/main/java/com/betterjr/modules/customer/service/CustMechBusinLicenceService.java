package com.betterjr.modules.customer.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.dao.CustMechBusinLicenceMapper;
import com.betterjr.modules.customer.entity.CustMechBusinLicence;
import com.betterjr.modules.customer.entity.CustMechBusinLicenceTmp;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechBusinLicenceService extends BaseService<CustMechBusinLicenceMapper, CustMechBusinLicence> {

    private static Logger logger = LoggerFactory.getLogger(CustMechBusinLicenceService.class);

    /**
     * 营业执照信息-查询详情
     * 
     * @param anCustNo
     * @return
     */
    public CustMechBusinLicence findCustMechBusinLicenceByCustNo(Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");

        final List<CustMechBusinLicence> businLicences = this.selectByProperty(CustomerConstants.CUST_NO, anCustNo);
        BTAssert.notEmpty(businLicences, "没有找到营业执照信息!");

        return Collections3.getFirst(businLicences);
    }

    /**
     * 营业执照信息-查询详情
     * 
     * @param anCustNo
     * @return
     */
    public CustMechBusinLicence findCustMechBusinLicence(Long anId) {
        BTAssert.notNull(anId, "客户编号不允许为空！");

        final CustMechBusinLicence businLicence = this.selectByPrimaryKey(anId);
        BTAssert.notNull(businLicence, "没有找到营业执照信息!");

        return businLicence;
    }

    /**
     * 营业执照信息-修改 代录/变更
     * 
     * @param anCustMechBusinLicence
     * @return
     */
    public CustMechBusinLicence saveCustMechBusinLicence(CustMechBusinLicenceTmp anBusinLicenceTmp) {
        BTAssert.notNull(anBusinLicenceTmp, "营业执照流水信息不允许为空！");

        String tmpType = anBusinLicenceTmp.getTmpType();
        CustMechBusinLicence tempCustMechBusinLicence = null;
        if (BetterStringUtils.equals(CustomerConstants.TMP_TYPE_INSTEAD, tmpType) == true) {
            Long custNo = anBusinLicenceTmp.getRefId();
            tempCustMechBusinLicence = this.findCustMechBusinLicenceByCustNo(custNo);
        }
        else if (BetterStringUtils.equals(CustomerConstants.TMP_TYPE_CHANGE, tmpType) == true) {
            Long id = anBusinLicenceTmp.getRefId();
            tempCustMechBusinLicence = this.findCustMechBusinLicence(id);
        }
        else {
            throw new BytterTradeException(20100, "营业执照流水类型不正确!");
        }

        BTAssert.notNull(tempCustMechBusinLicence, "没有找到营业执照信息!");

        tempCustMechBusinLicence.initModifyValue(anBusinLicenceTmp);
        this.updateByPrimaryKeySelective(tempCustMechBusinLicence);

        return tempCustMechBusinLicence;
    }

    /**
     * 营业执照信息-添加
     * 
     * @param anCustMechBusinLicence
     * @return
     */
    public CustMechBusinLicence addCustMechBusinLicence(CustMechBusinLicence anCustMechBusinLicence) {
        BTAssert.notNull(anCustMechBusinLicence, "营业执照信息不允许为空！");

        anCustMechBusinLicence.initAddValue();
        this.insert(anCustMechBusinLicence);
        return anCustMechBusinLicence;
    }

}