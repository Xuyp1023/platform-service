// Copyright (c) 2014-2016 Betty. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.0 : 2016年9月22日, liuwl, creation
// ============================================================================
package com.betterjr.modules;

/**
 * @author liuwl
 *
 */
public class TestLambda {

    @FunctionalInterface
    static interface Hello {
        String say(String xxx);
    }

    public static String sayHello(final Hello hello) {
        return hello.say("hello world");
    }

    public static String print(final String string) {
        System.out.println(string);
        return "你好";
    }
    /**
     * @param args
     */
    public static void main(final String[] args) {
        System.out.println(sayHello(TestLambda::print));
    }

}
