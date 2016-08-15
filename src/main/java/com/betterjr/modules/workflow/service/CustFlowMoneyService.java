package com.betterjr.modules.workflow.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.modules.workflow.dao.CustFlowMoneyMapper;
import com.betterjr.modules.workflow.entity.CustFlowMoney;

@Service
public class CustFlowMoneyService extends BaseService<CustFlowMoneyMapper,CustFlowMoney>{
    public List<CustFlowMoney> findAllValiableClasses(){
        return this.mapper.findAllValiableClasses();
    }

}
