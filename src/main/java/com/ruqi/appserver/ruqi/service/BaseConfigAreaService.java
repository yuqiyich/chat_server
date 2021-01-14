package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.AreaAdInfo;
import com.ruqi.appserver.ruqi.dao.mappers.BaseAdAreaInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class BaseConfigAreaService {

      @Autowired
      private BaseAdAreaInfoWrapper adAreaInfoWrapper;

      //注解方式使用
      @Cacheable(key = "#adCode", value = "ad_code", unless = "#result eq null")
      public AreaAdInfo getAreaAdInfo(String adCode) {
        return  adAreaInfoWrapper.getAreaAdInfo(adCode);
      }


      public void saveAreaAdInfo(AreaAdInfo areaAdInfo) {
            adAreaInfoWrapper.saveAreaAdInfo(areaAdInfo);
      }
}
