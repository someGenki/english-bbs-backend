package com.yuan.controller;


import com.yuan.common.annotation.LogAnnotation;
import com.yuan.common.lang.Result;
import com.yuan.entity.UserFile;
import com.yuan.service.UserFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户上传文件记录表 前端控制器
 * </p>
 * 大小限制10MB
 * 还打算弄个头像上传
 *
 * @author yuan
 * @since 2020-07-24
 */
@Api("文件操作相关")
@RestController
@RequestMapping("/upload")
public class UserFileController {

    @Autowired
    UserFileService userFileService;


    @RequiresAuthentication
    @LogAnnotation("用户上传文件")
    @ApiOperation("文件上传")
    @PostMapping("/file")
    public Result upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        String url = userFileService.saveFile(file, request);
        return Result.succ(url);
    }

    @RequiresAuthentication
    @ApiOperation("上传图片")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cloud", value = "上是否传到OSS,false为上传到ECS上", required = true, defaultValue = "false"),
            @ApiImplicitParam(name = "compress", value = "上传图片是否压缩", required = true, defaultValue = "false")
    })
    @PostMapping("/img")
    public Result uploadImg(@RequestParam("file") MultipartFile file, HttpServletRequest request, Boolean cloud, Boolean compress) {
        String url = userFileService.saveImageAndCompress(file, request, cloud, compress);
        return Result.succ(url);
    }

    @RequiresAuthentication
    @PostMapping("/cover")
    @ApiOperation("上传用户空间背景")
    public Result uploadCover(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        String url = userFileService.saveCover(file, request);
        return Result.succ(url);
    }

    @RequiresAuthentication
    @LogAnnotation("上传了头像")
    @ApiOperation("上传头像,uid从token获取")
    @PostMapping("/avatar")
    public Result setAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        String url = userFileService.saveAvatar(file, request);
        return Result.succ("更新成功", url);
    }

    @GetMapping("/file/info")
    @ApiOperation("通过文件的url链接获取文件的详细信息")
    public Result getFileInfo(@RequestParam("url") String url) {
        UserFile uf = userFileService.getFileRecord(url);
        if (uf == null) {
            uf = new UserFile().setId(-1).setOriginalFileName("unknown");
        }
        return Result.succ(uf);
    }
}
