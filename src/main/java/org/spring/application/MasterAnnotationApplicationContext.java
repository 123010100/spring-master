package org.spring.application;

import org.spring.annotation.Component;
import org.spring.annotation.ComponentScan;
import org.spring.annotation.Scope;
import org.spring.context.ApplicationContext;
import org.spring.definition.BeanDefinition;
import org.spring.util.ClassHandlerUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class MasterAnnotationApplicationContext implements ApplicationContext {

    // 单例池
    private static ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();

    // bean定义池
    private static ConcurrentHashMap<String, BeanDefinition> beanDefinitionObjects = new ConcurrentHashMap<>();

    private Class<?> configClass;

    public MasterAnnotationApplicationContext(Class<?> configClass) throws Exception {
        this.configClass = configClass;
        // 扫描
        scan();
        // 创建扫面后的bean
        create();
    }

    // 扫描
    private void scan() throws ClassNotFoundException {
        // 解析配置类
        // 1.ComponentScan
        ComponentScan componentScan = configClass.getDeclaredAnnotation(ComponentScan.class);
        String[] scanValue = componentScan.value();

        // 扫描包下的类
        // 类加载器 1、Bootstrap --> jre/lib 2、Ext --> jre/ext/lib 3、App --> classpath
        // 1.Component
        for (String url : scanValue) {
            ClassLoader classLoader = this.getClass().getClassLoader();
            URL resource = classLoader.getResource(ClassHandlerUtil.formatUrlPointToVirgule(url));

            assert resource != null;

            File file = new File(resource.getFile());
            if (file.isDirectory()) {
                File[] files = file.listFiles();

                assert files != null;

                for (File f : files) {
                    String filePath = f.getPath();

                    // 是不是一个class文件
                    if (!ClassHandlerUtil.isClassFile(filePath)) continue;

                    // 解析到class文件路径
                    String clazzPath = ClassHandlerUtil.formatUrlVirguleToPoint(
                            filePath.substring(filePath.indexOf("org"), filePath.indexOf(".class")));

                    // 加载类
                    Class<?> clazz = classLoader.loadClass(clazzPath);
                    // 是否声明了Component注解
                    if (clazz.isAnnotationPresent(Component.class)) {
                        // 当前类为Bean
                        // 开始解析bean
                        Component component = clazz.getDeclaredAnnotation(Component.class);
                        String beanName = component.value();

                        // 为每一个bean初始化一个定义对象
                        BeanDefinition beanDefinition = new BeanDefinition();
                        beanDefinition.setBeanName(beanName);
                        beanDefinition.setClazz(clazz);
                        // bean的定义设置
                        // 是否单例
                        if (clazz.isAnnotationPresent(Scope.class)) {
                            // 根据使用者定义
                            Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                            beanDefinition.setScope(scopeAnnotation.value());
                        } else {
                            // 单例的
                            beanDefinition.setScope("singleton");
                        }

                        // bean定义放入定义池
                        beanDefinitionObjects.put(beanName, beanDefinition);


                    }
                }
            }
        }
    }

    // 创建
    private void create() {
        // 从beanDefinition中创建对象
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionObjects.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = beanDefinitionObjects.get(beanName);
            // 创建单例对象到单例池
            if ("singleton".equals(beanDefinition.getScope())) {
                // 放入创建好的单例对象到单例池
                singletonObjects.put(beanName, this.createBean(beanDefinition));
            }
        }
    }

    // 创建bean对象
    public Object createBean(BeanDefinition beanDefinition) {
        Class<?> clazz = beanDefinition.getClazz();
        // 通过无参构造初始化对象
        Object o = null;
        try {
            o = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        if (!beanDefinitionObjects.containsKey(beanName)) {
            throw new Exception("[" + beanName + "] is not find.");
        }

        BeanDefinition beanDefinition = beanDefinitionObjects.get(beanName);
        // 单例
        if (beanDefinition.getScope().equals("singleton")) {
            return singletonObjects.get(beanName);
        }

        // 原型（创建bean）
        return createBean(beanDefinition);
    }
}
