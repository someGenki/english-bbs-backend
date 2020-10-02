package com.yuan.common.aspect;

import com.yuan.common.annotation.UserNotifyAnno;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
@Component
public class UserNotifyAop {

    /**
     * AfterReturning 后置通知方法 注解的参数是切点表达式
     *
     * @param joinPoint            切入点
     * @param userNotifyAnnotation 方法上的注解,用于获取注解里的值
     * @param returnVal            方法的返回值,不能修改,只能看
     */
    @AfterReturning(value = "@annotation(userNotifyAnnotation)", returning = "returnVal")
    public void after(JoinPoint joinPoint, UserNotifyAnno userNotifyAnnotation, Object returnVal) {
        System.out.println("======================");
        Signature signature = joinPoint.getSignature();// 方法签名
        Method method = ((MethodSignature) signature).getMethod(); //方法信息
        Object[] in = joinPoint.getArgs();// 方法参数
        Object target = joinPoint.getTarget();// 目标对象 相当于 this
        System.out.println("签名:" + signature);
        System.out.println("返回值:" + returnVal);
        System.out.println("方法名字:" + method.getName());
        System.out.println("第一个参数:" + in[0]);
        System.out.println("解析SpEL的值:" + parseSpEL(userNotifyAnnotation.type(), method, in, String.class));
        System.out.println("======================");
    }


    /**
     * 解析SpEL表达式
     *
     * @param key    SpEL表达式
     * @param method 反射得到的方法
     * @param args   反射得到的方法参数
     * @return 解析后SpEL表达式对应的值
     */
    private String parseKey(String key, Method method, Object[] args) {
        // 获取被拦截方法参数名列表(使用Spring支持类库)
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] paraNameArr = u.getParameterNames(method);
        // 使用SpEL进行key的解析
        ExpressionParser parser = new SpelExpressionParser();
        // SpEL上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        // 把方法参数放入SpEL上下文中
        for (int i = 0; i < paraNameArr.length; i++) {
            // 使用setVariable方法来注册自定义变量
            context.setVariable(paraNameArr[i], args[i]);
        }
        return parser.parseExpression(key).getValue(context, String.class);
    }

    /**
     * 解析SpEL表达式,配合注解使用
     *
     * @param key    SpEL表达式
     * @param method 通过反射得到的方法 Method
     * @param args   通过反射得到的方法参数 Object[]
     * @param clazz  需要返回的类型 String Boolean之类的
     * @param <T>    返回泛型
     * @return 解析后得到的值
     */
    public <T> T parseSpEL(String key, Method method, Object[] args, Class<T> clazz) {
        // SpEL上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        // 获取被拦截方法参数名列表(使用Spring支持类库)
        String[] paraNameArr = new LocalVariableTableParameterNameDiscoverer().getParameterNames(method);
        // 把方法参数放入SpEL上下文中
        for (int i = 0; i < Objects.requireNonNull(paraNameArr).length; i++) {
            // 使用setVariable方法来注册自定义变量
            context.setVariable(paraNameArr[i], args[i]);
        }
        // 使用SpEL进行key的解析
        return new SpelExpressionParser().parseExpression(key).getValue(context, clazz);
    }
}
