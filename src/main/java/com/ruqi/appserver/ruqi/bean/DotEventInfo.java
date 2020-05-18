package com.ruqi.appserver.ruqi.bean;

/**
 * @author ZhangYu
 * @date 2020/5/11
 * @desc 推荐上车点降级生效记录
 */
public class DotEventInfo extends BaseRecordInfo {
    /**
     * 降级生效key。前置条件：滴滴推荐点失败。顺序执行。
     * FALLBACK_SUCCESS_TX_RECOMMEND 腾讯推荐点生效
     * FALLBACK_SUCCESS_TX_GEO 腾讯GEO点生效
     * FALLBACK_SUCCESS_RUQI_APP 如祺缓存解析点生效
     *
     * 事件埋点记录统计KEY。
     * EVENT_MULIT_ORDER 乘客端，多开，且订单创建成功，无需预付费。eventDetail中记录订单ID。
     */
    public String eventKey;
    public String eventDetail;
    public String ext;
}
