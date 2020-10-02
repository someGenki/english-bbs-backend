package com.yuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.dto.SignDto;
import com.yuan.common.dto.SitesInfoDto;
import com.yuan.entity.Sign;

import java.time.LocalDate;

/**
 * <p>
 * 用户每月签到表 服务类
 * </p>
 * 参考:https://www.cnblogs.com/liujiduo/p/10396020.html
 *
 * @author yuan
 * @since 2020-07-26
 */
public interface SignService extends IService<Sign> {

    /**
     * 用户签到功能(过于复杂)
     * 没有签到则进行签到并返回用户签到信息
     * 签到过则返回签到用户消息
     * 用户要再次查看今日的签到状况可以再次调用该方法
     *
     * @param uid 用户id
     * @return 返回签到信息
     */
    SignDto sign(Integer uid);

    /**
     * 判断是否签到
     */
    boolean checkSign(Integer uid, LocalDate date);

    /**
     * 获取本月签到次数
     */
    Integer getSignCount(Integer uid, LocalDate date);

    /**
     * 获取签到状态 e.g:000000000000001111000(31个字符长度)
     * 第一天从的数组下标从0开始
     *
     * @param uid 用户id
     * @return 一串0或1的字符串(31个字符长度)
     */
    String getSignInfo(int uid, LocalDate date);

    /**
     * 获取本月第一次签到的时间
     */
    LocalDate getFirstSignDate(int uid, LocalDate date);

    /**
     * 从数据库中获取用户签到记录
     */
    Sign getSignByUid(int uid);

    /**
     * 根据当月签到记录获取用户连续签到次数
     *
     * @param chars 01010101类型的char数组
     */
    int getUserCheckContTime(char[] chars);

    SitesInfoDto getSitesSignInfo(LocalDate date);

    void redisDataToDB(LocalDate date);
}
