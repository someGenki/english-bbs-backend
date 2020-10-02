package com.yuan.common.enums;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

/**
 * 通知类型枚举
 */
@Getter
@ApiModel("通知类型枚举")
public enum NoticeTypeEnum {
    SYSTEM(1, "系统"),
    LIKE(2, "点赞"),
    COLLECT(3, "收藏"),
    COMMENT(4, "评论");
    private final int type;
    private final String desc;

    NoticeTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

}
