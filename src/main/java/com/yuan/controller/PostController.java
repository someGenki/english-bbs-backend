package com.yuan.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuan.common.annotation.LogAnnotation;
import com.yuan.common.dto.PostDto;
import com.yuan.common.lang.Result;
import com.yuan.entity.Post;
import com.yuan.service.PostService;
import com.yuan.util.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 帖子 前端控制器
 * </p>
 *
 * @author yuan
 * @since 2020-07-28
 */
@RestController
@RequestMapping("/post")
@Api("帖子功能")
public class PostController {
    @Autowired
    PostService postService;
    @Autowired
    JwtUtils jwtUtils;

    @ApiOperation("分页获取帖子")
    @ApiImplicitParam(name = "category", value = "帖子的分类:不填为默认." +
            "其他比如提问(question),分享(share),建议(advice),公告(notice),讨论(discuss )")
    @GetMapping("/page")
    public Result getPostList(Integer num, Integer size, String category) {
        Page<Post> postPage = postService.getPostList(num, size, category);
        return Result.succ(postPage);
    }

    @RequiresAuthentication
    @LogAnnotation("发布/编辑了一篇帖子")
    @ApiOperation("发布/编辑一篇帖子")
    @PostMapping("/commit")
    public Result publish(@Validated @RequestBody PostDto postDto, HttpServletRequest request) {
        Integer uid = jwtUtils.getUidByRequest(request);
        postService.edit(postDto, uid);
        return Result.succ();
    }

    @ApiOperation("获取一篇帖子详细内容")
    @GetMapping("/{pid}")
    public Result getOne(@PathVariable Integer pid) {
        Post post = postService.getPostByIDAndAddPv(pid);
        return Result.succ(post);
    }
}