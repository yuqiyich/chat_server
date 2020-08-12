//package com.ruqi.appserver.ruqi.service;
//
//import com.ruqi.appserver.ruqi.dao.entity.WechatAccessTokenEntity;
//import com.ruqi.appserver.ruqi.dao.entity.WechatMsgReceiverEntity;
//import com.ruqi.appserver.ruqi.dao.mappers.WechatMapper;
//import com.ruqi.appserver.ruqi.utils.EncryptUtils;
//import com.ruqi.appserver.ruqi.utils.MyStringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//// TODO: 2020/3/16 多线程情况下，分布式情况下，可能存在同时多个进行请求token和保存到数据库的操作。
//@Service
//public class WechatService {
//    private Logger logger = LoggerFactory.getLogger(getClass());
//
//    @Autowired
//    private WechatMapper wechatMapper;
//
//    private WechatAccessTokenEntity mWechatAccessTokenEntity = null;
//
//    /**
//     * 已过期或者token为空，返回空。后续需要进行重新api请求获取和保存。
//     * tips：可能存在其他地方也获取token，导致这里的token实际已过期，则在发送消息时会失败，暂不处理。
//     *
//     * @return
//     */
//    public String queryAccessToken() {
//        // 本地有则直接使用，否则数据库拿。
//        if (null != mWechatAccessTokenEntity &&
//                mWechatAccessTokenEntity.expiresTime.getTime() > System.currentTimeMillis()) {
//            logger.info("--->本地token");
//            return mWechatAccessTokenEntity.accessToken;
//        }
//
//        List<WechatAccessTokenEntity> dataList = wechatMapper.queryWechatAccessToken();
//        // System.out.println("--->dataList=" + JSON.toJSONString(dataList));
//        if (null != dataList && dataList.size() > 0) {
//            mWechatAccessTokenEntity = dataList.get(0);
//        }
//        if (null != mWechatAccessTokenEntity &&
//                mWechatAccessTokenEntity.expiresTime.getTime() > System.currentTimeMillis()) {
//            logger.info("--->数据库token");
//            mWechatAccessTokenEntity.accessToken = EncryptUtils.decode(mWechatAccessTokenEntity.accessToken);
//            return mWechatAccessTokenEntity.accessToken;
//        } else {
//            return "";
//        }
//    }
//
//    /**
//     * 更新token，更改本地值。更新到数据库。
//     *
//     * @param entity
//     */
//    public void updateAccessToken(WechatAccessTokenEntity entity) {
//        if (null != entity) {
//            mWechatAccessTokenEntity = entity;
//
//            WechatAccessTokenEntity entitySave = new WechatAccessTokenEntity();
//            entitySave.expiresTime = entity.expiresTime;
//            entitySave.accessToken = EncryptUtils.encode(entity.accessToken);
//
//            // 先清除数据库，再插入这条数据
//            wechatMapper.clearDatas();
//            wechatMapper.insertData(entitySave);
//        }
//    }
//
//    /**
//     * 绑定
//     *
//     * @param nickname
//     * @param openId
//     */
//    public void bindReceiver(String openId, String nickname) {
//        if (MyStringUtils.isEmpty(openId)) {
//            return;
//        }
//
//        openId = EncryptUtils.encode(openId);
//
//        // 先查询数据库是否有数据，有的话设为绑定状态即可；不存在则新增一个数据。
//        WechatMsgReceiverEntity receiverEntity = wechatMapper.queryReceiverByOpenid(openId);
//        // System.out.println("--->queryReceiverByOpenid=" + JSON.toJSONString(receiverEntity));
//        if (null != receiverEntity) {
//            logger.info("--->bindReceiver exits userStatus=" + receiverEntity.userStatus);
//            if (receiverEntity.userStatus != 1) {
//                receiverEntity.nickname = nickname;
//                receiverEntity.userStatus = 1;
//                wechatMapper.updateReceiver(receiverEntity);
//            }
//        } else {
//            logger.info("--->bindReceiver not exits insert");
//            receiverEntity = new WechatMsgReceiverEntity();
//            receiverEntity.openid = openId;
//            receiverEntity.nickname = nickname;
//            wechatMapper.insertReceiver(receiverEntity);
//        }
//    }
//
//    /**
//     * 更新
//     */
//    public void updateReceiver(WechatMsgReceiverEntity receiverEntity) {
//        if (null == receiverEntity || receiverEntity.id <= 0) {
//            logger.info("--->updateReceiver receiverEntity is null or id invalid");
//            return;
//        }
//
//        wechatMapper.updateReceiver(receiverEntity);
//    }
//
//    public long queryReceiverSize(String nickname, String remarks, String userStatus) {
//        return wechatMapper.queryReceiverSize(nickname, remarks, userStatus);
//    }
//
//    public List<WechatMsgReceiverEntity> queryReceivers(int pageIndex, int pageSize, String nickname, String remarks, String userStatus) {
//        return wechatMapper.queryReceivers(pageIndex, pageSize, nickname, remarks, userStatus);
//    }
//
//    public List<WechatMsgReceiverEntity> queryAvailableReceivers() {
//        return wechatMapper.queryAvailableReceivers();
//    }
//
//}
