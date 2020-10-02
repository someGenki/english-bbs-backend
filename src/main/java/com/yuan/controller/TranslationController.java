package com.yuan.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuan.common.annotation.LogAnnotation;
import com.yuan.common.baidu.TransApi;
import com.yuan.common.lang.Result;
import com.yuan.entity.Translation;
import com.yuan.service.TranslationService;
import com.yuan.util.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 翻译表 前端控制器
 * </p>
 *
 * @author yuan
 * @since 2020-07-17
 */
@Api("翻译相关")
@RestController
@RequestMapping("/tran")
public class TranslationController {

    @Autowired
    TransApi transApi;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    TranslationService translationService;

    @ApiOperation("对接的百度通用翻译api")
    @GetMapping("/api/{word}")
    public String getTrans(@PathVariable String word) throws Exception {
        return transApi.getTransResult(word);
    }

    @RequiresAuthentication
    @LogAnnotation("提交/编辑了一篇翻译")
    @ApiOperation("提交/编辑一篇翻译")
    @PostMapping("/commit")
    public Result commit(@Validated @RequestBody Translation tran, HttpServletRequest request) {
        Integer uid = jwtUtils.getUidByRequest(request);
        translationService.commitOrEdit(tran, uid);
        return Result.succ();
    }


    @ApiOperation("分页获取某文章下的翻译")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "num", value = "页码", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "每页大小<10", defaultValue = "5"),
            @ApiImplicitParam(name = "aid", value = "文章id", required = true),
    })
    @GetMapping("/page")
    public Result queryList(Integer num, Integer size, Integer aid) {
        if (size == null || size > 10 || size < 1) size = 5;
        IPage<Translation> p = translationService.selectPage(num, size, aid);
        return Result.succ(p);
    }

}
