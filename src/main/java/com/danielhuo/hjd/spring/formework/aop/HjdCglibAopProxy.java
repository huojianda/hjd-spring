package com.danielhuo.hjd.spring.formework.aop;

import com.danielhuo.hjd.spring.formework.aop.support.HjdAdvisedSupport;


public class HjdCglibAopProxy implements HjdAopProxy {
    public HjdCglibAopProxy(HjdAdvisedSupport config) {
    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
