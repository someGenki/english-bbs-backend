package com.yuan.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.entity.User;
import com.yuan.entity.UserFile;
import com.yuan.entity.Userinfo;
import com.yuan.mapper.UserFileMapper;
import com.yuan.service.UserFileService;
import com.yuan.service.UserService;
import com.yuan.service.UserinfoService;
import com.yuan.util.JwtUtils;
import com.yuan.util.QiniuUtil;
import com.yuan.util.ShiroUtil;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 用户上传文件记录表 服务实现类
 * </p>
 * 主要是用来学习的,写了一些不重要的代码
 *
 * @author yuan
 * @since 2020-07-24
 */
@Slf4j
@Service
public class UserFileServiceImpl extends ServiceImpl<UserFileMapper, UserFile> implements UserFileService {

    //支持的图片类型S
    private static final List<String> CONTENT_TYPE = Arrays.asList("image/gif", "image/jpeg", "image/png");
    private static final String FOLDER_NAME = "images/";
    @Value("${yuan.file.root.path}")
    public String fileRootPath;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    QiniuUtil qiniuUtil;
    @Autowired
    UserService userService;
    @Autowired
    UserinfoService userinfoService;


    @Override
    public String saveFile(MultipartFile file, HttpServletRequest request) {
//        Integer uid = jwtUtils.getUidByRequest(request);
        Integer uid = ShiroUtil.getUidFromProfile();
        Assert.isTrue(!file.isEmpty(), "文件为空");
        // 计算md5,相同的话,就复制之前的url,并不做文件存储
        // 如果要计算积分的话,要重新弄表,还挺麻烦
        String md5 = null;
        try {
            md5 = SecureUtil.md5(file.getInputStream());
        } catch (Exception e) {
            return null;
        }
        UserFile existFile = getOne(new UpdateWrapper<UserFile>().eq("md5", md5).last("limit 1"), false);
        if (existFile != null) {
            return existFile.getUrl();
        }

        // 原始名 以 eyes.jpg为例
        String originalFilename = file.getOriginalFilename();
        // 加上时间戳生成新的文件名,防止重复 newFileName = "1595511980146eyes.jpg"
        String newFileName = System.currentTimeMillis() + originalFilename;
        // 获取后缀并拼接'/'用于分类,也可以用日期 例: suffix1 = "jpg/"
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        String suffix1 = suffix + "/";
        // 文件保存的位置
        String filePath = fileRootPath + suffix1 + newFileName;
        // 文件web浏览路径
        String urlPath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/archive/" + suffix1 + newFileName;
        // 封装对象
        UserFile userFile = new UserFile();
        userFile.setOriginalFileName(originalFilename).setNewFileName(newFileName)
                .setFileSize(file.getSize()).setFileSuffix(suffix)
                .setUrl(urlPath).setUid(uid).setMd5(md5);
        // 保存文件
        try {
            File file1 = new File(filePath);
            if (!file1.exists()) file1.mkdirs(); // 要是目录不存在,创建一个,linux中需要权限
            file.transferTo(file1);
            this.save(userFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return urlPath;
    }

    @Override
    public UserFile getFileRecord(String url) {
        UserFile uf = this.getOne(new QueryWrapper<UserFile>().eq("url", url), false);
        System.out.println(uf);
        return uf;
    }

    public String saveImageAndCompress(MultipartFile file, HttpServletRequest request) {
        return this.saveImageAndCompress(file, request, true, true);
    }

    /**
     * 设置用户封面图片
     *
     * @param file
     * @param request
     */
    @Override
    public String saveCover(MultipartFile file, HttpServletRequest request) {
        Integer uid = ShiroUtil.getUidFromProfile();
        System.out.println(uid);
        Assert.isTrue(!file.isEmpty(), "文件为空");
        // 包含返回true,是合法的类型
        Assert.isTrue(CONTENT_TYPE.contains(file.getContentType()), "图片类型不合法");
        String url = this.saveImageAndCompress(file, request, true, false);
        System.out.println(userinfoService.update(new UpdateWrapper<Userinfo>()
                .eq("uid", uid)
                .set("cover_img", url)));
        return url;
    }

    @Override
    public String saveImageAndCompress(MultipartFile file, HttpServletRequest request, Boolean useOSS, Boolean isCompress) {
        // 用户id,原始名,文件路径,后缀名,新名字,url路径,临时文件,md5
//        Integer uid = jwtUtils.getUidByRequest(request);
        Integer uid = ShiroUtil.getUidFromProfile();
        String originalFilename = file.getOriginalFilename();
        String filePath = fileRootPath + FOLDER_NAME;
        String newFileName;
        String urlPath;
        File tempFile;
        String md5;
        try {
            // 是否压缩图片 图像处理质量为0.8倍,宽高为160*160像素,后缀为jpg
            if (isCompress) {
                newFileName = System.currentTimeMillis() + "-" + originalFilename
                        .substring(0, originalFilename.lastIndexOf(".")) + ".jpg";
                tempFile = new File(filePath, newFileName);
                if (!tempFile.getParentFile().exists()) {
                    tempFile.getParentFile().mkdirs();//创建父级文件路径
                    tempFile.createNewFile();//创建文件
                }
                Thumbnails.of(file.getInputStream()).size(140, 140)
                        .outputQuality(0.8f) // 输出图片的质量,scale获得缩略图
                        .outputFormat("jpeg").toFile(tempFile);
            } else {
                newFileName = System.currentTimeMillis() + "-" + originalFilename;
                tempFile = new File(filePath, newFileName);
                if (!tempFile.getParentFile().exists()) {
                    tempFile.getParentFile().mkdirs();//创建父级文件路径
                    tempFile.createNewFile();//创建文件
                }
                file.transferTo(tempFile);
            }
            // 算出图片的md5,要是有了就不保存 返回存在的url
            md5 = SecureUtil.md5(tempFile);
            UserFile existFile = getOne(new UpdateWrapper<UserFile>().eq("md5", md5).last("limit 1"), false);
            long size = tempFile.length();
            if (existFile != null) {
                tempFile.delete();
                return existFile.getUrl();
            }
            // 选择保存到本地还是七牛云
            if (useOSS) {
                urlPath = qiniuUtil.uploadFile(tempFile, newFileName);
                tempFile.delete();
            } else {
                urlPath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/archive/" + FOLDER_NAME + newFileName;
            }
            // 记录文件
            this.save(new UserFile() // 记录文件信息到数据库 非关键代码
                    .setOriginalFileName(originalFilename).setNewFileName(newFileName)
                    .setFileSize(size).setFileSuffix(".jpg")
                    .setUrl(urlPath).setUid(uid).setMd5(md5));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return urlPath;
    }


    @Override
    public String saveAvatar(MultipartFile file, HttpServletRequest request) {
//        Integer uid = jwtUtils.getUidByRequest(request);
        Integer uid = ShiroUtil.getUidFromProfile();
        Assert.isTrue(!file.isEmpty(), "文件为空");
        // 包含返回true,是合法的类型
        Assert.isTrue(CONTENT_TYPE.contains(file.getContentType()), "图片类型不合法");
        String url = this.saveImageAndCompress(file, request);
        userService.update(new UpdateWrapper<User>()
                .eq("id", uid)
                .set("avatar", url));
        return url;
    }
}
