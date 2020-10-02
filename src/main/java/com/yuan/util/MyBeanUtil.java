package com.yuan.util;


import cn.hutool.extra.spring.SpringUtil;
import com.yuan.common.enums.ItemTypeEnum;
import com.yuan.common.jiekou.ItemCanLike;
import com.yuan.common.jiekou.ItemLike;
import com.yuan.service.impl.*;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 获取bean的工具类 进一步的封装
 */
@Data
@Component
public class MyBeanUtil {
    /**
     * 使用hutool工具类动态获取bean
     * 直接new xxxImpl会有空指针异常
     *
     * @param type
     * @return
     */
    public ItemCanLike getItemCanLikeByType(ItemTypeEnum type) {
        ItemCanLike i = null;
        switch (type) {
            case POST:
                i = SpringUtil.getBean(PostServiceImpl.class);
                break;
            case COMMENT:
                i = SpringUtil.getBean(CommentServiceImpl.class);
                break;
            case TRANSLATION:
                i = SpringUtil.getBean(TranslationServiceImpl.class);
                break;
            default:
                throw new IllegalStateException(type.toString());
        }
        return i;
    }
    /*

     */

    /**
     * 获取一个bean jdk 14的写法
     *
     * @param type
     * @return
     *//*

    public ItemLike getItemLikeByType(ItemTypeEnum type) {
        ItemLike i = switch (type) {
            case POST -> SpringUtil.getBean(PostLikeServiceImpl.class);
            case COMMENT -> SpringUtil.getBean(CommentLikeServiceImpl.class);
            case TRANSLATION -> SpringUtil.getBean(TranslationLikeServiceImpl.class);
            default -> throw new IllegalStateException(type.toString());
        };
        return i;
    }
*/

    // jdk14之前switch写法
    public ItemLike getItemLikeByType(ItemTypeEnum type) {
        ItemLike i = null;
        switch (type) {
            case POST:
                i = SpringUtil.getBean(PostLikeServiceImpl.class);
                break;
            case COMMENT:
                i = SpringUtil.getBean(CommentLikeServiceImpl.class);
                break;
            case TRANSLATION:
                i = SpringUtil.getBean(TranslationLikeServiceImpl.class);
                break;
            default:
                throw new IllegalStateException(type.toString());
        }
        return i;
    }

}
