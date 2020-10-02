package com.yuan.common.enums;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

/**
 * 用户操作类型枚举
 */
@Getter
@ApiModel("用户操作类型枚举")
public enum OptTypeEnum {

    REGISTERED(1, "注册"),
    POSTED(2, "发帖"),
    COMMENT(3, "评论"),
    TWEET(4, "发推");


    private final int type;
    private final String desc;

    OptTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
