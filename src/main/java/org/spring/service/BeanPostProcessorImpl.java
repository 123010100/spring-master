package org.spring.service;

import org.apache.commons.lang3.StringUtils;
import org.spring.annotation.Component;
import org.spring.factory.BeanPostProcessor;

/**
 * 统一的BeanPostProcessor
 */
@Component
public class BeanPostProcessorImpl implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        System.out.println("after bean init.");
        if (StringUtils.equals("userService", beanName)) {
            ((UserService) bean).setProcessor(true);
        }
        return bean;
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        System.out.println("before bean init.");
        return bean;
    }
}
