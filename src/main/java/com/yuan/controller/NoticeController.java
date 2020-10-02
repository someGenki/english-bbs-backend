package com.yuan.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yuan.common.lang.Result;
import com.yuan.entity.Notice;
import com.yuan.service.NoticeService;
import com.yuan.util.JwtUtils;
import com.yuan.util.ShiroUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 通知表 前端控制器
 * </p>
 *
 * @author yuan
 * @since 2020-07-19
 */
@Api("消息通知")
@RestController
public class NoticeController {
    @Autowired
    NoticeService noticeService;
    @Autowired
    JwtUtils jwtUtils;


    @RequiresAuthentication
    @ApiOperation("登录状态网站刷新时获取用户未读通知")
    @GetMapping("/notice")
    public Result getUserNotice() {
        Integer uid = ShiroUtil.getUidFromProfile();
        List<Notice> list = noticeService.list(new QueryWrapper<Notice>().eq("status", "0").eq("receiver", uid));
        return Result.succ(list);
    }

    /**
     * 将用户通知标记为已读
     *
     * @param id 通知的主键,为0或null则将用户所有通知都已读
     */
    @RequiresAuthentication
    @ApiOperation("已读通知,如果id==0则已读该用户所有未读通知")
    @ApiImplicitParam(name = "id", value = "通知消息的主键")
    @PutMapping("/notice/{id}")
    public Result read(@PathVariable Integer id) {
        Integer uid = ShiroUtil.getUidFromProfile();
        noticeService.read(id, uid);
        return Result.succ();
    }

}
