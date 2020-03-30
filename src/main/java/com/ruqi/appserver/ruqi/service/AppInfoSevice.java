package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.AppInfo;
import com.ruqi.appserver.ruqi.dao.mappers.AppInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class AppInfoSevice {
    public static final int APP_CACHE_EXPIRE_MAX = 200;// 单位天

    @Autowired
    private AppInfoWrapper appInfoWrapper;
    @Autowired
    private RedisUtil redisService;

    //注解方式使用
    @Cacheable(key = "#key", value = "app_info", unless = "#result eq null")
    public AppInfo getAppInfoByKey(String key) {
        return appInfoWrapper.getAppInfoByKey(key);
    }

//    public AppInfo getAppInfoByKey(String key) {
//        AppInfo resultAppInfo=null;
//        if (redisService.existsKey(key)){
//            resultAppInfo = (AppInfo) redisService.getKey(key);
//            redisService.expireKey(key,APP_CACHE_EXPIRE_MAX, TimeUnit.DAYS);//
//        } else {
//            resultAppInfo = appInfoWrapper.getAppInfoByKey(key);
//            redisService.putKey(key,resultAppInfo,APP_CACHE_EXPIRE_MAX, TimeUnit.DAYS);
//        }
//        return resultAppInfo;
//    }
}
