package com.betterjr.modules.wechat.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.betterjr.common.data.SimpleDataEntity;
import com.betterjr.common.exception.SessionInvalidException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.modules.wechat.dao.SaleAccoBankInfoMapper;
import com.betterjr.modules.wechat.entity.SaleAccoBankInfo;

@Service
public class SaleAccoBankService extends BaseService<SaleAccoBankInfoMapper, SaleAccoBankInfo> {

    /**
     * 查询操作员管理的银行账户信息，主要用于交易类业务
     * 
     * @return
     */

    public List<SimpleDataEntity> findAccountBank() {
        List custList = UserUtils.findCustNoList();
        if (Collections3.isEmpty(custList)) {
            // throw new SessionInvalidException(20005, "not fund custinfo please relogin");
            return new ArrayList<SimpleDataEntity>();
        } else {
            List<SimpleDataEntity> list = new ArrayList();
            SimpleDataEntity sde;
            for (SaleAccoBankInfo accoBank : this.selectByListProperty("custNo", custList)) {
                // 只列状态正常的账户
                if ("0".equals(accoBank.getStatus())) {
                    sde = new SimpleDataEntity(buildShowName(accoBank), accoBank.getMoneyAccount().toString());
                    list.add(sde);
                }
            }
            return list;
        }
    }

    public SaleAccoBankInfo findSaleAccoBank(Long anMoneyAccount) {
        List custList = UserUtils.findCustNoList();
        if (Collections3.isEmpty(custList)) {
            // throw new SessionInvalidException(20005, "not find custinfo please relogin");
            logger.warn("not find CustNo List; please open account or relogin");
            return null;
        }
        Map<String, Object> map = new HashMap();
        map.put("moneyAccount", anMoneyAccount);
        map.put("custNo", custList);
        logger.info("获取银行账户信息:入参=" + map.toString());
        List<SaleAccoBankInfo> list = this.selectByProperty(map);
        if (Collections3.isEmpty(list)) {

            throw new SessionInvalidException(20005, "invalid moneyAccount, please relogin");
        } else {
            return Collections3.getFirst(list);
        }
    }

    private String buildShowName(SaleAccoBankInfo accoBank) {
        String tmpStr = accoBank.getBankAccount();
        return accoBank.getBankAcountName() + "[**" + tmpStr.substring(tmpStr.length() - 6) + "]";
    }

    public Long findMoneyAccount(Long anCustNo, String anTradeAccount, String anNetNo) {
        Map map = new HashMap();
        map.put("custNo", anCustNo);
        map.put("tradeAccount", anTradeAccount);
        map.put("netNo", anNetNo);
        List<SaleAccoBankInfo> list = this.selectByProperty(map);
        if (list.size() == 1) {
            return Collections3.getFirst(list).getMoneyAccount();
        } else if (list.size() > 1) {
            return -1L;
        } else {
            return 0L;
        }
    }

    /**
     * 
     * 检查银行账户是否存在
     * 
     * @param0 交易网点信息
     * @param1 银行账户信息
     * @return 检查账户是否存在如果存在，返回True,不存在，返回False
     * @throws 异常情况
     */
    public boolean checkAcooBankExists(String anNetNo, String anBankAccount) {
        if (StringUtils.isNotBlank(anNetNo) && StringUtils.isNotBlank(anBankAccount)) {
            Map<String, Object> map = new HashMap();
            map.put("netNo", anNetNo);
            map.put("bankAccount", anBankAccount);
            List list = this.selectByProperty(map);
            return list.size() > 0;
        }

        return false;
    }
}
