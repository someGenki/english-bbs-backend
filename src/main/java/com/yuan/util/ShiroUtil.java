package com.yuan.util;

import com.yuan.shiro.AccountProfile;
import org.apache.shiro.SecurityUtils;

/**
 * 获得Subject()里的用户信息
 */
public class ShiroUtil {
    public static AccountProfile getProfile() {
        return (AccountProfile) SecurityUtils.getSubject().getPrincipal();
    }

    /**
     * 获取当前token对应的uid
     * TODO 不应在service/controller中用jwtUtil去获取!!
     *
     * @return 当前登录uid
     */
    public static Integer getUidFromProfile() {
        return getProfile().getId();
    }
}