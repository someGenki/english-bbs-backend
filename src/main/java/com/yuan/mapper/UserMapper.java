package com.yuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuan.common.dto.UserDto;
import com.yuan.entity.User;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author yuan
 * @since 2020-07-08
 */
public interface UserMapper extends BaseMapper<User> {

    @ResultType(String.class)
    @Select("select nickname from y_user where id=#{uid}")
    String getNicknameById(Integer uid);

    @ResultType(String.class)
    @Select("select avatar from y_user where id=#{uid}")
    String getAvatarId(Integer uid);

    @ResultType(UserDto.class)
    @Select("select id as uid ,username,avatar,nickname,point from y_user where id=#{uid}")
    UserDto getNewest(Integer uid);
}
