package com.yuan.common.annotation;

import com.yuan.common.enums.OptTypeEnum;

import java.lang.annotation.*;

@Target(ElementType.METHOD) //方法
@Retention(RetentionPolicy.RUNTIME) //运行时
@Documented
public @interface OptAnnotation {
    // 说明操作的类型
    OptTypeEnum value();

}
