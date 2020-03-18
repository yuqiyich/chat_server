package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.dao.entity.WechatMsgEntity;
import com.ruqi.appserver.ruqi.dao.mappers.WechatMsgMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WechatMsgService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private WechatMsgMapper wechatMsgMapper;

    /**
     * 添加一条微信消息记录
     */
    public void addWechatMsgRecord(WechatMsgEntity msgEntity) {
        wechatMsgMapper.insertMsgRecord(msgEntity);
    }

    public long queryMsgSize(String openid, String details, String remark, String result, String startTime, String endTime) {
        return wechatMsgMapper.queryMsgSize(openid, details, remark, result, startTime, endTime);
    }

    public List<WechatMsgEntity> queryMsgList(int pageIndex, int pageSize, String openid, String details, String remark, String result, String startTime, String endTime) {
        return wechatMsgMapper.queryMsgList(pageIndex, pageSize, openid, details, remark, result, startTime, endTime);
    }

    public void updateMsgRemark(WechatMsgEntity wechatMsgEntity) {
        wechatMsgMapper.updateMsgRemark(wechatMsgEntity);
    }
}