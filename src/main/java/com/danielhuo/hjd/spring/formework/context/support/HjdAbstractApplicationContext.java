package com.danielhuo.hjd.spring.formework.context.support;

/**
 * IOC容器实现的顶层设计
 */
public abstract class HjdAbstractApplicationContext {
    //受保护，只提供给子类重写
    public void refresh() throws Exception {}
}
