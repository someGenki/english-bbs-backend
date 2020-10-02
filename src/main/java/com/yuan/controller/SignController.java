package com.yuan.controller;


import com.yuan.common.dto.SignDto;
import com.yuan.common.dto.SitesInfoDto;
import com.yuan.common.lang.Result;
import com.yuan.service.SignService;
import com.yuan.util.ShiroUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * <p>
 * 用户每月签到表 前端控制器
 * </p>
 *
 * @author yuan
 * @since 2020-07-26
 */
@RestController
@Api("签到功能")
@RequestMapping("/sign")
public class SignController {

    @Autowired
    SignService signService;


    @RequiresAuthentication
    @ApiOperation("用户签到")
    @PostMapping("/in")
    public Result userSign() {
        Integer uid = ShiroUtil.getUidFromProfile();
        SignDto dto = signService.sign(uid);
        return Result.succ(dto);
    }

    @ApiOperation("检查用户今日是否签到")
    @GetMapping("/check/{uid}")
    public Result checkSign(@PathVariable Integer uid) {
        return Result.succ(signService.checkSign(uid, LocalDate.now()));
    }

    // 网站签到信息 包括但不限于当前签到排行榜(前十),签到次数排行榜(前十),今天签到人数,当月签到人数
    @ApiOperation("网站签到信息")
    @GetMapping("/info")
    public Result sitesSignInfo() {
        SitesInfoDto sitesSignInfo = signService.getSitesSignInfo(LocalDate.now());
        signService.redisDataToDB(LocalDate.now());
        return Result.succ(sitesSignInfo);
    }
}
