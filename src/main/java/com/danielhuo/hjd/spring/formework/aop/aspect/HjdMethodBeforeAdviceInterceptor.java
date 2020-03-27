package com.danielhuo.hjd.spring.formework.aop.aspect;

import com.danielhuo.hjd.spring.formework.aop.intercept.HjdMethodInterceptor;
import com.danielhuo.hjd.spring.formework.aop.intercept.HjdMethodInvocation;

import java.lang.reflect.Method;


public class HjdMethodBeforeAdviceInterceptor extends HjdAbstractAspectAdvice implements HjdAdvice,HjdMethodInterceptor {


    private HjdJoinPoint joinPoint;
    public HjdMethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    private void before(Method method,Object[] args,Object target) throws Throwable{
        //传送了给织入参数
        //method.invoke(target);
        super.invokeAdviceMethod(this.joinPoint,null,null);

    }
    @Override
    public Object invoke(HjdMethodInvocation mi) throws Throwable {
        //从被织入的代码中才能拿到，JoinPoint
        this.joinPoint = mi;
        before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }
}
