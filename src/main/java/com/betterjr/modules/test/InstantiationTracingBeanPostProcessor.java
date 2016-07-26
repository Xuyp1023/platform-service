package com.betterjr.modules.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class InstantiationTracingBeanPostProcessor implements BeanPostProcessor {  
    protected final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
  
    // simply return the instantiated bean as-is  
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {  
        return bean; // we could potentially return any object reference here...  
    }  
    //在创建bean后输出bean的信息  
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {  
        logger.info("启动 Bean '" + beanName + "' created : " + bean.toString());
        return bean;  
    }  
}  