// Copyright (c) 2014-2016 Betty. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.0 : 2016年9月19日, liuwl, creation
// ============================================================================
package com.betterjr.modules.sms;

import org.junit.Test;

import com.betterjr.common.selectkey.SerialGenerator;

/**
 * @author liuwl
 *
 */
public class VerificationCodeTest {

    @Test
    public void testVerifiCodeGen() {

        System.out.println(getRandom());
        System.out.println(getRandom());
        System.out.println(getRandom());
        System.out.println(getRandom());
        System.out.println(getRandom());
        System.out.println(getRandom());
        System.out.println(getRandom());
        System.out.println(getRandom());
    }
    public static String getRandom()
    {
        final int s = SerialGenerator.randomInt(999999) % (999999 - 100000 + 1) + 100000;
        return String.valueOf(s);
    }

}
