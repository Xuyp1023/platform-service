package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.modules.customer.ICustMechFinanceService;

/**
 * 财务上传记录
 * 
 * @author liuwl
 *
 */
@Service(interfaceClass = ICustMechFinanceService.class)
public class CustMechFinanceDubboService implements ICustMechFinanceService {

    @Override
    public String webAddFinanceInfo(Map<String, Object> anParam, Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webFindFinanceInfo(Long anCustNo, Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webQueryFinanceList(Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webSaveFinanceInfo(Map<String, Object> anParam, Long anCustNo, Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

}
