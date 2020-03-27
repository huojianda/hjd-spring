package com.danielhuo.hjd.spring.formework.aop.aspect;

import com.danielhuo.hjd.spring.formework.aop.intercept.HjdMethodInterceptor;
import com.danielhuo.hjd.spring.formework.aop.intercept.HjdMethodInvocation;

import java.lang.reflect.Method;


public class HjdAfterThrowingAdviceInterceptor extends HjdAbstractAspectAdvice implements HjdAdvice,HjdMethodInterceptor {


    private String throwingName;

    public HjdAfterThrowingAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(HjdMethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        }catch (Throwable e){
            invokeAdviceMethod(mi,null,e.getCause());
            throw e;
        }
    }

    public void setThrowName(String throwName){
        this.throwingName = throwName;
    }
}
