package com.ruqi.appserver.ruqi.dao.mappers;

import java.util.List;

import com.ruqi.appserver.ruqi.dao.entity.WechatAccessTokenEntity;

import org.springframework.stereotype.Repository;

@Repository
public interface WechatMapper {
	List<WechatAccessTokenEntity> queryWechatAccessToken();
}