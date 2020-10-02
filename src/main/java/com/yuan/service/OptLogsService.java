package com.yuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.entity.OptLogs;

import java.util.List;

/**
 * <p>
 * 用户操作记录表（动态） 服务类
 * </p>
 * <p>
 * 被依赖于：UserService
 *
 * @author yuan
 * @since 2020-09-24
 */
public interface OptLogsService extends IService<OptLogs> {

    /**
     * 生成一条用户动态
     *
     * @param uid     用户id
     * @param optType 动态的类型：用枚举，大概包括，用户注册，用户发帖，用户评论，之后在加
     * @param optId
     */
    void createOptLogs(Integer uid, Integer optType, Integer optId, String content);

    List<OptLogs> getOptLogs(Integer uid);
}
