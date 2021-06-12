package org.spring.service;

import org.apache.commons.lang3.StringUtils;
import org.spring.annotation.Component;
import org.spring.factory.BeanPostProcessor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 统一的BeanPostProcessor
 */
@Component
public class BeanPostProcessorImpl implements BeanPostProcessor {


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        if (StringUtils.equals("userService", beanName)) {
            System.out.println("before bean.");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        if (StringUtils.equals("userService", beanName)) {
            System.out.println("after bean.");
            return Proxy.newProxyInstance(this.getClass().getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    System.out.println("代理逻辑Before");
                    Object object = method.invoke(bean, args);
                    System.out.println("代理逻辑After");
                    return object;
                }
            });
        }
        return bean;
    }


}
