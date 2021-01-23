package com.ruqi.appserver.ruqi.utils;

import com.ruqi.appserver.ruqi.bean.response.EventTypeKeyListResp;
import org.springframework.util.CollectionUtils;

/**
 * @author ZhangYu
 * @date 2020/10/16
 * @desc 埋点表的EventType、EventKey对应关系
 */
public class DotEventDataUtils {
    private static DotEventDataUtils instance;

    private DotEventDataUtils() {
    }

    public static DotEventDataUtils getInstance() { // 1
        if (instance == null) { // 2:第一次检查
            synchronized (DotEventDataUtils.class) { // 3:加锁
                if (instance == null) // 4:第二次检查
                    instance = new DotEventDataUtils(); // 5:问题的根源出在这里
            }
        }
        return instance;
    }

    // 拼接类型匹配的key的查询。
    public String getSqlStr(String eventType, EventTypeKeyListResp eventTypeKeyListResp) {
        if (null != eventTypeKeyListResp && !CollectionUtils.isEmpty(eventTypeKeyListResp.eventTypes)) {
            for (EventTypeKeyListResp.EventType type : eventTypeKeyListResp.eventTypes) {
                if (MyStringUtils.isEqueals(type.typeKey, eventType)) {
                    if (!CollectionUtils.isEmpty(type.eventKeys)) {
                        StringBuilder sb = new StringBuilder();
                        for (EventTypeKeyListResp.EventKey key : type.eventKeys) {
                            sb.append("event_key='" + key.eventKey + "' or ");
                        }
                        if (sb.length() > 4) {
                            return sb.substring(0, sb.length() - 4);
                        }
                    } else {
                        return null;
                    }
                }
            }
        }
        return null;
    }
}
