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
 * 用户表
 * </p>
 *
 * @author yuan
 * @since 2020-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("y_user")
@ApiModel(value = "User对象", description = "用户表")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "唯一标识", hidden = true)
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户名,不能重复")
    private String username;

    @ApiModelProperty(value = "用户昵称,初始值为用户名")
    private String nickname;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "用户状态:1启动 | 0冻结")
    private Integer status;

    @ApiModelProperty(value = "用户状态积分")
    private Integer point;

    @ApiModelProperty(value = "头像url")
    private String avatar;

    @ApiModelProperty(value = "邮箱,不能重复")
    private String email;

    @ApiModelProperty(value = "密码MD5盐")
    private String salt;

    @ApiModelProperty(value = "可用于用户分类,例如简单的权限管理,也可能是无用字段")
    private Integer type;

    @ApiModelProperty(hidden = true)
    private LocalDateTime gmtCreate;

    @ApiModelProperty(hidden = true)
    private LocalDateTime gmtModified;


}
