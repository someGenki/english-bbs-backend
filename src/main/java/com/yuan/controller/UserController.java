package com.yuan.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yuan.common.annotation.LogAnnotation;
import com.yuan.common.dto.LoginDto;
import com.yuan.common.dto.RegisterDto;
import com.yuan.common.dto.UserDto;
import com.yuan.common.lang.Result;
import com.yuan.entity.User;
import com.yuan.service.UserService;
import com.yuan.util.JwtUtils;
import com.yuan.util.ShiroUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 * TODO  已经登录用户刷新首页时 会获取用户最新的头像,昵称等存在Vuex
 *
 * @author yuan
 * @since 2020-07-08
 */
@Api("用户功能")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    UserService userService;


    @ApiOperation("修改用户昵称")
    @RequiresAuthentication
    @PostMapping("/update/nickname/{val}")
    public Result updateNickname(@PathVariable String val) {
        userService.updateNickname(val);
        return Result.succ();
    }

    @ApiOperation("获取当前用户id")
    @GetMapping("/myId")
    @RequiresAuthentication
    public Result getName(HttpServletRequest request) {

        System.out.println(ShiroUtil.getProfile());
        return Result.succ(jwtUtils.getUidByRequest(request));
    }

    /**
     * 用户F5这种刷新或者首次加载页面 或获取最新的昵称和头像
     * 默认从token中获取,也可以指定uid
     * 待加入:积分
     */
    @GetMapping("/newest")
    @ApiOperation("获取用户的最新资料,默认从token中获取,也可以指定uid")
    @ApiImplicitParam(name = "uid", value = "需要的用户id", required = false)
    public Result getNewestInfo(Integer uid) {
        uid = uid != null ? uid : ShiroUtil.getUidFromProfile();
        UserDto dto = userService.getNewestInfo(uid);
        return Result.succ(dto);
    }

    @ApiOperation("用户登出")
    @RequiresAuthentication
    @GetMapping("/logout")
    public Result logout() {
        // 存在问题:登出后token依然可用
        // 目前想到的解决方法:将过期的token保存起来,下次登录判断下
        SecurityUtils.getSubject().logout();
        return Result.succ("登出成功");
    }


    @ApiOperation("判断用户/邮箱是否已经存在")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "类型:email/username", required = true),
            @ApiImplicitParam(name = "value", value = "值:要判断的数据", required = true)
    })
    @GetMapping("/exist")
    public Result isExist(String type, String value) {
        if ("email".equalsIgnoreCase(type)) {
            //  待改成数据库判断,暂时不用email,需要的话mysql得email字段要设置unique
            if ("1159140148@qq.com".equalsIgnoreCase(value))
                return Result.fail("邮箱重复");
            return Result.succ("邮箱可以使用", null);
        } else {
            int c = userService.count(new QueryWrapper<User>().eq("username", value));
            return c > 0 ? Result.fail("用户名重复") : Result.succ("用户名可用", null);
        }
    }

    @LogAnnotation("用户登录")
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result login(@Validated @RequestBody LoginDto dto) {
        UserDto login = userService.login(dto);
        return Result.succ(login);
    }

    @ApiOperation("用户注册功能")
    @PostMapping("/reg")
    public Result register(@Validated @RequestBody RegisterDto dto) {
        UserDto register = userService.register(dto);
        return Result.succ(register);
    }
}
