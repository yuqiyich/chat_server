package com.ruqi.appserver.ruqi.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Author:liangbingkun
 * Time:2020/8/25
 * Description: 城市名、区名
 */
public class CityUtil {
    private static Map<String, String> nameMap = new HashMap<>();
    private static Map<String, String> centerLnglatMap = new HashMap<>();

    static {
        nameMap.put("440100", "广州市");
        nameMap.put("440117", "从化区");
        nameMap.put("440115", "南沙区");
        nameMap.put("440114", "花都区");
        nameMap.put("440111", "白云区");
        nameMap.put("440113", "番禺区");
        nameMap.put("440103", "荔湾区");
        nameMap.put("440105", "海珠区");
        nameMap.put("440118", "增城区");
        nameMap.put("440104", "越秀区");
        nameMap.put("440112", "黄埔区");
        nameMap.put("440106", "天河区");

        nameMap.put("440600", "佛山市");
        nameMap.put("440607", "三水区");
        nameMap.put("440608", "高明区");
        nameMap.put("440606", "顺德区");
        nameMap.put("440605", "南海区");
        nameMap.put("440604", "禅城区");


        centerLnglatMap.put("440100", "113.280637,23.125178");
        centerLnglatMap.put("440117", "113.587386,23.545283");
        centerLnglatMap.put("440115", "113.53738,22.794531");
        centerLnglatMap.put("440114", "113.211184,23.39205");
        centerLnglatMap.put("440111", "113.262831,23.162281");
        centerLnglatMap.put("440113", "113.364619,22.938582");
        centerLnglatMap.put("440103", "113.243038,23.124943");
        centerLnglatMap.put("440105", "113.262008,23.103131");
        centerLnglatMap.put("440118", "113.829579,23.290497");
        centerLnglatMap.put("440104", "113.280714,23.125624");
        centerLnglatMap.put("440112", "113.450761,23.103239");
        centerLnglatMap.put("440106", "113.335367,23.13559");

        centerLnglatMap.put("440600", "113.122717,23.028762");
        centerLnglatMap.put("440607", "112.899414,23.16504");
        centerLnglatMap.put("440608", "112.882123,22.893855");
        centerLnglatMap.put("440606", "113.281826,22.75851");
        centerLnglatMap.put("440605", "113.145577,23.031562");
        centerLnglatMap.put("440604", "113.112414,23.019643");
    }

    public static String getCityName(String cityCode) {
        if (nameMap.containsKey(cityCode)) {
            return nameMap.get(cityCode);
        } else {
            return "未知";
        }
    }

    public static String getCenterLngLat(String cityCode) {
        if (centerLnglatMap.containsKey(cityCode)) {
            return centerLnglatMap.get(cityCode);
        } else {
            return "未知";
        }
    }
}
