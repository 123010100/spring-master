package org.spring.context;


import org.spring.definition.BeanDefinition;

public interface ApplicationContext {

    Object getBean(String beanName) throws Exception;

    Object createBean(BeanDefinition beanDefinition);
}
