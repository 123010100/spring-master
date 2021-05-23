package org.spring;

import org.spring.application.MasterAnnotationApplicationContext;
import org.spring.config.AppConfig;
import org.spring.context.ApplicationContext;
import org.spring.service.UserService;

public class SpringTest {

    public static void main(String[] args) {
        try {

            ApplicationContext applicationContext = new MasterAnnotationApplicationContext(AppConfig.class);

            UserService userService = (UserService) applicationContext.getBean("userService");

            System.out.println(userService);
            System.out.println(userService.getOrderService());

            UserService userService1 = (UserService) applicationContext.getBean("userService");
            System.out.println(userService1);

            System.out.println(userService1.getOrderService());

        } catch (Exception e) {
            System.out.println("error");
            e.printStackTrace();
        }
    }
}
