package com.yuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.jiekou.ItemLike;
import com.yuan.entity.TranslationLike;

/**
 * <p>
 * 文章点赞表 服务类
 * </p>
 *
 * @author yuan
 * @since 2020-07-18
 */
public interface TranslationLikeService extends IService<TranslationLike>, ItemLike {

    // 保存点赞状态如果已经有了就更新他
    void saveOrUpdateStatus(Integer id, Integer uid, Integer status);

    Integer[] getLikes(Integer uid);
}
