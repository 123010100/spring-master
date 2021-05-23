package org.spring.context;


public interface ApplicationContext {

    Object getBean(String beanName) throws Exception;

}
