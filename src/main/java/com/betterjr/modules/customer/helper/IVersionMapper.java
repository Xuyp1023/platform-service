package com.betterjr.modules.customer.helper;

import org.apache.ibatis.annotations.Param;

public interface IVersionMapper {
    public Long selectMaxVersion(@Param("refId") Long refId);
    
    public Long selectPrevVersion(@Param("refId") Long refId, @Param("version") Long version);
}
