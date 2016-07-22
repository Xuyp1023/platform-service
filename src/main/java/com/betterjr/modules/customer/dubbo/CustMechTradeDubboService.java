package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.modules.customer.ICustMechTradeService;

/**
 * 贸易信息
 * 
 * @author liuwl
 *
 */
@Service(interfaceClass = ICustMechTradeService.class)
public class CustMechTradeDubboService implements ICustMechTradeService {

    @Override
    public String webAddTradeRecord(Map<String, Object> anParam, Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webFindTradeRecord(Long anCustNo, Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webQueryTradeRecordList(Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webSaveTradeRecord(Map<String, Object> anParam, Long anCustNo, Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

}
