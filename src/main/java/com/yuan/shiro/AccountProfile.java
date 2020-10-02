package com.yuan.shiro;

import lombok.Data;

import java.io.Serializable;

/**
 * 四
 * 一个用户信息的载体
 */
@Data
public class AccountProfile implements Serializable {

    private Integer id;
    private String username;
    private String nickname;
    private String avatar;
    private Integer type;

}