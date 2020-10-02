package com.yuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.dto.LoginDto;
import com.yuan.common.dto.RegisterDto;
import com.yuan.common.dto.UserDto;
import com.yuan.entity.User;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author yuan
 * @since 2020-07-08
 */
public interface UserService extends IService<User> {

    /**
     * 根据uid获取头像,并可以加入缓存
     *
     * @param uid 用户id
     * @return 用户头像url
     */
    String getAvatarByUid(int uid);

    /**
     * 根据uid获取头像和昵称 ...
     *
     * @param uid
     * @return
     */
    UserDto getNewestInfo(Integer uid);

    boolean updateNickname(String newVal);

    UserDto login(LoginDto dto);

    UserDto register(RegisterDto dto);

}
