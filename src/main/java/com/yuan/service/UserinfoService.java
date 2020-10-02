package com.yuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.dto.UserDigestDto;
import com.yuan.entity.Userinfo;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author yuan
 * @since 2020-07-16
 */
public interface UserinfoService extends IService<Userinfo> {

    Userinfo getUserinfo(Integer uid);

    UserDigestDto getUserDigest(Integer uid);
}
