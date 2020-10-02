package com.yuan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户操作记录表（动态）
 * </p>
 *
 * @author yuan
 * @since 2020-09-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("y_opt_logs")
@ApiModel(value = "OptLogs对象", description = "用户操作记录表（动态）")
public class OptLogs implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "操作人的id")
    private Integer uid;

    @ApiModelProperty(value = "操作的类型：例如提问，回帖等等")
    private Integer optType;

    @ApiModelProperty(value = "操作对应对象的id")
    private Integer optId;

    @ApiModelProperty(value = "这两个字段用来表示具有一些层次关系的动态，例如回帖：parent_type 和 parent_id 就会填充上回帖对应帖子的编号和类型")
    private Integer parentType;

    private Integer parentId;

    @ApiModelProperty(value = "用户可以自己发个动态（好像叫动弹），那么就有个内容")
    private String content;

    private LocalDateTime gmtCreate;


    // 当取出动态后，一些内容比如帖子可能需要帖子的预览
    @TableField(exist = false)
    private Object detail;

}
