package com.ruqi.appserver.ruqi.bean;

import java.util.Map;

/**
 * @author ZhangYu
 * @date 2020/5/11
 * @desc 推荐上车点降级生效记录
 */
public class UploadDotEventInfo extends BaseUploadRecordInfo {
    public static final String NAME_USER_TYPE = "userType";

    /**
     * 上车点降级生效key。前置条件：滴滴推荐点失败。顺序执行。
     * FALLBACK_SUCCESS_TX_RECOMMEND 腾讯推荐点生效
     * FALLBACK_SUCCESS_TX_GEO 腾讯GEO点生效
     * FALLBACK_SUCCESS_RUQI_GEO APP本地GEO兜底
     * FALLBACK_SUCCESS_RUQI_RECOMMEND APP本地推荐点兜底
     * 上面4个KEY之一 兜底订单关联
     * FALLBACK_FAIL_BOARDING_POINT 兜底失败
     * <p>
     * 定位点降级生效。
     * FALLBACK_SUCCESS_TX_HISTORY_LOCATION SDK缓存兜底
     * FALLBACK_SUCCESS_TX_DEVICE_LOCATION 原生实时兜底
     * FALLBACK_SUCCESS_DEVICE_HISTORY_LOCATION 原生缓存兜底
     * FALLBACK_SUCCESS_APP_HISTORY_LOCATION APP缓存兜底
     * FALLBACK_FAIL_LOCATION 兜底失败
     * <p>
     * 算路降级
     * FALLBACK_SUCCESS_TX_ROUTE_RETRY 重试兜底
     * FALLBACK_SUCCESS_TX_HISTORY_CACHE 缓存兜底
     * FALLBACK_SUCCESS_ROUTE_GAODE 高德兜底
     * FALLBACK_SUCCESS_ROUTE_BAIDU 百度兜底
     * FALLBACK_SUCCESS_ROUTE_TXMAP 腾讯地图兜底
     * FALLBACK_FAIL_ROUTE 兜底失败
     * <p>
     * 事件埋点记录统计KEY。
     * EVENT_MULIT_ORDER 乘客端，多开，且订单创建成功，无需预付费。eventDetail中记录订单ID。
     */
    public String eventKey;
    public String eventDetail;
    public String ext;
    /**
     * 订单id
     */
    public String orderId;
    /**
     * 场景
     * 1：接驾
     * 2：送驾
     */
    public String scene;

    /**
     * 以下额外数据在这里取值
     */
    public Map<String, Object> eventData;
    /**
     * 用户类型 可见用户类型. 1:APP通用 2:未登录用户 3:专车用户 4:顺风车乘客 5:顺风车车主
     */
    public int userType;
}
