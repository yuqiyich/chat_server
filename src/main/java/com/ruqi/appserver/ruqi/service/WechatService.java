package com.ruqi.appserver.ruqi.service;

import java.util.List;

import com.ruqi.appserver.ruqi.dao.entity.WechatAccessTokenEntity;
import com.ruqi.appserver.ruqi.dao.entity.WechatMsgReceiverEntity;
import com.ruqi.appserver.ruqi.dao.mappers.WechatMapper;
import com.ruqi.appserver.ruqi.utils.EncryptUtils;
import com.ruqi.appserver.ruqi.utils.MyStringUtils;

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
        // TODO: 如果是分布式之类的，其他地方重新获取了本地的就不正确。
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
            return EncryptUtils.decode(mWechatAccessTokenEntity.accessToken);
        } else {
            return "";
        }
    }
    
    public void updateAccessToken(WechatAccessTokenEntity entity) {
        entity.accessToken = EncryptUtils.encode(entity.accessToken);

        // 先清除数据库，再插入这条数据
        wechatMapper.clearDatas();
        wechatMapper.insertData(entity);
    }

    /**
     * 绑定
     * @param nickname
     * @param openId
     */
    public void bindReceiver(String openId, String nickname) {
        if (MyStringUtils.isEmpty(openId)) {
            return;
        }

        openId = EncryptUtils.encode(openId);

        // 先查询数据库是否有数据，有的话设为绑定状态即可；不存在则新增一个数据。
        WechatMsgReceiverEntity receiverEntity = wechatMapper.queryReceiverByOpenid(openId);
        // System.out.println("--->queryReceiverByOpenid=" + JSON.toJSONString(receiverEntity));
        if (null != receiverEntity) {
            System.out.println("--->bindReceiver exits userStatus=" + receiverEntity.userStatus);
            if (receiverEntity.userStatus != 1) {
                receiverEntity.nickname = nickname;
                receiverEntity.userStatus = 1;
                wechatMapper.updateReceiver(receiverEntity);
            }
        } else {
            System.out.println("--->bindReceiver not exits insert");
            receiverEntity = new WechatMsgReceiverEntity();
            receiverEntity.openid = openId;
            receiverEntity.nickname = nickname;
            wechatMapper.insertReceiver(receiverEntity);
        }
    }
    
    public long queryReceiverSize(String nickname, String remarks, String userStatus) {
        return wechatMapper.queryReceiverSize(nickname, remarks, userStatus);
    }

    public List<WechatMsgReceiverEntity> queryReceivers(int pageIndex, int pageSize, String nickname, String remarks, String userStatus) {
        return wechatMapper.queryReceivers(pageIndex, pageSize, nickname, remarks, userStatus);
    }

    public List<WechatMsgReceiverEntity> queryAvailableReceivers() {
        return wechatMapper.queryAvailableReceivers();
    }
}