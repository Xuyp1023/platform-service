package com.betterjr.modules.workflow.dao;

import java.util.List;

import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import com.betterjr.common.annotation.BetterjrMapper;
import com.betterjr.mapper.common.Mapper;
import com.betterjr.modules.workflow.entity.CustFlowMoney;

@BetterjrMapper
public interface CustFlowMoneyMapper extends Mapper<CustFlowMoney> {
    @Select("SELECT mon.`ID` AS id,mon.`F_ADUIT_MIN_AMOUNT` AS F_ADUIT_MIN_AMOUNT,mon.`F_ADUIT_MAX_AMOUNT` AS F_ADUIT_MAX_AMOUNT FROM `t_cust_flow_money` mon WHERE mon.`C_ADUIT_ALL`=0")
    @ResultType(CustFlowMoney.class)
    public List<CustFlowMoney> findAllValiableClasses();
}