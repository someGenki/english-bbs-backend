package com.yuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.dto.UserDigestDto;
import com.yuan.entity.Userinfo;
import com.yuan.mapper.UserinfoMapper;
import com.yuan.service.UserinfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author yuan
 * @since 2020-07-16
 */
@Service
public class UserinfoServiceImpl extends ServiceImpl<UserinfoMapper, Userinfo> implements UserinfoService {

    @Resource
    UserinfoMapper userinfoMapper;

    @Override
    public Userinfo getUserinfo(Integer uid) {
        return userinfoMapper.selectOne(new QueryWrapper<Userinfo>().eq("uid", uid));
    }

    @Override
    public UserDigestDto getUserDigest(Integer uid) {

        return userinfoMapper.getDigest(uid);
    }
}
