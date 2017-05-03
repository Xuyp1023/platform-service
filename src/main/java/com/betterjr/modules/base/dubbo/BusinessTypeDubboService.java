// Copyright (c) 2014-2017 Bytter. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.0 : 2017年4月19日, liuwl, creation
// ============================================================================
package com.betterjr.modules.base.dubbo;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.base.IBusinessTypeService;
import com.betterjr.modules.base.service.BusinessTypeService;

/**
 * @author liuwl
 *
 */
@Service(interfaceClass = IBusinessTypeService.class)
public class BusinessTypeDubboService implements IBusinessTypeService {
    @Resource
    private BusinessTypeService businessTypeService;

    /* (non-Javadoc)
     * @see com.betterjr.modules.base.IBusinessTypeService#webQueryList()
     */
    @Override
    public String webQueryTypeList() {
        return AjaxObject.newOk("业务类型查询成功！", businessTypeService.queryType()).toJson();
    }

    /* (non-Javadoc)
     * @see com.betterjr.modules.base.IBusinessTypeService#webQuerySimpleList()
     */
    @Override
    public String webQuerySimpleTypeList() {
        return AjaxObject.newOk("业务类型查询成功！", businessTypeService.querySimpleType()).toJson();
    }

}
