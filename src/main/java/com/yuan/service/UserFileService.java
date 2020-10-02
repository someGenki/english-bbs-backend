package com.yuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.entity.UserFile;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户上传文件记录表 服务类
 * </p>
 *
 * @author yuan
 * @since 2020-07-24
 */
public interface UserFileService extends IService<UserFile> {

    /**
     * 返回文件的url路径
     */
    String saveFile(MultipartFile file, HttpServletRequest request);

    UserFile getFileRecord(String url);
    /**
     * 保存头像 使用OSS并且压缩,并返回url
     */
    String saveAvatar(MultipartFile file, HttpServletRequest request);

    /**
     * 保存图片使用oss并压缩
     */
    String saveImageAndCompress(MultipartFile file, HttpServletRequest request);

    /**
     * 图片处理,并统一用jpg格式保存...
     *
     * @param file
     * @param request
     * @param useOSS     true 保存到七牛云  false 保存到本地
     * @param isCompress 是否压缩图片 true 压    false 不压
     * @return
     */
    String saveImageAndCompress(MultipartFile file, HttpServletRequest request, Boolean useOSS, Boolean isCompress);

    /**
     * 设置用户封面图片
     *
     * @param file
     * @param request
     */
    String saveCover(MultipartFile file, HttpServletRequest request);
}
