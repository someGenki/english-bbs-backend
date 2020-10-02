package com.yuan.controller;

import com.yuan.common.annotation.LogAnnotation;
import com.yuan.common.lang.Result;
import com.yuan.service.LikeService;
import com.yuan.util.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 点赞功能,还没用完成定时落库任务
 * 可以用action来合并这些接口
 */
@Api("点赞功能")
@RestController
public class LikeController {

    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    LikeService likeService;

    /**
     * @param type 资源类型 1是翻译 2是评论 4是帖子
     * @param id   资源的主键
     * @return 11
     */
    @RequiresAuthentication
    @LogAnnotation("点赞了翻译或者评论")
    @ApiOperation("点赞翻译或者评论")
    @GetMapping("/like/{type}/{id}")
    public Result like(HttpServletRequest request, @PathVariable Integer type, @PathVariable Integer id) {
        Integer uid = jwtUtils.getUidByRequest(request);
        // 断言点过赞是true !取反为false,丢出异常
        Assert.isTrue(!isLike(type, id, uid), "已经点过赞了");
        likeService.like(type, id, uid);
        return Result.succ();
    }

    @RequiresAuthentication
    @ApiOperation("取消点赞翻译或者评论")
    @GetMapping("/unlike/{type}/{id}")
    public Result unlike(HttpServletRequest request, @PathVariable Integer type, @PathVariable Integer id) {
        Integer uid = jwtUtils.getUidByRequest(request);
        Assert.isTrue(isLike(type, id, uid), "还没点过赞噢");
        likeService.unlike(type, id, uid);
        return Result.succ();
    }

    @ApiOperation("讨厌/踩一个可点赞的资源")
    @GetMapping("/hate/{type}/{id}")
    public Result hate(HttpServletRequest request, @PathVariable Integer type, @PathVariable Integer id) {
        Integer uid = jwtUtils.getUidByRequest(request);
        boolean like = isLike(type, id, uid);
        likeService.hate(type, id, uid, like);
        return Result.succ();
    }

    @ApiOperation("获取用户点赞的过记录")
    @GetMapping("/likes/{type}/{uid}")
    public Result getLikeList(@PathVariable Integer uid, @PathVariable Integer type) {
        // 或者用户加载是带着资源们的id来请求 而不是全部请求
        // Collection<T> listByIds(Collection<? extends Serializable> idList);
        return Result.succ(likeService.getUserLikes(uid, type));
    }

    /**
     * 点赞/取消点赞的逻辑校验
     *
     * @return true:点过赞了,false 没点过
     */
    public boolean isLike(Integer type, Integer id, Integer uid) {
        return likeService.getLikeStatus(type, id, uid);
    }
}
