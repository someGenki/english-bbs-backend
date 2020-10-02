package com.yuan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 文章点赞表
 * </p>
 *
 * @author yuan
 * @since 2020-07-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("y_translation_like")
@ApiModel(value = "TranslationLike对象", description = "文章点赞表")
public class TranslationLike implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "被点赞翻译的id")
    private Integer tid;

    @ApiModelProperty(value = "点赞人的id")
    private Integer uid;

    @ApiModelProperty(value = "点赞状态0取消|1点赞")
    private Integer status;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;


}
