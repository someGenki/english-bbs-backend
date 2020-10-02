package com.yuan.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yuan.common.lang.Result;
import com.yuan.entity.Collection;
import com.yuan.service.CollectionService;
import com.yuan.util.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 收藏表 前端控制器
 * </p>
 *
 * @author yuan
 * @since 2020-07-19
 */
@Api("用户收藏功能")
@RestController
@RequestMapping("/collection")
public class CollectionController {

    @Autowired
    CollectionService collectionService;
    @Autowired
    JwtUtils jwtUtils;

    @RequiresAuthentication
    @ApiOperation("分页获取用户的收藏list")
    @ApiImplicitParam(name = "type", value = "收藏的类型:1-翻译,3-文章,4-帖子", required = true)
    @GetMapping("/{type}/{num}/{size}")
    public Result collectionList(HttpServletRequest request, @PathVariable Integer type, @PathVariable Integer num, @PathVariable Integer size) {
        Integer uid = jwtUtils.getUidByRequest(request);

        return Result.succ(collectionService.selectPage(uid, num, size, type));
    }


    /**
     * 收藏一个资源,若重复收藏则无效果
     * 或者可以删除以后记录,再新建一条
     * 这样每次最新收藏的可以再前面,不用排序
     */
    @RequiresAuthentication
    @ApiOperation("收藏一个资源")
    @PostMapping("/{type}/{id}")
    public Result collect(HttpServletRequest request, @PathVariable Integer type, @PathVariable Integer id) {
        Integer uid = jwtUtils.getUidByRequest(request);
        Assert.isTrue(!isCollect(type, id, uid), "已经收藏过了");
        Collection c = new Collection();
        c.setUid(uid).setItemId(id).setItemType(type);
        collectionService.save(c);
        return Result.succ();
    }


    @RequiresAuthentication
    @ApiOperation("取消收藏一个资源")
    @DeleteMapping("/{type}/{id}")
    public Result unCollect(HttpServletRequest request, @PathVariable Integer type, @PathVariable Integer id) {
        Integer uid = jwtUtils.getUidByRequest(request);
        Assert.isTrue(isCollect(type, id, uid), "还未收藏过");
        collectionService.remove(new QueryWrapper<Collection>()
                .eq("item_type", type)
                .eq("item_id", id)
                .eq("uid", uid));
        return Result.succ();
    }

    /**
     * 通过3个条件确定一条记录,判断是否有收藏记录 返回 (记录数>0)
     *
     * @return true 已收藏
     */
    public boolean isCollect(Integer type, Integer id, Integer uid) {

        return collectionService.count(new QueryWrapper<Collection>()
                .eq("uid", uid)
                .eq("item_id", id)
                .eq("item_type", type)) > 0;
    }
}



