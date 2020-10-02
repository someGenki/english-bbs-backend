package com.yuan.common.aspect;


import com.yuan.common.annotation.LogAnnotation;
import com.yuan.common.annotation.OptAnnotation;
import com.yuan.entity.OptLogs;
import com.yuan.service.OptLogsService;
import com.yuan.util.ShiroUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class OptAspect {

    @Autowired
    OptLogsService optLogsService;


    @AfterReturning(value = "@annotation(optAnnotation)", returning = "returnVal")
    public void after(JoinPoint joinPoint, OptAnnotation optAnnotation, Object returnVal) {
        OptLogs opt = new OptLogs();
        opt.setUid(ShiroUtil.getUidFromProfile());
        opt.setOptType(optAnnotation.value().getType());
        opt.setOptId((Integer) returnVal);
        optLogsService.save(opt);
    }

}
