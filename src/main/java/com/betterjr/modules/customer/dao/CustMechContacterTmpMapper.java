package com.betterjr.modules.customer.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import com.betterjr.common.annotation.BetterjrMapper;
import com.betterjr.mapper.common.Mapper;
import com.betterjr.modules.customer.entity.CustMechContacterTmp;
import com.betterjr.modules.customer.helper.IVersionMapper;

@BetterjrMapper
public interface CustMechContacterTmpMapper extends Mapper<CustMechContacterTmp>,IVersionMapper {
    @Select("select max(n_version) from t_cust_mech_contacter_tmp where L_REF_ID=#{refId}")
    @ResultType(Long.class)
    @Override
    public Long selectMaxVersion(@Param("refId") Long refId);
}