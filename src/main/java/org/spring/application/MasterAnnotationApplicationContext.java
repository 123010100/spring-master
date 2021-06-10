package org.spring.application;

import org.apache.commons.lang3.StringUtils;
import org.spring.annotation.Autowired;
import org.spring.annotation.Component;
import org.spring.annotation.ComponentScan;
import org.spring.annotation.Scope;
import org.spring.aware.BeanNameAware;
import org.spring.context.ApplicationContext;
import org.spring.definition.BeanDefinition;
import org.spring.factory.BeanPostProcessor;
import org.spring.factory.InitializingBean;
import org.spring.util.ClassHandlerUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


public class MasterAnnotationApplicationContext implements ApplicationContext {

    // 单例池
    private static ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();

    // bean定义池
    private static ConcurrentHashMap<String, BeanDefinition> beanDefinitionObjects = new ConcurrentHashMap<>();

    private static List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

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
        if (Objects.isNull(componentScan)) return;
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

                        // 是否是一个beanPostProcessor
                        boolean isAssignableFrom = BeanPostProcessor.class.isAssignableFrom(clazz);
                        if (isAssignableFrom) {
                            try {
                                BeanPostProcessor beanPostProcessor = (BeanPostProcessor) clazz.getDeclaredConstructor().newInstance();
                                beanPostProcessorList.add(beanPostProcessor);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        // 当前类为Bean
                        // 开始解析bean
                        Component component = clazz.getDeclaredAnnotation(Component.class);
                        String beanName = component.value();

                        // default beanName
                        if (StringUtils.isBlank(beanName)) {
                            // System.out.println("[" + clazz.getTypeName() + "]No defined bean name.");
                            beanName = StringUtils.uncapitalize(clazz.getSimpleName());
                        }

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
    @Override
    public Object createBean(BeanDefinition beanDefinition) {
        String beanName = beanDefinition.getBeanName();
        Class<?> clazz = beanDefinition.getClazz();

        // 通过无参构造初始化对象
        Object instance = null;
        try {
            // 通过无参构造实例化对象
            instance = clazz.getDeclaredConstructor().newInstance();

            // 依赖注入 对属性进行赋值
            for (Field field : clazz.getDeclaredFields()) {
                // 只针对有自动注入的属性
                if (field.isAnnotationPresent(Autowired.class)) {
                    field.setAccessible(true); // 允许对private进行操作
                    field.set(instance, this.getBean(field.getName()));
                }
            }

            // aware
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) instance).setBeanName(beanName);
            }

            // 初始化前
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
            }

            // 初始化
            if (instance instanceof InitializingBean) {
                ((InitializingBean) instance).afterPropertiesSet();
            }

            // 初始化后
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessAfterInitialization(instance, beanName);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        if (!beanDefinitionObjects.containsKey(beanName)) {
            throw new Exception("[" + beanName + "] is not find.");
        }

        BeanDefinition beanDefinition = beanDefinitionObjects.get(beanName);
        // 单例
        if (StringUtils.equals("singleton", beanDefinition.getScope())) {
            return singletonObjects.get(beanName);
        }

        // 原型（创建bean）
        return createBean(beanDefinition);
    }
}
