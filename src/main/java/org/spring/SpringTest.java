package org.spring;

import org.spring.application.MasterAnnotationApplicationContext;
import org.spring.config.AppConfig;
import org.spring.context.ApplicationContext;

public class SpringTest {

    public static void main(String[] args) {
        try {

            ApplicationContext applicationContext = new MasterAnnotationApplicationContext(AppConfig.class);

            System.out.println(applicationContext.getBean("userService"));
            System.out.println(applicationContext.getBean("userService"));
            System.out.println(applicationContext.getBean("userService"));

        } catch (Exception e) {
            System.out.println("error");
            e.printStackTrace();
        }
    }
}
