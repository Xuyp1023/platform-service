package com.betterjr.modules.customer.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import com.betterjr.common.annotation.BetterjrMapper;
import com.betterjr.mapper.common.Mapper;
import com.betterjr.modules.customer.entity.CustMechShareholderTmp;
import com.betterjr.modules.customer.helper.IVersionMapper;

@BetterjrMapper
public interface CustMechShareholderTmpMapper extends Mapper<CustMechShareholderTmp>, IVersionMapper {
    // 列表类型需要使用 L_CUSTNO
    @Select("SELECT MAX(n_version) FROM t_cust_mech_shareholder_tmp WHERE L_CUSTNO=#{refId} AND C_BUSIN_STATUS='2'")
    @ResultType(Long.class)
    @Override
    public Long selectMaxVersion(@Param("refId") Long refId);

    // 列表类型需要使用 L_CUSTNO
    @Select("SELECT MAX(n_version) FROM t_cust_mech_shareholder_tmp WHERE L_CUSTNO=#{refId} AND N_VERSION < #{version} AND  C_BUSIN_STATUS='2'")
    @ResultType(Long.class)
    @Override
    public Long selectPrevVersion(@Param("refId") Long refId, @Param("version") Long version);
}