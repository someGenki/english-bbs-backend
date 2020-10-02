package com.yuan.common.task;

import com.yuan.common.enums.ItemTypeEnum;
import com.yuan.common.jiekou.ItemCanLike;
import com.yuan.common.jiekou.ItemLike;
import com.yuan.service.SignService;
import com.yuan.util.MyBeanUtil;
import com.yuan.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 定时落库点赞,还没有考虑被点赞资源不存在时是否会报错导致程序异常或者落库失败
 * spring boot 使用@Scheduled注解 https://blog.csdn.net/qq_36820717/article/details/88364766
 * 在线生成 https://cron.qqe2.com/
 */
@Slf4j
@Component
@SuppressWarnings("all")
@Transactional(rollbackFor = Exception.class)
public class MyTask {

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    MyBeanUtil beanUtil;
    @Autowired
    SignService signService;


    /**
     * 点赞数量和状态落库
     * 由于是多对多的关系，用中间表xxx_like记录点赞状态，xxx表记录获得的在赞数
     * 每个xxx就有xxx_like表，所以复杂了点
     *
     * @author yuan
     */
    @Transactional
    @Scheduled(cron = "0 0/10 * * * ?")
    public void redisLikeDataToDB() {
        ItemTypeEnum[] typeEnums = {ItemTypeEnum.TRANSLATION, ItemTypeEnum.COMMENT, ItemTypeEnum.POST};
        String key1, key2;
        Boolean bool1, bool2;
        ItemCanLike itemCanLike;
        ItemLike itemLike;
        /*
         遍历所有可点赞的资源,获取对应的key
          1. 获取hash1里资源的点赞数量,然后加到数据库里
          2. 获取hash2里资源跟用户点赞关系,来更新或插入到
         用户-资源点赞的关联表里.hash2的value是id:uid拼接而来的
         */
        for (ItemTypeEnum type : typeEnums) {
            key1 = RedisKeyUtil.getCountKeyByType(type);
            bool1 = redisTemplate.hasKey(key1);
            if (bool1 != null && bool1) {
                log.info("{}的点赞数量落库{}", type.getDesc(), LocalDateTime.now());
                itemCanLike = beanUtil.getItemCanLikeByType(type);
                Assert.notNull(itemCanLike, "对象为空" + type.getDesc());
                Map<Object, Object> map = redisTemplate.opsForHash().entries(key1);
                for (Map.Entry<Object, Object> entry : map.entrySet()) {
                    itemCanLike.updateLikes((int) entry.getKey(), (int) entry.getValue());
                }
                redisTemplate.delete(key1);
            }
            key2 = RedisKeyUtil.getStatusKeyByType(type);
            bool2 = redisTemplate.hasKey(key2);
            if (bool2 != null && bool2) {
                log.info("{}的点赞状态落库{}", type.getDesc(), LocalDateTime.now());
                itemLike = beanUtil.getItemLikeByType(type);
                Map<Object, Object> map = redisTemplate.opsForHash().entries(key2);
                for (Map.Entry<Object, Object> entry : map.entrySet()) {
                    int[] ids = RedisKeyUtil.splitField((String) entry.getKey());
                    itemLike.saveOrUpdateStatus(ids[1], ids[0], (int) entry.getValue());
                }
                redisTemplate.delete(key2);
            }
        }
    }

    /**
     * TOOD 用户签到记录入库 每月入库一次
     */
    @Scheduled(cron = "0 0 0 1 * ? ")
    @Transactional
    public void redisSignDataToDB() {
        LocalDate now = LocalDate.now();
        log.info("用户签到记录落库{}", now);
        signService.redisDataToDB(now);
    }
}
