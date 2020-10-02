package com.yuan.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Field;

/**
 * https://blog.csdn.net/u010811939/article/details/84107446
 * 怎么说了 redisJbTemplate操作位图麻烦,还得使用jedis来操作,所以通过反射来获取连接池的一个jedis对象
 * 还有呢 网上的教程获取连接池是旧版的 新的是lettuce  得换回原来的jedis
 */
@Component
public class JedisUtil {
    @Autowired
    private RedisConnectionFactory connectionFactory;

    public Jedis getJedis() {
        Field jedisField = ReflectionUtils.findField(JedisConnection.class, "jedis");
        ReflectionUtils.makeAccessible(jedisField);
        Jedis jedis = (Jedis) ReflectionUtils.getField(jedisField, connectionFactory.getConnection());
        return jedis;
    }
}
