package com.yuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.enums.OptTypeEnum;
import com.yuan.entity.OptLogs;
import com.yuan.entity.Post;
import com.yuan.mapper.OptLogsMapper;
import com.yuan.service.OptLogsService;
import com.yuan.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户操作记录表（动态） 服务实现类
 * </p>
 *
 * @author yuan
 * @since 2020-09-24
 */
@Service
public class OptLogsServiceImpl extends ServiceImpl<OptLogsMapper, OptLogs> implements OptLogsService {

    @Autowired
    PostService postService;

    /**
     * 生成一条用户动态
     *
     * @param uid     用户id
     * @param optType 动态的类型：用枚举，大概包括，用户注册，用户发帖，用户评论，之后在加
     * @param optId   可能涉及到资源的id
     */
    @Override
    public void createOptLogs(Integer uid, Integer optType, Integer optId, String content) {
        OptLogs opt = new OptLogs();
        opt.setUid(uid);
        opt.setOptType(optType);
        if (optType == OptTypeEnum.REGISTERED.getType()) {
            opt.setContent("成为了这个社区的一名会员");
        } else if (optType == OptTypeEnum.POSTED.getType()) {
            opt.setOptId(optId);
        } else if (optType == OptTypeEnum.TWEET.getType()) {
            opt.setContent(content);
        } else {
            System.out.println("异常");
        }
        this.save(opt);
    }

    @Override
    public List<OptLogs> getOptLogs(Integer uid) {
        List<OptLogs> list = this.list(new QueryWrapper<OptLogs>().eq("uid", uid).orderByDesc("gmt_create").last("limit 10"));
        List<Integer> collect = list.stream()
                .filter(optLogs -> optLogs.getOptType() == OptTypeEnum.POSTED.getType())
                .map(OptLogs::getOptId)
                .collect(Collectors.toList());
        if (collect.size() > 0) {
            List<Post> posts = postService.listByIds(collect);
            return list.stream().peek(e -> {
                for (Post post : posts) {
                    if (Objects.equals(post.getId(), e.getOptId()))
                        e.setDetail(post);
                }
            }).collect(Collectors.toList());
        } else {
            return list;
        }
    }
}
