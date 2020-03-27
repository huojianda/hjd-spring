package com.danielhuo.hjd.spring.formework.aop;


public interface HjdAopProxy {


    Object getProxy();


    Object getProxy(ClassLoader classLoader);
}
