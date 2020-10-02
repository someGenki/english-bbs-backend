package com.yuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.annotation.OptAnnotation;
import com.yuan.common.dto.PostDto;
import com.yuan.common.enums.ItemStatusEnum;
import com.yuan.common.enums.ItemTypeEnum;
import com.yuan.common.enums.OptTypeEnum;
import com.yuan.entity.Post;
import com.yuan.mapper.PostMapper;
import com.yuan.service.LikeService;
import com.yuan.service.PostService;
import com.yuan.util.SensitiveFilterUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * <p>
 * 帖子 服务实现类
 * </p>
 *
 * @author yuan
 * @since 2020-07-28
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Resource
    PostMapper mapper;
    @Autowired
    LikeService likeService;
    @Autowired
    SensitiveFilterUtil sensitiveFilterUtil;

    @Override
    public Page<Post> getPostList(int num, int size, String category) {

        Page<Post> postAndPage = (StringUtils.hasText(category) && !category.equalsIgnoreCase("default"))
                ? mapper.getPostAndPageWithCategory(new Page<>(num, size), category)
                : mapper.getPostAndPage(new Page<>(num, size));
        postAndPage.getRecords().forEach(post -> {
            // getLikes 多一个资源就要多弄一张点赞关联表以及一个落库任务等等
            post.setLikes(post.getLikes() + likeService.getLikeCountByType(ItemTypeEnum.POST.getType(), post.getId()));
        });
        return postAndPage;
    }

    @Override
    public void addPv(Integer pid) {
        this.update(new UpdateWrapper<Post>().eq("id", pid).setSql("pv=pv+1"));
    }

    @Override
    public Post getPostByIDAndAddPv(Integer pid) {
        Post post = this.getById(pid);
        Assert.isTrue(post.getStatus() == ItemStatusEnum.NORMAL.getStatus(), "资源状态异常");
        post.setLikes(post.getLikes() + likeService.getLikeCountByType(ItemTypeEnum.POST.getType(), pid));
        this.addPv(pid);
        return post;
    }

    @OptAnnotation(OptTypeEnum.POSTED)
    @Override
    public Integer edit(PostDto postDto, Integer uid) {
        Integer pid = postDto.getId();
        Post post;
        if (pid == null || pid < 1) {
            post = new Post();
            post.setUid(uid);
        } else {
            post = this.getById(pid);
            Assert.isTrue(post.getUid().equals(uid), "无操作权限");
            post.setGmtModified(LocalDateTime.now());
        }
        BeanUtils.copyProperties(postDto, post);
        post.setContent(sensitiveFilterUtil.filter(post.getContent()));
        this.saveOrUpdate(post);
        return this.getOne(new UpdateWrapper<Post>().eq("uid", uid).eq("title", post.getTitle()).orderByDesc("gmt_modified")).getId();
    }

    @Override
    public void updateLikes(int id, int likes) {
        this.mapper.updateLike(id, likes);
    }
}
