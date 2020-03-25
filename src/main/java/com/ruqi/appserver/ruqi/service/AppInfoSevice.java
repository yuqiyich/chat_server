package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.AppInfo;
import com.ruqi.appserver.ruqi.dao.mappers.AppInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class AppInfoSevice {
    @Autowired
    private AppInfoWrapper appInfoWrapper;

    @Cacheable(key = "#key", value = "app_info", unless = "#result eq null")
    public AppInfo getAppInfoByKey(String key) {
        return appInfoWrapper.getAppInfoByKey(key);
    }
}
