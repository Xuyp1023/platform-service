package com.betterjr.modules;

import java.net.URL;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Log4jConfigurer;

public class Provider {

    public static void main(String[] args) throws Exception {
        Provider.class.getClassLoader();
        URL url = ClassLoader.getSystemResource("log4j.properties");
        System.out.println(url.toString());
        Log4jConfigurer.initLogging(url.getFile());
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "spring-context-platform-dubbo-provider.xml" });
        context.start();
        System.out.println("platform-service 已经启动");
        System.in.read();
        context.close();
        System.exit(0);
    }

}
