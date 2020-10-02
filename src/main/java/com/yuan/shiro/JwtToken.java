package com.yuan.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * 自定义一个JwtToken，来完成shiro的supports方法。
 * 还可以再redis中 token作为键,uid作为指来根据token获取uid
 */
public class JwtToken implements AuthenticationToken {
    private final String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}