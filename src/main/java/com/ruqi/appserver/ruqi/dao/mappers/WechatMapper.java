package com.ruqi.appserver.ruqi.dao.mappers;

import java.util.List;

import com.ruqi.appserver.ruqi.dao.entity.WechatAccessTokenEntity;
import com.ruqi.appserver.ruqi.dao.entity.WechatMsgReceiverEntity;

import org.springframework.stereotype.Repository;

@Repository
public interface WechatMapper {
	List<WechatAccessTokenEntity> queryWechatAccessToken();
	void clearDatas();
	void insertData(WechatAccessTokenEntity entity);

	List<WechatMsgReceiverEntity> queryReceivers();
	List<WechatMsgReceiverEntity> queryAvailableReceivers();
	WechatMsgReceiverEntity queryReceiverByOpenid(String openid);
	void updateReceiver(WechatMsgReceiverEntity receiverEntity);
	void insertReceiver(WechatMsgReceiverEntity receiverEntity);
}