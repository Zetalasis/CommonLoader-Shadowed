package com.zetalasis.commonloader.inject.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MethodInject {
    public String method() default "";
    public InjectPosition position() default InjectPosition.HEAD;
}