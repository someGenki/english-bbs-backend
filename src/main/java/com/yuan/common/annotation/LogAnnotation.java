package com.yuan.common.annotation;


import java.lang.annotation.*;

/**
 * 标记方法,使其可以配合AOP记录调用日志
 *
 * @author yuan
 */
@Target(ElementType.METHOD) //方法
@Retention(RetentionPolicy.RUNTIME) //运行时
@Documented
public @interface LogAnnotation {
    String value() default "";
}
