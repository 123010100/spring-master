package org.spring.service;

import lombok.Getter;
import org.spring.annotation.Autowired;
import org.spring.annotation.Component;
import org.spring.annotation.Scope;
import org.spring.aware.BeanNameAware;
import org.spring.factory.InitializingBean;

@Component
@Scope("prototype")
@Getter
public class UserService implements BeanNameAware, InitializingBean {

    @Autowired
    private OrderService orderService;

    private String beanName;

    private boolean isProcessor;

    public void setProcessor(boolean processor) {
        this.isProcessor = processor;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(this.getBeanName() + "初始化完成");
    }
}
