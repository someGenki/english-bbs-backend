package com.yuan.controller;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yuan.common.annotation.LogAnnotation;
import com.yuan.common.dto.UserDigestDto;
import com.yuan.common.lang.Result;
import com.yuan.entity.Userinfo;
import com.yuan.service.UserinfoService;
import com.yuan.util.ShiroUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author yuan
 * @since 2020-07-16
 */
@Api("用户信息")
@RequestMapping("/userinfo")
@RestController
public class UserInfoController {

    @Autowired
    UserinfoService userinfoService;

    @ApiOperation("获取用户信息")
    @ApiImplicitParam(name = "uid", value = "用户id", required = true)
    @GetMapping("/{uid}")
    public Result getUserinfo(@PathVariable Integer uid) {
        Userinfo userinfo = userinfoService.getUserinfo(uid);
        Assert.notNull(userinfo, "用户信息不存在或者设置不可见");
        return Result.succ(userinfo);
    }


    @RequiresAuthentication
    @ApiOperation("修改用户的某个字段信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "要修改的字段: " +
                    "sex(1,2,3),birthday(date),qqnum,cover_img,school,Profession"),
            @ApiImplicitParam(name = "value", value = "修改后的值"),
    })
    @LogAnnotation("修改用户信息")
    @PutMapping("/update")
    public Result updateUserinfo(String key, String value) {
        Integer uid = ShiroUtil.getUidFromProfile();
        // 逻辑判断:断言key不是关键字段
        Assert.doesNotContain("id|uid|gmt_create|gmt_modified", key, "无法更新该字段");
        userinfoService.update(new UpdateWrapper<Userinfo>().set(key, value).eq("uid", uid));
        return Result.succ("更新成功", null);
    }

    @ApiOperation("获取用户的信息摘要：昵称头像，签名，性别，封面图")
    @GetMapping("/digest/{uid}")
    public Result getUserinfoDigest(@PathVariable Integer uid) {
        UserDigestDto userDigest = userinfoService.getUserDigest(uid);

        return Result.succ(userDigest);
    }
}
