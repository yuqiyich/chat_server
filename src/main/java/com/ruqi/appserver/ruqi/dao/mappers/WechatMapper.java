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

	long queryReceiverSize(String nickname, String remarks, String userStatus);
	List<WechatMsgReceiverEntity> queryReceivers(int pageIndex, int pageSize, String nickname, String remarks, String userStatus);
	// 内部使用，查询所有的，每个人都要进行挨个通知
	List<WechatMsgReceiverEntity> queryAvailableReceivers();
	WechatMsgReceiverEntity queryReceiverByOpenid(String openid);
	void updateReceiver(WechatMsgReceiverEntity receiverEntity);
	void insertReceiver(WechatMsgReceiverEntity receiverEntity);
}