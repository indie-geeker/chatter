package com.indiegeeker.data.utils;

import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Desc: Redis 工具类
 * Author: wen
 * Date: 2025/6/24
 **/
@Component
public class RedisUtils {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    // Key（键），简单的key-value操作
    /**
     * 判断 key 是否存在
     * @param key
     * @return
     */
    public boolean keyIsExist(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 以秒为单位，返回给定 key的剩余生存时间(TTL, time to live)
     * @param key
     * @return
     */
    public long getExpire(String key) {
        return stringRedisTemplate.getExpire(key);
    }

    /**
     * 设置过期时间，单位
     * @param key
     * @param timeout
     */
    public void setExpire(String key, long timeout) {
        stringRedisTemplate.expire(key,timeout, TimeUnit.SECONDS);
    }


    /**
     * 找所有符合给定模式 pattern的 key
     * @param pattern
     * @return
     */
    public Set<String> optKeys(String pattern){
        return stringRedisTemplate.keys(pattern);
    }

    /**
     * 删除 Key
     * @param key 可以传一个值 或多个
     */
    public void delKey(String key){
        Set<String> keys = optKeys(key + "*");
        stringRedisTemplate.delete(keys);
    }

    // String（字符串）

    /**
     * 设置key-value值
     * @param key
     * @param value
     */
    public void set(String key, String value){
        stringRedisTemplate.opsForValue().set(key,value);
    }

    /**
     * 设置key-value值、超时时间(秒)
     * @param key
     * @param value
     */
    public void setExpireBySecond(String key, String value, long timeout){
        set(key,value,timeout,TimeUnit.SECONDS);
    }

    /**
     * 设置key-value值、超时时间(分钟)
     * @param key
     * @param value
     */
    public void setExpireByMinutes(String key, String value, long timeout){
        set(key,value,timeout,TimeUnit.MINUTES);
    }

    /**
     * 设置key-value值、超时时间(小时)
     * @param key
     * @param value
     */
    public void setExpireByHours(String key, String value, long timeout){
        set(key,value,timeout,TimeUnit.HOURS);
    }

    /**
     * 设置key-value值、超时时间(天)
     * @param key
     * @param value
     */
    public void setExpireByDays(String key, String value, long timeout){
        set(key,value,timeout,TimeUnit.DAYS);
    }

    /**
     * 设置key-value值、超时时间
     * @param key
     * @param value
     * @param timeout
     */
    public void set(String key, String value, long timeout, TimeUnit timeUnit){
        stringRedisTemplate.opsForValue().set(key,value,timeout,timeUnit);
    }

    /**
     * 如果key不存在，则设置，如果存在，则不操作
     * @param key
     * @param value
     * @return true表示设置成功，false表示key已存在未设置
     */
    public Boolean setIfAbsent(String key, String value){
        return stringRedisTemplate.opsForValue().setIfAbsent(key,value);
    }

    /**
     * 如果key不存在，则设置，如果存在，则不操作
     * @param key
     * @param value
     * @param timeout
     * @param timeUnit
     * @return true表示设置成功，false表示key已存在未设置
     */
    public Boolean setIfAbsent(String key, String value, long timeout, TimeUnit timeUnit){
        return stringRedisTemplate.opsForValue().setIfAbsent(key,value,timeout,timeUnit);
    }

    /**
     * 获取Key 对应的 Value
     * @param key
     * @return
     */
    public String get(String key){
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 获取多个 Key 对应的 Value
     * 少量的批量查询（千个以下）
     * @param keys
     * @return
     */
    public List<String> multiGet(List<String> keys){
        return stringRedisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 获取多个 Key 对应的 Value，批量查询，管道pipeline 实现
     * 适用于执行大量独立的写入或读取操作
     * @param keys
     * @return
     */
    public List<String> batchGet(List<String> keys){
        List<Object> result = stringRedisTemplate.executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(@Nonnull RedisConnection connection) throws DataAccessException {
                StringRedisConnection src = (StringRedisConnection)connection;
                for (String k : keys) {
                    src.get(k);
                }
                return null;
            }
        });

        return result.stream().map(o -> (String)o).collect(Collectors.toList());
    }

    /**
     * 累加
     * 原子性地递增存储在键中的数字值，若键不存在则初始化为0后再递增
     * @param key
     */
    public Long increment(String key){
        return stringRedisTemplate.opsForValue().increment(key);
    }

    /**
     * 累加, 可设置步长
     * 原子性地递增存储在键中的数字值，若键不存在则初始化为0后再递增
     * @param key
     * @param delta
     */
    public Long increment(String key,long delta){
        return stringRedisTemplate.opsForValue().increment(key,delta);
    }

    /**
     * 累减
     * 原子性地递减存储在键中的数字值，若键不存在则初始化为0后再递增
     * @param key
     */
    public Long decrement(String key){
        return stringRedisTemplate.opsForValue().decrement(key);
    }

    /**
     * 累减，可设置步长
     * 原子性地递减存储在键中的数字值，若键不存在则初始化为0后再递增
     * @param key
     * @param delta
     */
    public Long decrement(String key,long delta){
        return stringRedisTemplate.opsForValue().decrement(key,delta);
    }
}
