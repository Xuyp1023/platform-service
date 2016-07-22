package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.modules.customer.ICustFinancingService;

/**
 * 融资情况
 * 
 * @author liuwl
 *
 */
@Service(interfaceClass = ICustFinancingService.class)
public class CustFinancingDubboService implements ICustFinancingService {

    @Override
    public String webAddFinancing(Map<String, Object> anParam, Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webFindFinancing(Long anCustNo, Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webQueryFinancingList(Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webSaveFinancing(Map<String, Object> anParam, Long anCustNo, Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

}
