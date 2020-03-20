package com.ruqi.appserver.ruqi.dao.mappers;

import com.ruqi.appserver.ruqi.dao.entity.WechatMsgEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WechatMsgMapper {
    // 新增微信消息记录
    void insertMsgRecord(WechatMsgEntity msgEntity);

    // 微信消息总数目
    long queryMsgSize(String openid, String msgid, String details, String remark, String result, String startTime, String endTime);

    // 查询微信消息列表
    List<WechatMsgEntity> queryMsgList(int pageIndex, int pageSize, String openid, String msgid, String details, String remark, String result, String startTime, String endTime);

    // 更新设置消息的备注
    long updateMsgRemark(WechatMsgEntity wechatMsgEntity);
}