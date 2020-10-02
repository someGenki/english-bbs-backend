package com.yuan.common.enums;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

/**
 * 资源类型枚举
 */
@Getter
@ApiModel("资源类型枚举")
public enum ItemTypeEnum {
    TRANSLATION(1, "翻译"),
    COMMENT(2, "评论"),
    ARTICLE(3, "文章"),
    POST(4, "帖子");
    private final int type;
    private final String desc;

    ItemTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
