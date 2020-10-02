package com.yuan.common.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@ApiModel("登录注册后的传输对象")
@Data
public class UserDto {
    Integer uid;
    Integer point;
    String token;
    String username;
    String nickname;
    String avatar;
}
