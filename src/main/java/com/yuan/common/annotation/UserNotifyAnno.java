package com.yuan.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD) //方法
@Retention(RetentionPolicy.RUNTIME) //运行时
@Documented
public @interface UserNotifyAnno {
    String value() default "";

    String type() default "";

}
