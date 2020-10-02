package com.yuan.controller;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuan.common.annotation.LogAnnotation;
import com.yuan.common.dto.ArticleDto;
import com.yuan.common.enums.ItemStatusEnum;
import com.yuan.common.lang.Result;
import com.yuan.entity.Article;
import com.yuan.service.ArticleService;
import com.yuan.util.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 文章表 前端控制器
 * </p>
 *
 * @author yuan
 * @since 2020-07-16
 */
@Api("文章相关功能")
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    ArticleService articleService;
    @Autowired
    JwtUtils jwtUtils;

    @ApiOperation("获取一篇文章详细内容")
    @GetMapping("/{aid}")
    public Result getOne(@PathVariable Integer aid) {
        Article byId = articleService.getById(aid);
        Assert.isTrue(byId.getStatus() == ItemStatusEnum.NORMAL.getStatus(), "文章已被删除或者被隐藏");
        articleService.addPv(aid);// 简易的增加文章访问量
        return Result.succ(byId);
    }

    /**
     * 分页查询,考虑不返回content字段,不然数据量太大,
     * 还要考虑status,size大小
     * 还有关键词查询 还没搞
     * 还有根据用户id查询
     */
    @ApiOperation("文章的分页查询")
    @GetMapping("/page")
    public Result queryList(Integer num, Integer size, String search) {
        IPage<Article> articleIPage = articleService.selectPage(num, size, search);
        return Result.succ(articleIPage);
    }


    @RequiresAuthentication
    @LogAnnotation("发布/编辑了一篇文章")
    @ApiOperation("发布/编辑一篇文章")
    @PostMapping("/commit")
    public Result publish(@Validated @RequestBody ArticleDto articleDto, HttpServletRequest request) {
        Integer uid = jwtUtils.getUidByRequest(request);
        articleService.edit(articleDto, uid);
        return Result.succ();
    }

    @RequiresAuthentication
    @ApiOperation("修改文章状态")
    @LogAnnotation("修改了文章状态")
    @PutMapping("/{aid}")
    public Result setArticleStatus(@PathVariable Integer aid, Integer val, HttpServletRequest request) {
        Article article = articleService.getById(aid);
        Integer uid = jwtUtils.getUidByRequest(request);
        Assert.isTrue(article.getUid().equals(uid) && (val > 0 && val < 4), "无操作权限或数值错误");
        articleService.update(new UpdateWrapper<Article>().set("status", val).eq("id", aid));
        return Result.succ();
    }

}
