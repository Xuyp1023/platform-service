// Copyright (c) 2014-2017 Bytter. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.3 : 2017年4月17日, liuwl, creation
// ============================================================================
package com.betterjr.modules.base.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.betterjr.common.data.SimpleDataEntity;
import com.betterjr.common.service.BaseService;
import com.betterjr.modules.base.dao.BusinessTypeMapper;
import com.betterjr.modules.base.entity.BusinessType;

/**
 * @author liuwl
 *
 */
@Service
public class BusinessTypeService extends BaseService<BusinessTypeMapper, BusinessType> {
    /**
     * 查询所有 业务类型
     *
     * @return
     */
    public List<BusinessType> queryType() {
        return this.selectAll();
    }

    /**
     * 查询所有业务类型 并按 SimpleDataEntity 返回
     * @return
     */
    public List<SimpleDataEntity> querySimpleType() {
        return this.selectAll().stream()
                .map(businessType -> new SimpleDataEntity(businessType.getName(), businessType.getId().toString()))
                .collect(Collectors.toList());
    }
}
