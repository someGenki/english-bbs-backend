package com.yuan.util;


import cn.hutool.core.util.IdUtil;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;


@Component
@Slf4j
@Data
@ConfigurationProperties(prefix = "yuan.qiniu")
public class QiniuUtil {
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String path;

    private Configuration cfg = new Configuration(Region.region2());
    private UploadManager uploadManager = new UploadManager(cfg);

    /**
     * 上传图片文件 时间戳-原始文件名
     *
     * @return 文件路径
     */
    public String uploadImg(MultipartFile file) {
        Auth auth = Auth.create(accessKey, secretKey);
        String token = auth.uploadToken(bucket);
        String originalFilename = file.getOriginalFilename();
        String fileKey = System.currentTimeMillis() + "-" + originalFilename;
        try {
            Response response = uploadManager.put(file.getInputStream(), fileKey, token, null, null);
            System.out.println(response.getInfo());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path + fileKey;
    }

    /**
     * 上传文件
     *
     * @return url
     */
    public String uploadFile(File file, String fileName) {
        String fileKey = (fileName != null) ? fileName : IdUtil.simpleUUID();
        Auth auth = Auth.create(accessKey, secretKey);
        String token = auth.uploadToken(bucket);
        try {
            uploadManager.put(file, fileKey, token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path + fileKey;
    }

}
