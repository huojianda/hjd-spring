package com.danielhuo.hjd.spring.formework.aop.aspect;

import com.danielhuo.hjd.spring.formework.aop.intercept.HjdMethodInterceptor;
import com.danielhuo.hjd.spring.formework.aop.intercept.HjdMethodInvocation;

import java.lang.reflect.Method;


public class HjdAfterReturningAdviceInterceptor extends HjdAbstractAspectAdvice implements HjdAdvice,HjdMethodInterceptor {

    private HjdJoinPoint joinPoint;

    public HjdAfterReturningAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(HjdMethodInvocation mi) throws Throwable {
        Object retVal = mi.proceed();
        this.joinPoint = mi;
        this.afterReturning(retVal,mi.getMethod(),mi.getArguments(),mi.getThis());
        return retVal;
    }

    private void afterReturning(Object retVal, Method method, Object[] arguments, Object aThis) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint,retVal,null);
    }
}
