package org.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Scope {
    // Default is singleton
    // The optional values are singleton or prototype
    // If it is not a singleton, it must be prototype
    String value() default "singleton";
}
