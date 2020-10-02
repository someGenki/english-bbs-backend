package com.yuan.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuan.common.lang.Result;
import com.yuan.entity.Message;
import com.yuan.service.MessageService;
import com.yuan.util.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 留言表 前端控制器
 * </p>
 * 发布 查看 删除(为实现)
 *
 * @author yuan
 * @since 2020-07-20
 */
@Api("留言功能")
@RestController
public class MessageController {

    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    MessageService messageService;


    @ApiOperation("发表一条留言")
    @PostMapping("/message")
    public Result saveMessage(HttpServletRequest request, @Validated @RequestBody Message message) {
        messageService.save(message, request);
        return Result.succ();
    }


    @ApiOperation("分页获取留言")
    @GetMapping("/message")
    public Result getMessages(Integer num, Integer size) {
        IPage<Message> messageIPage = messageService.selectPageParent(num, size);
        return Result.succ(messageIPage);
    }

    @ApiOperation("分页获取子留言")
    @GetMapping("/message/{pid}")
    public Result getMessageChildren(@PathVariable Integer pid, Integer num, Integer size) {
        return Result.succ(messageService.selectPageChildren(pid, num, size));
    }

}
