package com.yuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.jiekou.ItemLike;
import com.yuan.entity.CommentLike;

/**
 * <p>
 * 文章点赞表 服务类
 * </p>
 *
 * @author yuan
 * @since 2020-07-21
 */
public interface CommentLikeService extends IService<CommentLike>, ItemLike {

    void saveOrUpdateStatus(Integer id, Integer uid, Integer status);

    Integer[] getLikes(Integer uid);
}
