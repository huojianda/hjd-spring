package com.danielhuo.hjd.spring.formework.aop.intercept;


public interface HjdMethodInterceptor {
    Object invoke(HjdMethodInvocation invocation) throws Throwable;
}
