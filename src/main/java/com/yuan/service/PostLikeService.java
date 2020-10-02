package com.yuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.jiekou.ItemLike;
import com.yuan.entity.PostLike;

/**
 * <p>
 * 帖子点赞表 服务类
 * </p>
 *
 * @author yuan
 * @since 2020-07-28
 */
public interface PostLikeService extends IService<PostLike>, ItemLike {

    void saveOrUpdateStatus(Integer id, Integer uid, Integer status);

    Integer[] getLikes(Integer uid);
}
