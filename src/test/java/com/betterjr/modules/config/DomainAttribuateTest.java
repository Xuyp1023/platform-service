// Copyright (c) 2014-2017 Bytter. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.0 : 2017年5月3日, liuwl, creation
// ============================================================================
package com.betterjr.modules.config;

import org.junit.Test;

import com.betterjr.modules.BasicServiceTest;
import com.betterjr.modules.config.service.DomainAttributeService;

import junit.framework.Assert;

/**
 * @author liuwl
 *
 */
public class DomainAttribuateTest extends BasicServiceTest<DomainAttributeService> {

    @Test
    public void testHello() {
        final DomainAttributeService domainAttributeService = this.getServiceObject();
        domainAttributeService.saveString("hello", "abc");

        final String value = domainAttributeService.findString("hello");

        Assert.assertEquals(value, "abc");
    }

    /* (non-Javadoc)
     * @see com.betterjr.modules.BasicServiceTest#getTargetServiceClass()
     */
    @Override
    public Class<DomainAttributeService> getTargetServiceClass() {
        return DomainAttributeService.class;
    }

}
