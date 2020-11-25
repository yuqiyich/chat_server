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
     * <p>
     * 事件埋点记录统计KEY。
     * EVENT_MULIT_ORDER 乘客端，多开，且订单创建成功，无需预付费。eventDetail中记录订单ID。
     */
    public String eventKey;
    public String eventDetail;
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

    public long userId;
}
