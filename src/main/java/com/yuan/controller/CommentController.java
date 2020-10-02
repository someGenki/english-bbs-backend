package com.yuan.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuan.common.annotation.LogAnnotation;
import com.yuan.common.lang.Result;
import com.yuan.entity.Comment;
import com.yuan.service.CommentService;
import com.yuan.util.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 评论表 前端控制器
 * </p>
 * Comment实体多了个2个新字段 字回复列表和评论者头像.用于展示
 * 待优化: 对评论查询加个redis缓存要是有新的评论，就把这个资源缓存的评论删除，下次请求时重新读数据库并将最新的数据缓存到 Redis 中。
 * 参考:https://juejin.im/post/5beea202e51d451f5b54cdc4
 *
 * @author yuan
 * @since 2020-07-19
 */
@Api("评论功能")
@RequestMapping("/comment")
@RestController
public class CommentController {

    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    CommentService commentService;

    /**
     * 需要一个dto
     * sql: select * from y_comment where from_id=3 and xxx order by id desc limit 1;
     */
    @CacheEvict(cacheNames = "commentCache", allEntries = true)
    @RequiresAuthentication
    @LogAnnotation("发表评论")
    @ApiOperation("发表评论")
    @PostMapping("/commit")
    public Result saveComment(HttpServletRequest request, @Validated @RequestBody Comment comment) {
        Integer uid = jwtUtils.getUidByRequest(request);
        return Result.succ(commentService.saveComment(uid, comment));//更新后返回刚刚评论的完整信息
    }

    @CacheEvict(cacheNames = "commentCache", allEntries = true)
    @RequiresAuthentication
    @LogAnnotation("删除了自己的评论")
    @ApiOperation("删除自己的评论,要评论的主键")
    @DeleteMapping("/{id}")
    public Result deleteComment(HttpServletRequest request, @PathVariable Integer id) {
        Integer uid = jwtUtils.getUidByRequest(request);
        commentService.deleteComment(id, uid);
        return Result.succ();
    }


    /**
     * 分页查询
     * 可参考连接:https://github.com/tianqi-bucuo/Community/blob/master/src/main/java/com/cky/community/service/impl/CommentServiceImpl.java
     */
    @Cacheable(cacheNames = "commentCache")
    @ApiOperation("分页获取评论")
    @GetMapping("/page/{type}/{itemId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "资源类型", required = true),
            @ApiImplicitParam(name = "itemId", value = "资源id", required = true),
            @ApiImplicitParam(name = "num", value = "页码", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "每页大小<10", defaultValue = "5")
    })
    public Result getComments(@PathVariable Integer type, @PathVariable Integer itemId, Integer num, Integer size) {
        if (size == null || size > 10 || size < 1) size = 5;
        IPage<Comment> commentIPage = commentService.selectPageParent(type, itemId, num, size);
        return Result.succ(commentIPage);
    }

    @ApiOperation("分页获取子评论")
    @Cacheable(cacheNames = "commentCache")
    @GetMapping("/page/{pid}")
    public Result getCommentChildren(@PathVariable Integer pid, Integer num, Integer size) {
        return Result.succ(commentService.selectPageChildren(pid, num, size));
    }

}
