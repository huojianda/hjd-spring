package com.danielhuo.hjd.spring.formework.aop;

import com.danielhuo.hjd.spring.formework.aop.intercept.HjdMethodInvocation;
import com.danielhuo.hjd.spring.formework.aop.support.HjdAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;


public class HjdJdkDynamicAopProxy implements HjdAopProxy,InvocationHandler{

    private HjdAdvisedSupport advised;

    public HjdJdkDynamicAopProxy(HjdAdvisedSupport config){
        this.advised = config;
    }

    @Override
    public Object getProxy() {
        return getProxy(this.advised.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader,this.advised.getTargetClass().getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> interceptorsAndDynamicMethodMatchers = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method,this.advised.getTargetClass());
        HjdMethodInvocation invocation = new HjdMethodInvocation(proxy,this.advised.getTarget(),method,args,this.advised.getTargetClass(),interceptorsAndDynamicMethodMatchers);
        return invocation.proceed();
    }
}
