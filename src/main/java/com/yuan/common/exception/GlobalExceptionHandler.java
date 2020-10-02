package com.yuan.common.exception;


import com.yuan.common.lang.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;


/**
 * 通过使用@ControllerAdvice来进行统一异常处理
 * ,@ExceptionHandler(value = RuntimeException.class)来指定捕获的Exception各个类型异常 ，
 * 这个异常的处理，是全局的，所有类似的异常，都会跑到这个地方处理。
 * <p>
 * 定义全局异常处理，@ControllerAdvice表示定义全局控制器异常处理
 * ，@ExceptionHandler表示针对性异常处理，可对每种异常针对性处理。
 * <p>
 * 五 全局异常处理
 * ShiroException：shiro抛出的异常，比如没有权限，用户登录异常
 * IllegalArgumentException：处理Assert的异常
 * MethodArgumentNotValidException：处理实体校验的异常
 * RuntimeException：捕捉其他异常
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 401
     *
     * @param e 捕捉shiro的异常
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ShiroException.class)
    public Result handle401(ShiroException e) {
        log.error("Shiro异常:{}", e.getMessage());
        return Result.fail(401, e.getMessage(), null);
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SQLException.class)
    public Result handleSqlException(SQLException e) {
        log.error("sql错误异常:{}", e.getMessage());
        return Result.fail(500, "sql异常", null);
    }

    /**
     * token异常 401
     *
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ExpiredCredentialsException.class)
    public Result handler(ExpiredCredentialsException e) {
        log.error("token异常:{}", e.getMessage());
        return Result.fail(404, e.getMessage(), null);
    }

    /**
     * 处理Assert的异常 200 然后 根据里面的msg进一步处理
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Result handler(IllegalArgumentException e) {
        log.error("Assert异常:{}", e.getMessage());
        return Result.fail(e.getMessage());
    }


    /**
     * 400
     *
     * @param e 校验错误异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result handler(MethodArgumentNotValidException e) {
        log.error("校验错误异常:{}", e.getMessage());
        BindingResult bindingResult = e.getBindingResult();
        ObjectError objectError = bindingResult.getAllErrors().stream().findFirst().get();
        return Result.fail(objectError.getDefaultMessage());
    }

    /**
     * 500
     *
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = RuntimeException.class)
    public Result handler(RuntimeException e) {
        e.printStackTrace();
        log.error("运行时异常:{}", e.getMessage());
        return Result.fail(e.getMessage());
    }
}