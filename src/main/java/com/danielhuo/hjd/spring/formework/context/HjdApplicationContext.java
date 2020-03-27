package com.danielhuo.hjd.spring.formework.context;

import com.danielhuo.hjd.spring.formework.annotation.HjdAutowired;
import com.danielhuo.hjd.spring.formework.annotation.HjdController;
import com.danielhuo.hjd.spring.formework.annotation.HjdService;
import com.danielhuo.hjd.spring.formework.aop.HjdAopProxy;
import com.danielhuo.hjd.spring.formework.aop.HjdCglibAopProxy;
import com.danielhuo.hjd.spring.formework.aop.HjdJdkDynamicAopProxy;
import com.danielhuo.hjd.spring.formework.aop.config.HjdAopConfig;
import com.danielhuo.hjd.spring.formework.aop.support.HjdAdvisedSupport;
import com.danielhuo.hjd.spring.formework.beans.HjdBeanWrapper;
import com.danielhuo.hjd.spring.formework.beans.config.HjdBeanPostProcessor;
import com.danielhuo.hjd.spring.formework.beans.config.HjdBeanDefinition;
import com.danielhuo.hjd.spring.formework.core.HjdBeanFactory;
import com.danielhuo.hjd.spring.formework.beans.support.HjdBeanDefinitionReader;
import com.danielhuo.hjd.spring.formework.beans.support.HjdDefaultListableBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IOC核心容器
 *
 */
public class HjdApplicationContext extends HjdDefaultListableBeanFactory implements HjdBeanFactory {

    private String [] configLoactions;
    private HjdBeanDefinitionReader reader;

    //单例的IOC容器缓存
    private Map<String,Object> factoryBeanObjectCache = new ConcurrentHashMap<String, Object>();
    //通用的IOC容器
    private Map<String,HjdBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, HjdBeanWrapper>();

    public HjdApplicationContext(String... configLoactions){
        this.configLoactions = configLoactions;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void refresh() throws Exception{
        //1、定位，定位配置文件
        reader = new HjdBeanDefinitionReader(this.configLoactions);

        //2、加载配置文件，扫描相关的类，把它们封装成BeanDefinition
        List<HjdBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

        //3、注册，把配置信息放到容器里面(伪IOC容器)
        doRegisterBeanDefinition(beanDefinitions);

        //4、把不是延时加载的类，有提前初始化
        doAutowrited();
    }

    //只处理非延时加载的情况
    private void doAutowrited() {
        for (Map.Entry<String, HjdBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
           String beanName = beanDefinitionEntry.getKey();
           if(!beanDefinitionEntry.getValue().isLazyInit()) {
               try {
                   getBean(beanName);
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }
        }
    }

    private void doRegisterBeanDefinition(List<HjdBeanDefinition> beanDefinitions) throws Exception {

        for (HjdBeanDefinition beanDefinition: beanDefinitions) {
            if(super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())){
                throw new Exception("The “" + beanDefinition.getFactoryBeanName() + "” is exists!!");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
        }
        //到这里为止，容器初始化完毕
    }
    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

    //依赖注入，从这里开始，通过读取BeanDefinition中的信息
    //然后，通过反射机制创建一个实例并返回
    //Spring做法是，不会把最原始的对象放出去，会用一个BeanWrapper来进行一次包装
    //装饰器模式：
    //1、保留原来的OOP关系
    //2、我需要对它进行扩展，增强（为了以后AOP打基础）
    @Override
    public Object getBean(String beanName) throws Exception {

        HjdBeanDefinition hjdBeanDefinition = this.beanDefinitionMap.get(beanName);
        Object instance = null;

        //这个逻辑还不严谨，自己可以去参考Spring源码
        //工厂模式 + 策略模式
        HjdBeanPostProcessor postProcessor = new HjdBeanPostProcessor();

        postProcessor.postProcessBeforeInitialization(instance,beanName);

        instance = instantiateBean(beanName, hjdBeanDefinition);

        //3、把这个对象封装到BeanWrapper中
        HjdBeanWrapper beanWrapper = new HjdBeanWrapper(instance);

        //4、把BeanWrapper存到IOC容器里面
//        //1、初始化

//        //class A{ B b;}
//        //class B{ A a;}
//        //先有鸡还是先有蛋的问题，一个方法是搞不定的，要分两次

        //2、拿到BeanWraoper之后，把BeanWrapper保存到IOC容器中去
        this.factoryBeanInstanceCache.put(beanName,beanWrapper);

        postProcessor.postProcessAfterInitialization(instance,beanName);

//        //3、注入
        populateBean(beanName,new HjdBeanDefinition(),beanWrapper);


        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }

    private void populateBean(String beanName, HjdBeanDefinition hjdBeanDefinition, HjdBeanWrapper hjdBeanWrapper) {
        Object instance = hjdBeanWrapper.getWrappedInstance();

//        hjdBeanDefinition.getBeanClassName();

        Class<?> clazz = hjdBeanWrapper.getWrappedClass();
        //判断只有加了注解的类，才执行依赖注入
        if(!(clazz.isAnnotationPresent(HjdController.class) || clazz.isAnnotationPresent(HjdService.class))){
            return;
        }

        //获得所有的fields
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if(!field.isAnnotationPresent(HjdAutowired.class)){ continue;}

            HjdAutowired autowired = field.getAnnotation(HjdAutowired.class);

            String autowiredBeanName =  autowired.value().trim();
            if("".equals(autowiredBeanName)){
                autowiredBeanName = field.getType().getName();
            }

            //强制访问
            field.setAccessible(true);

            try {
                //为什么会为NULL，先留个坑
                if(this.factoryBeanInstanceCache.get(autowiredBeanName) == null){ continue; }
//                if(instance == null){
//                    continue;
//                }
                field.set(instance,this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

    }

    private Object instantiateBean(String beanName, HjdBeanDefinition hjdBeanDefinition) {
        //1、拿到要实例化的对象的类名
        String className = hjdBeanDefinition.getBeanClassName();

        //2、反射实例化，得到一个对象
        Object instance = null;
        try {
//            hjdBeanDefinition.getFactoryBeanName()
            //假设默认就是单例,细节暂且不考虑
            if(this.factoryBeanObjectCache.containsKey(className)){
                instance = this.factoryBeanObjectCache.get(className);
            }else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                HjdAdvisedSupport config = instantionAopConfig(hjdBeanDefinition);
                config.setTargetClass(clazz);
                config.setTarget(instance);


                if(config.pointCutMatch()) {
                    /**
                     * 匹配到方法 ，需要进行aop切面
                     * 即使创建一个代理类 ，进行通知
                     */
                    instance = createProxy(config).getProxy();
                }

                this.factoryBeanObjectCache.put(className,instance);
                this.factoryBeanObjectCache.put(hjdBeanDefinition.getFactoryBeanName(),instance);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return instance;
    }

    private HjdAopProxy createProxy(HjdAdvisedSupport config) {

        Class targetClass = config.getTargetClass();
        if(targetClass.getInterfaces().length > 0){
            return new HjdJdkDynamicAopProxy(config);
        }
        return new HjdCglibAopProxy(config);
    }

    private HjdAdvisedSupport instantionAopConfig(HjdBeanDefinition hjdBeanDefinition) {
        HjdAopConfig config = new HjdAopConfig();
        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new HjdAdvisedSupport(config);
    }

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new  String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount(){
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig(){
        return this.reader.getConfig();
    }
}
