package com.betterjr.modules.customer.dao;

import java.util.List;

import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import com.betterjr.common.annotation.BetterjrMapper;
import com.betterjr.mapper.common.Mapper;
import com.betterjr.modules.customer.entity.CustRelation;

@BetterjrMapper
public interface CustRelationMapper extends Mapper<CustRelation> {
    @Select("select id from t_cust_relation where L_CUSTNO=#{anCustNo} and L_RELATE_CUSTNO=#{anRelateCustno} and L_PARTNER_CUSTNO=#{anPartnerCustNo} and C_RELATE_TYPE in (#{anRelateTypes})")
    @ResultType(CustRelation.class)
    public List<CustRelation> findOneRelation(Long anCustNo, Long anRelateCustno,String anPartnerCustNo,String anRelateTypes);
}