package com.ruqi.appserver.ruqi.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author ZhangYu
 * @date 2020/10/16
 * @desc 埋点表的EventType、EventKey对应关系
 */
public class DotEventDataUtils {
    private Map<String, Set<String>> dotEventDataMap = new HashMap<>();

    private static DotEventDataUtils instance;

    private DotEventDataUtils() {
        Set<String> navKeySet = new HashSet<>();
        navKeySet.add("FALLBACK_SUCCESS_TX_ROUTE_RETRY");
        navKeySet.add("FALLBACK_SUCCESS_TX_ROUTE_CACHE");
        navKeySet.add("FALLBACK_SUCCESS_ROUTE_GAODE");
        navKeySet.add("FALLBACK_SUCCESS_ROUTE_BAIDU");
        navKeySet.add("FALLBACK_SUCCESS_ROUTE_TENCENT");
        navKeySet.add("FALLBACK_FAIL_ROUTE");
        dotEventDataMap.put("nav", navKeySet);

        Set<String> recmdPointKeySet = new HashSet<>();
        recmdPointKeySet.add("FALLBACK_SUCCESS_TX_RECOMMEND");
        recmdPointKeySet.add("FALLBACK_SUCCESS_TX_GEO");
        recmdPointKeySet.add("FALLBACK_SUCCESS_RUQI_GEO");
        recmdPointKeySet.add("FALLBACK_SUCCESS_RUQI_RECOMMEND");
        recmdPointKeySet.add("FALLBACK_FAIL_BOARDING_POINT");
        recmdPointKeySet.add("FALLBACK_SUCCESS_GAIA_RECOMMEND");
        dotEventDataMap.put("recmdPoint", recmdPointKeySet);

        Set<String> driverLocationKeySet = new HashSet<>();
        driverLocationKeySet.add("FALLBACK_SUCCESS_TX_HISTORY_LOCATION");
        driverLocationKeySet.add("FALLBACK_SUCCESS_TX_DEVICE_LOCATION");
        driverLocationKeySet.add("FALLBACK_SUCCESS_DEVICE_HISTORY_LOCATION");
        driverLocationKeySet.add("FALLBACK_SUCCESS_APP_HISTORY_LOCATION");
        driverLocationKeySet.add("FALLBACK_FAIL_LOCATION");
        dotEventDataMap.put("driverLocation", driverLocationKeySet);

        Set<String> endPointKeySet = new HashSet<>();
        endPointKeySet.add("FB_SUC_DES_POP_SHOW");
        endPointKeySet.add("FB_SUC_DES_POP_SELECT");
        endPointKeySet.add("FB_SUC_DES_POP_ORDER");
        endPointKeySet.add("FB_SUC_DES_LIST_SHOW");
        endPointKeySet.add("FB_SUC_DES_LIST_SELECT");
        endPointKeySet.add("FB_SUC_DES_LIST_ORDER");
        dotEventDataMap.put("endPoint", endPointKeySet);

        Set<String> h5hybridKeySet = new HashSet<>();
        h5hybridKeySet.add("H5_HYBRID_LOAD_SUCCESS");
        h5hybridKeySet.add("H5_HYBRID_LOAD_FAIL");
        h5hybridKeySet.add("H5_HYBRID_RELOAD_SUCCESS");
        h5hybridKeySet.add("H5_HYBRID_RELOAD_FAIL");
        dotEventDataMap.put("h5hybrid", h5hybridKeySet);

        Set<String> voiceDialKeySet = new HashSet<>();
        voiceDialKeySet.add("VOICE_CALL_START_CALL");
        voiceDialKeySet.add("VOICE_CALL_CALLER_CONNECT_FAIL");
        voiceDialKeySet.add("VOICE_CALL_CALLER_CONNECT_SUCCESS");
        voiceDialKeySet.add("VOICE_CALL_CALLER_SEND_MSG");
        voiceDialKeySet.add("VOICE_CALL_RECEIVER_GOT_MSG");
        voiceDialKeySet.add("VOICE_CALL_RECEIVER_JOIN_CHANNEL");
        voiceDialKeySet.add("VOICE_CALL_RECEIVER_CONNECT_SUCCESS");
        voiceDialKeySet.add("VOICE_CALL_RECEIVER_CONNECT_FAIL");
        voiceDialKeySet.add("VOICE_CALL_CALLER_FAIL_MICROPHONE_NOT_ALLOW");
        voiceDialKeySet.add("VOICE_CALL_RECEIVER_FAIL_MICROPHONE_NOT_ALLOW");
        dotEventDataMap.put("voiceDial", voiceDialKeySet);

        Set<String> adKeySet = new HashSet<>();
        adKeySet.add("FULL_LINK_AD_CLICK");
        adKeySet.add("FULL_LINK_AD_EXPOSURE");
        dotEventDataMap.put("ad", adKeySet);
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

    public String getSqlStr(String eventType) {
//        System.out.println("--->eventType:" + eventType);
        if (dotEventDataMap.containsKey(eventType)) {
            Set<String> keySet = dotEventDataMap.get(eventType);
//            System.out.println("--->keySet:" + keySet);
            StringBuilder sb = new StringBuilder();
            for (String key : keySet) {
                sb.append("event_key='" + key + "' or ");
            }
            if (sb.length() > 4) {
                return sb.substring(0, sb.length() - 4);
            }
        }
        return null;
    }
}
