package org.spring.definition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BeanDefinition {

    private String beanName;

    private Class<?> clazz;

    private String scope;

}
