package com.yuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.entity.CommentLike;
import com.yuan.mapper.CommentLikeMapper;
import com.yuan.service.CommentLikeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 文章点赞表 服务实现类
 * </p>
 *
 * @author yuan
 * @since 2020-07-21
 */
@Service
public class CommentLikeServiceImpl extends ServiceImpl<CommentLikeMapper, CommentLike> implements CommentLikeService {
    @Resource
    CommentLikeMapper mapper;

    @Override
    public void saveOrUpdateStatus(Integer id, Integer uid, Integer status) {
        mapper.insertOrUpdateStatus(id, uid, status);
    }

    @Override
    public Integer[] getLikes(Integer uid) {
        return mapper.getUserLikes(uid);
    }
}
