package com.betterjr.modules.customer.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import com.betterjr.common.annotation.BetterjrMapper;
import com.betterjr.mapper.common.Mapper;
import com.betterjr.modules.customer.entity.CustRelation;

@BetterjrMapper
public interface CustRelationMapper extends Mapper<CustRelation> {
    @Select("select id from t_cust_relation where L_CUSTNO=#{anCustNo} and L_RELATE_CUSTNO=#{anRelateCustno} and L_PARTNER_CUSTNO=#{anPartnerCustNo} and C_RELATE_TYPE in (#{anRelateTypes})")
    @ResultType(CustRelation.class)
    public List<CustRelation> findOneRelation(Long anCustNo, Long anRelateCustno, String anPartnerCustNo, String anRelateTypes);
    
    @Select("select a.* from t_cust_relation a left join t_cust_relation b on a.L_RELATE_CUSTNO = b.L_CUSTNO where a.L_RELATE_CUSTNO = #{anCoreCustNo} and a.C_RELATE_TYPE in ('1', '4') and b.L_RELATE_CUSTNO in (#{anFactorCustNo})")
    @ResultType(CustRelation.class)
    public List<CustRelation> findDataByFactorAndCore(@Param("anCoreCustNo") Long anCoreCustNo, @Param("anFactorCustNo")String anFactorCustNo);
}