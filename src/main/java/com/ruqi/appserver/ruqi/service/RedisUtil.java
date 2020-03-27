package com.ruqi.appserver.ruqi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class RedisUtil {
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 默认过期时长，单位：秒
     */
    public static final long DEFAULT_EXPIRE = 60 * 60 * 24;

    /**
     * 不设置过期时长
     */
    public static final long NOT_EXPIRE = -1;

    public Object getKey(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void putKey(String key,Object value, long time,TimeUnit timeUnit) {
        if (time>0&&timeUnit!=null){
            redisTemplate.opsForValue().set(key,value,time,timeUnit);
        } else {
            redisTemplate.opsForValue().set(key,value,DEFAULT_EXPIRE,TimeUnit.SECONDS);
        }

    }

    public void putKey(String key,Object value) {
        redisTemplate.opsForValue().set(key,value,DEFAULT_EXPIRE,TimeUnit.SECONDS);
    }


    public boolean existsKey(Object key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 重名名key，如果newKey已经存在，则newKey的原值被覆盖
     *
     * @param oldKey
     * @param newKey
     */
    public void renameKey(Object oldKey, Object newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    /**
     * newKey不存在时才重命名
     *
     * @param oldKey
     * @param newKey
     * @return 修改成功返回true
     */
    public boolean renameKeyNotExist(Object oldKey, Object newKey) {
        return redisTemplate.renameIfAbsent(oldKey, newKey);
    }

    /**
     * 删除key
     *
     * @param key
     */
    public void deleteKey(Object key) {
        redisTemplate.delete(key);
    }


    /**
     * 删除Key的集合
     *
     * @param keys
     */
    public void deleteKey(Collection<Object> keys) {
        Set<Object> kSet = keys.stream().map(k -> k).collect(Collectors.toSet());
        redisTemplate.delete(kSet);
    }

    /**
     * 设置key的生命周期
     *
     * @param key
     * @param time
     * @param timeUnit
     */
    public void expireKey(Object key, long time, TimeUnit timeUnit) {
        redisTemplate.expire(key, time, timeUnit);
    }

    /**
     * 指定key在指定的日期过期
     *
     * @param key
     * @param date
     */
    public void expireKeyAt(Object key, Date date) {
        redisTemplate.expireAt(key, date);
    }

    /**
     * 查询key的生命周期
     *
     * @param key
     * @param timeUnit
     * @return
     */
    public long getKeyExpire(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    /**
     * 将key设置为永久有效
     *
     * @param key
     */
    public void persistKey(Object key) {
        redisTemplate.persist(key);
    }
}