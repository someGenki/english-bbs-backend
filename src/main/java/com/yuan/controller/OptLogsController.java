package com.yuan.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yuan.common.enums.OptTypeEnum;
import com.yuan.common.lang.Result;
import com.yuan.entity.OptLogs;
import com.yuan.service.OptLogsService;
import com.yuan.util.ShiroUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户操作记录表（动态） 前端控制器
 * </p>
 *
 * @author yuan
 * @since 2020-09-24
 */
@RestController
public class OptLogsController {

    @Autowired
    OptLogsService optLogsService;

    @PostMapping("/tweet")
    @ApiOperation("发一个推文/动态")
    @RequiresAuthentication
    public Result sendTweet(@RequestParam String content) {
        optLogsService.createOptLogs(ShiroUtil.getUidFromProfile(), OptTypeEnum.TWEET.getType(), null, content);
        return Result.succ();
    }


    @ApiOperation("获取个人动态")
    @GetMapping("/dynamic/{uid}")
    public Result getDynamic(@PathVariable Integer uid) {
        List<OptLogs> optLogs = optLogsService.getOptLogs(uid);
        return Result.succ(optLogs);
    }

    @RequiresAuthentication
    @ApiOperation("删除一条动态")
    @DeleteMapping("/dynamic/{id}")
    public Result deleteDynamic(@PathVariable Integer id) {
        Integer uid = ShiroUtil.getUidFromProfile();
        boolean remove = optLogsService.remove(new QueryWrapper<OptLogs>().eq("id", id).eq("uid", uid));
        return Result.succ(remove);
    }
}
