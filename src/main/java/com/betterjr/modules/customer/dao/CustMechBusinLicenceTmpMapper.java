package com.betterjr.modules.customer.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import com.betterjr.common.annotation.BetterjrMapper;
import com.betterjr.mapper.common.Mapper;
import com.betterjr.modules.customer.entity.CustMechBusinLicenceTmp;
import com.betterjr.modules.customer.helper.IVersionMapper;

@BetterjrMapper
public interface CustMechBusinLicenceTmpMapper extends Mapper<CustMechBusinLicenceTmp>, IVersionMapper {
    @Select("select max(N_VERSION) FROM t_cust_mech_busin_licence_tmp WHERE L_REF_ID=#{refId}")
    @ResultType(Long.class)
    @Override
    public Long selectMaxVersion(@Param("refId") Long refId);

    /**
     * 取比当前版本低,并且被使用的流水
     */
    @Select("SELECT MAX(N_VERSION) FROM t_cust_mech_busin_licence_tmp WHERE L_REF_ID=#{refId} AND N_VERSION < #{version} AND C_BUSIN_STATUS = '2'")
    @ResultType(Long.class)
    @Override
    public Long selectPrevVersion(@Param("refId") Long refId, @Param("version") Long version);
}