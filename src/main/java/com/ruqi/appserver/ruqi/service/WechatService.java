package com.ruqi.appserver.ruqi.service;

import java.sql.Timestamp;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.ruqi.appserver.ruqi.dao.entity.WechatAccessTokenEntity;
import com.ruqi.appserver.ruqi.dao.mappers.WechatMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WechatService {
    @Autowired
    private WechatMapper wechatMapper;

    /**
     * 已过期或者token为空，返回空。后续需要进行重新api请求获取和保存。
     * @return
     */
	public String queryAccessToken() {
        List<WechatAccessTokenEntity> dataList = wechatMapper.queryWechatAccessToken();
        // System.out.println("--->dataList=" + JSON.toJSONString(dataList));
        WechatAccessTokenEntity wechatAccessTokenEntity = null;
        if (null != dataList && dataList.size() > 0) {
            wechatAccessTokenEntity = dataList.get(0);
        }
        if (null != wechatAccessTokenEntity && 
            wechatAccessTokenEntity.expiresTime.getTime() > System.currentTimeMillis()) {
            return wechatAccessTokenEntity.accessToken;
        } else {
            return "";
        }
	}
    
}