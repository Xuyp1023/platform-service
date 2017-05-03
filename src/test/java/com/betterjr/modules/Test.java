// Copyright (c) 2014-2017 Bytter. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.0 : 2017年4月13日, liuwl, creation
// ============================================================================
package com.betterjr.modules;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuwl
 *
 */
public class Test {
    static class Hello {
        public void sayHello(final Map param) {
            System.out.println("Hello" + param);
        }

        public void sayWorld(final Map param) {
            System.out.println("World" + param);
        }
    }


    public static void main(final String[] args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final String methodName = args[0];

        final Hello hello = new Hello();
        final Method method = Hello.class.getMethod(methodName, Map.class);

        final Map param = new HashMap();

        param.put("hello", "World");
        method.invoke(hello, param);
    }

}
