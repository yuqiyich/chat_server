package com.ruqi.appserver.ruqi.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Author:liangbingkun
 * Time:2020/8/25
 * Description:
 */
public class CityUtil {
    private static Map<String, String> map = new HashMap<>();
    static {
        map.put("440100", "广州市");
        map.put("440600", "佛山市");
    }

    public static String getCityName(String cityCode){
        return map.get(cityCode);
    }
}
