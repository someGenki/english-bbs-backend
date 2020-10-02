package com.yuan.common.aspect;

import com.yuan.common.annotation.LogAnnotation;
import com.yuan.entity.Log;
import com.yuan.service.LogService;
import com.yuan.util.JwtUtils;
import com.yuan.util.ShiroUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 0. 引入aop依赖
 * 1. 编写个自定义注解类
 * 2. 编写aop实现类
 * 3. 在方法上添加自定义注解
 * <p>
 * <p>
 * 用户操作日志：切面处理类 记录ip,uid,操作描述
 * 参考 https://blog.csdn.net/textalign/article/details/83863475
 * https://www.cnblogs.com/wangshen31/p/9379197.html
 */
@Aspect
@Component
public class LogAspect {

    @Autowired
    LogService logService;
    @Autowired
    JwtUtils jwtUtils;

    //定义切点 @Pointcut
    //在注解的位置切入代码 也可以整合,就是把里面value直接给AfterReturning
    @Pointcut("@annotation(com.yuan.common.annotation.LogAnnotation)")
    public void logPointCut() {
    }

    //切面 配置通知
    @AfterReturning("logPointCut()")
    public void saveSysLog(JoinPoint joinPoint) {
        //从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取切入点所在的方法
        Method method = signature.getMethod();
        Log recordLog = new Log();

        //获取注解里的value
        LogAnnotation log = method.getAnnotation(LogAnnotation.class);
        if (log != null)
            recordLog.setState(log.value() + "");
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();//这个RequestContextHolder是Springmvc提供来获得请求的东西
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        Integer uid = null;
        try {
            // 就是刚刚登录 profile是空的,然后登录又要记录profile就会空指针异常
            // 所以要用token中解析
            uid = ShiroUtil.getUidFromProfile();
            recordLog.setRequestIp(request.getRemoteAddr());
            recordLog.setUid(uid);
            recordLog.setType(1);//对于类型，可以在Log注解上加个分隔符然后标记 或者SpEl
            logService.save(recordLog);
        } catch (Exception ignored) {
        }


    }
}
