package org.spring;

import org.spring.application.MasterAnnotationApplicationContext;
import org.spring.config.AppConfig;
import org.spring.context.ApplicationContext;
import org.spring.proxy.UserService;

/**
 * @author Gepw
 * 2021-06-06 16:09:48
 */
public class SpringTest {

    public static void main(String[] args) {
        try {
            ApplicationContext applicationContext = new MasterAnnotationApplicationContext(AppConfig.class);

            UserService userService = (UserService) applicationContext.getBean("userService");

            userService.test();

        } catch (Exception e) {
            System.err.println("Application run error.");
            e.printStackTrace();
        }
    }
}
