package com.danielhuo.hjd.spring.formework.beans.support;

import com.danielhuo.hjd.spring.formework.beans.config.HjdBeanDefinition;
import com.danielhuo.hjd.spring.formework.context.support.HjdAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class HjdDefaultListableBeanFactory extends HjdAbstractApplicationContext {

    //存储注册信息的BeanDefinition,伪IOC容器
    protected final Map<String, HjdBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, HjdBeanDefinition>();
}
