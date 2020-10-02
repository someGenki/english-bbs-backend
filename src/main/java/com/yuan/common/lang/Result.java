package com.yuan.common.lang;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 3、统一结果封装
 * 跟前端交互的数据格式
 */
@Data
@ApiModel("结果封装")
public class Result implements Serializable {
    @ApiModelProperty("状态码:200成功")
    private Integer code;
    private String msg;
    private Object data;

    /**
     * 操作成功
     */
    public static Result succ(int code, String msg, Object data) {
        Result m = new Result();
        m.setCode(code);
        m.setData(data);
        m.setMsg(msg);
        return m;
    }

    public static Result succ(String msg, Object data) {
        return succ(200, msg, data);
    }

    public static Result succ(Object data) {
        return succ("操作成功", data);
    }

    public static Result succ() {
        return succ("操作成功", null);
    }

    /**
     * 失败,消息自定义
     *
     * @param msg  自定义的消息
     * @param data 数据
     * @return 对象
     */
    public static Result fail(int code, String msg, Object data) {
        Result m = new Result();
        m.setCode(code);
        m.setData(data);
        m.setMsg(msg);
        return m;
    }

    /**
     * 失败,消息自定义
     *
     * @param msg  自定义的消息
     * @param data 数据
     * @return 对象
     */
    public static Result fail(String msg, Object data) {
        return fail(201, msg, data);
    }

    /**
     * 失败
     *
     * @param msg 自定义的消息
     * @return 对象
     */
    public static Result fail(String msg) {

        return fail(201, msg, null);
    }

    /**
     * 操作失败
     *
     * @return 返回操作失败
     */
    public static Result fail() {
        return fail(201, "操作失败", null);
    }


}