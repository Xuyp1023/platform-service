// Copyright (c) 2014-2017 Bytter. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.0 : 2017年4月12日, liuwl, creation
// ============================================================================
package com.betterjr.modules.generator;

import org.junit.Test;

import com.betterjr.modules.BasicServiceTest;
import com.betterjr.modules.generator.service.SequenceService;

/**
 * @author liuwl
 *
 */
public class SequenceGeneratorTest extends BasicServiceTest<SequenceService> {

    /* (non-Javadoc)
     * @see com.betterjr.modules.BasicServiceTest#getTargetServiceClass()
     */
    @Override
    public Class<SequenceService> getTargetServiceClass() {
        return SequenceService.class;
    }

    @Test
    public void test1() {
        final String test1 = SequenceFactory.generate("TEST_SEQ", "TEST-#{Date:yy}-#{Seq:14}", "Y");
        final String test2 = SequenceFactory.generate("TEST_SEQ", "TEST-#{Date:yy}-#{Seq:14}", "Y");
        final String test3 = SequenceFactory.generate("TEST_SEQ", "TEST-#{Date:yy}-#{Seq:14}", "Y");
        final String test4 = SequenceFactory.generate("TEST_SEQ", "TEST-#{Date:yy}-#{Seq:14}", "Y");

        SequenceFactory.generate("PLAT_BILL", "TA#{Date:yy}${Seq:14}", "Y");

        final String test5 = SequenceFactory.generate("TEST_SEQ_HT", "测试公司", "TEST-合同-#{Date:yyMM}-#{Seq:14}", "Y");
        final String test6 = SequenceFactory.generate("TEST_SEQ_HT", "测试公司", "TEST-合同-#{Date:yyMM}-#{Seq:14}", "Y");

        final String test7 = SequenceFactory.generate("TEST_SEQ_HT", "测试公司1", "TEST-合同-#{Date:yyMM}-#{Seq:14}", "Y");
        final String test8 = SequenceFactory.generate("TEST_SEQ_HT", "测试公司1", "TEST-合同-#{Date:yyMM}-#{Seq:14}", "Y");

        final String test9 = SequenceFactory.generate("PLAT_COMMON", "#{Date:yyyyMMdd}#{Seq:12}", "Y");
        final String test91 = SequenceFactory.generate("PLAT_COMMON", "#{Date:yyyyMMdd}#{Seq:12}", "Y");
        final String test92 = SequenceFactory.generate("PLAT_COMMON", "#{Date:yyyyMMdd}#{Seq:12}", "Y");
        final String test93 = SequenceFactory.generate("PLAT_COMMON", "#{Date:yyyyMMdd}#{Seq:12}", "Y");
        final String test94 = SequenceFactory.generate("PLAT_COMMON", "#{Date:yyyyMMdd}#{Seq:12}", "Y");
        System.out.println(test1);
        System.out.println(test2);
        System.out.println(test3);
        System.out.println(test4);

        System.out.println(test5);
        System.out.println(test6);

        System.out.println(test7);
        System.out.println(test8);

        System.out.println(test9);
        System.out.println(test91);
        System.out.println(test92);
        System.out.println(test93);
        System.out.println(test94);
    }
}
