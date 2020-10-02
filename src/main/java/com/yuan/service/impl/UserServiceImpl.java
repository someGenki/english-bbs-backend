package com.yuan.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.dto.LoginDto;
import com.yuan.common.dto.RegisterDto;
import com.yuan.common.dto.UserDto;
import com.yuan.common.enums.OptTypeEnum;
import com.yuan.entity.Sign;
import com.yuan.entity.User;
import com.yuan.entity.Userinfo;
import com.yuan.mapper.UserMapper;
import com.yuan.service.OptLogsService;
import com.yuan.service.SignService;
import com.yuan.service.UserService;
import com.yuan.service.UserinfoService;
import com.yuan.util.JwtUtils;
import com.yuan.util.ShiroUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author yuan
 * @since 2020-07-08
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Resource
    UserMapper mapper;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    UserinfoService userinfoService;
    @Autowired
    SignService signService;
    @Autowired
    OptLogsService optLogsService;

    /**
     * 根据uid获取头像
     *
     * @param uid 用户id
     * @return 用户头像url
     */
    @Override
    public String getAvatarByUid(int uid) {
        return mapper.getAvatarId(uid);
    }

    @Override
    public UserDto login(LoginDto dto) {
        User user = this.getOne(new QueryWrapper<User>().eq("username", dto.getUsername()));
        Assert.notNull(user, "用户不存在");
        //如果数据库表里用户密码不等于MD5(dto里的密码+表里的盐)则返回错误
        Assert.isTrue(user.getPassword().equals(SecureUtil.md5(dto.getPassword() + user.getSalt())),
                "用户名或密码错误");
        String token = jwtUtils.generateToken(user.getId());
        // 或者用BeanUtils的copy
        return new UserDto().setToken(token)
                .setUid(user.getId())
                .setNickname(user.getNickname())
                .setUsername(user.getUsername())
                .setAvatar(user.getAvatar());
    }

    @Override
    public UserDto register(RegisterDto dto) {
        //生成一条User记录
        User user = new User();
        String slat = "jojo";  //TODO 生成4位随机盐,并且判断2次密码是否一致(应该交给前端)
        user.setPassword(SecureUtil.md5(dto.getPassword() + slat))
                .setUsername(dto.getUsername()).setSalt(slat)
                .setNickname(dto.getUsername());// 默认昵称为用户名
        this.save(user);
        //获取uid 生成jwt,并保存生成对应的用户信息和签到信息
        User u = this.getOne(new QueryWrapper<User>().eq("username", dto.getUsername()), false);
        int uid = u.getId();
        String username = u.getUsername();
        userinfoService.save(new Userinfo().setUid(uid));
        signService.save(new Sign().setUid(uid));
        optLogsService.createOptLogs(uid, OptTypeEnum.REGISTERED.getType(), null, null);
        String token = jwtUtils.generateToken(uid);
        return new UserDto().setToken(token).setUid(uid).setNickname(username).setUsername(username);
    }

    /**
     * 根据uid获取头像和昵称 ...
     *
     * @param uid
     * @return
     */
    @Override
    public UserDto getNewestInfo(Integer uid) {
        return mapper.getNewest(uid);
    }

    @Override
    public boolean updateNickname(String newVal) {
        if (StringUtils.hasText(newVal) && newVal.length() < 20) {
            // 先简单的校验
            this.update(new UpdateWrapper<User>().set("nickname", newVal).eq("id", ShiroUtil.getUidFromProfile()));
            return true;
        } else {
            return false;
        }

    }

}
