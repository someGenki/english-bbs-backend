package com.yuan.common.exception;

import lombok.Data;

/**
 * 自定义异常 暂时没想好
 */
@Data
public class CustomException extends RuntimeException {
    private int code;
    private String message;

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public CustomException(int code, String message) {
        this.code = code;
        this.message = message;
    }


}
