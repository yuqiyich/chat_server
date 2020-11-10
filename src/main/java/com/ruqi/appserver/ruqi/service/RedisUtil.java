package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.utils.MyStringUtils;
import io.lettuce.core.RedisCommandTimeoutException;
import io.lettuce.core.RedisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.persistence.QueryTimeoutException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class RedisUtil {
    private Logger logger = LoggerFactory.getLogger(getClass());
    public static final String GROUP_USER_INFO = "user_info";
    public static final String GROUP_ENCRYPT_UTIL_SIGN = "encrypt_sign";
    public static final String GROUP_APP_VERSION_NAME = "app_version_name";


    public static final String GROUP_RECOMMEND_POINT = "point_recommend";


    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 默认过期时长，单位：秒
     */
    public static final long DEFAULT_EXPIRE = 60 * 60 * 24;

    public static final long EXPIRE_WEEK = 60 * 60 * 24 * 7;

    /**
     * 不设置过期时长
     */
    public static final long NOT_EXPIRE = -1;

    private String getGroupKey(String groupName, String key) {
        if (MyStringUtils.isEmpty(groupName)) {
            return key;
        }
        return groupName + "::" + key;
    }

    public Object getKey(String groupName, String key) {
        return redisTemplate.opsForValue().get(getGroupKey(groupName, key));
    }

    public void putQueueData(String groupKey,String queueName,Object  value) {
        queueName = getGroupKey(groupKey, queueName);
        try {
            ListOperations operation = redisTemplate.opsForList();
            operation.leftPush(queueName, value);
        }catch (RedisException| RedisSystemException e){
            logger.error("putQueueData failed",e);
            e.printStackTrace();
        }
    }

    /**
     *
     * @param groupName
     * @param queueName
     * @return
     */
    public Object popQueueDataBlock(String groupName,String queueName) {
        queueName = getGroupKey(groupName, queueName);
        try {
            ListOperations operation = redisTemplate.opsForList();
            return  operation.leftPop(queueName  ,30,TimeUnit.SECONDS);
        }catch (RedisException| RedisSystemException e){
            logger.error("popQueueDataBlock failed",e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param groupName
     * @param queueName
     * @return
     */
    public List rangeQueueDataBlock(String groupName, String queueName, int pullSize) {
        queueName = getGroupKey(groupName, queueName);
        try {
            ListOperations operation = redisTemplate.opsForList();
            List data=  operation.range(queueName  ,0,pullSize);
            operation.trim(queueName,pullSize,-1);//删除数据
            return data;
        }catch (RedisException| RedisSystemException e){
            logger.error("rangeQueueDataBlock failed",e);
            e.printStackTrace();
        }
        return null;
    }

    public long getQueueSize(String groupName, String queueName) {
        try{
            queueName = getGroupKey(groupName, queueName);
            ListOperations operation = redisTemplate.opsForList();
            return operation.size(queueName);
        }catch (RedisCommandTimeoutException| RedisConnectionFailureException e){
            e.printStackTrace();
            logger.error("get queue size failed",e);
            return 0;
        }
    }

    public void putKey(String groupName, String key, Object value, long time, TimeUnit timeUnit) {
        key = getGroupKey(groupName, key);
        if (time > 0 && timeUnit != null) {
            redisTemplate.opsForValue().set(key, value, time, timeUnit);
        } else {
            redisTemplate.opsForValue().set(key, value, DEFAULT_EXPIRE, TimeUnit.SECONDS);
        }
    }

    public void putKey(String groupName, String key, Object value) {
        putKey(groupName, key, value, DEFAULT_EXPIRE, TimeUnit.SECONDS);
    }

    public boolean existsKey(String groupName, String key) {
        return redisTemplate.hasKey(getGroupKey(groupName, key));
    }

    /**
     * 重名名key，如果newKey已经存在，则newKey的原值被覆盖
     *
     * @param oldKey
     * @param newKey
     */
    public void renameKey(String groupName, String oldKey, String newKey) {
        redisTemplate.rename(getGroupKey(groupName, oldKey), getGroupKey(groupName, newKey));
    }

    /**
     * newKey不存在时才重命名
     *
     * @param oldKey
     * @param newKey
     * @return 修改成功返回true
     */
    public boolean renameKeyNotExist(String groupName, String oldKey, String newKey) {
        return redisTemplate.renameIfAbsent(getGroupKey(groupName, oldKey), getGroupKey(groupName, newKey));
    }

    /**
     * 删除key
     *
     * @param key
     */
    public void deleteKey(String groupName, String key) {
        redisTemplate.delete(getGroupKey(groupName, key));
    }

    /**
     * 删除key
     */
    public void deleteKeys(String groupName) {
        /*keys方法 进行模糊匹配*/
        Set keys = redisTemplate.keys(groupName + "::*");
        /*执行删除*/
        redisTemplate.delete(keys);


    }

    /**
     * 删除Key的集合
     *
     * @param keys
     */
    public void deleteKey(String groupName, Collection<String> keys) {
        Set<String> kSet = keys.stream().map(k -> k).collect(Collectors.toSet());
        for (String key : kSet) {
            deleteKey(groupName, key);
        }
    }

    /**
     * 设置key的生命周期
     *
     * @param key
     * @param time
     * @param timeUnit
     */
    public void expireKey(String groupName, String key, long time, TimeUnit timeUnit) {
        redisTemplate.expire(getGroupKey(groupName, key), time, timeUnit);
    }

    /**
     * 指定key在指定的日期过期
     *
     * @param key
     * @param date
     */
    public void expireKeyAt(String groupName, String key, Date date) {
        redisTemplate.expireAt(getGroupKey(groupName, key), date);
    }

    /**
     * 查询key的生命周期
     *
     * @param key
     * @param timeUnit
     * @return
     */
    public long getKeyExpire(String groupName, String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(getGroupKey(groupName, key), timeUnit);
    }

    /**
     * 将key设置为永久有效
     *
     * @param key
     */
    public void persistKey(String groupName, String key) {
        redisTemplate.persist(getGroupKey(groupName, key));
    }

}