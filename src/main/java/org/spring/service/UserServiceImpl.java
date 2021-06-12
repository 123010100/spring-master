package org.spring.service;

import lombok.Getter;
import org.spring.annotation.Autowired;
import org.spring.annotation.Component;
import org.spring.aware.BeanNameAware;
import org.spring.factory.InitializingBean;
import org.spring.proxy.UserService;

@Component("userService")
// @Scope("prototype")
@Getter
public class UserServiceImpl implements UserService, BeanNameAware, InitializingBean {

    @Autowired
    private OrderService orderService;

    private String beanName;

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(this.getBeanName() + "初始化");
    }

    @Override
    public void test() {
        System.out.println("beanName=" + beanName);
    }
}
