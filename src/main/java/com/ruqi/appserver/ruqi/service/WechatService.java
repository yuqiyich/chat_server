package com.ruqi.appserver.ruqi.service;

import java.util.List;

import com.ruqi.appserver.ruqi.dao.entity.WechatAccessTokenEntity;
import com.ruqi.appserver.ruqi.dao.mappers.WechatMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WechatService {
    @Autowired
    private WechatMapper wechatMapper;

    private WechatAccessTokenEntity mWechatAccessTokenEntity = null;

    /**
     * 已过期或者token为空，返回空。后续需要进行重新api请求获取和保存。
     * @return
     */
	public String queryAccessToken() {
        // 本地有则直接使用，否则数据库拿。
        if (null != mWechatAccessTokenEntity && 
            mWechatAccessTokenEntity.expiresTime.getTime() > System.currentTimeMillis()) {
            System.out.println("--->本地token");
            return mWechatAccessTokenEntity.accessToken;
        }

        List<WechatAccessTokenEntity> dataList = wechatMapper.queryWechatAccessToken();
        // System.out.println("--->dataList=" + JSON.toJSONString(dataList));
        if (null != dataList && dataList.size() > 0) {
            mWechatAccessTokenEntity = dataList.get(0);
        }
        if (null != mWechatAccessTokenEntity && 
            mWechatAccessTokenEntity.expiresTime.getTime() > System.currentTimeMillis()) {
            System.out.println("--->数据库token");
            return mWechatAccessTokenEntity.accessToken;
        } else {
            return "";
        }
    }
    
    public void updateAccessToken(WechatAccessTokenEntity entity) {
        // 先清除数据库，再插入这条数据
        wechatMapper.clearDatas();
        wechatMapper.insertData(entity);
    }
    
}