package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.modules.customer.ICustMechCooperationService;

/**
 * 合作企业
 * 
 * @author liuwl
 *
 */
@Service(interfaceClass = ICustMechCooperationService.class)
public class CustMechCooperationDubboService implements ICustMechCooperationService {

    @Override
    public String webAddCooperation(Map<String, Object> anParam, Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webFindCooperation(Long anCustNo, Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webQueryCooperationList(Long anCustNo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String webSaveCooperation(Map<String, Object> anParam, Long anCustNo, Long anId) {
        // TODO Auto-generated method stub
        return null;
    }

}
