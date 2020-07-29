package com.ruqi.appserver.ruqi.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.aliyuncs.utils.StringUtils;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Json工具类
 * fastjson  序列化成json的过程
 *  1.先查找共有域属性
 *  2.get和set方法，最后序列化
 *
 * @author lukeyan
 * @date 2019/4/3
 */
public class JsonUtil {

    private JsonUtil() {

    }

    /**
     * bean对象转换成JSON String
     *
     * @param src
     * @return String
     */
    public static String beanToJsonStr(Object src) {
        return JSON.toJSONString(src);
    }

    /**
     * String 转换成 bean
     *
     * @param tClass
     * @param value
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T jsonStrToBean(Class<T> tClass, String value) {
        return JSON.parseObject(value, tClass);
    }


    public static <T> T jsonToBeanByType(String value, TypeReference<T> tTypeReference) {
        return JSON.parseObject(value, tTypeReference);
    }

//
//    /**
//     * 获取key对应的值，这个值是基本数据类型，其中key对应的值不是json数组
//     *
//     * @param json
//     * @param key
//     * @param <T>
//     * @return
//     */
//    public static <T> T getSingleValueByKey(String json, String key) throws JSONException, IOException {
//        JSONObject jsonObject = new JSONObject(json);
//        return (T) jsonObject.get(key);
//    }


    public static <T> List<T> jsonStrToList(String json, Class clazz) {
        return JSON.parseArray(json, clazz);
    }

    public static String combineJsonStr(String jsonA, String jsonB) {
        if (StringUtils.isEmpty(jsonA)) {
            return jsonB;
        }
        if (StringUtils.isEmpty(jsonB)) {
            return jsonA;
        }
        try {
            com.alibaba.fastjson.JSONObject jsonObjectA = JSON.parseObject(jsonA);
            com.alibaba.fastjson.JSONObject jsonObjectB = JSON.parseObject(jsonB);
            for (Map.Entry<String, Object> entry : jsonObjectB.entrySet()) {
                jsonObjectA.put(entry.getKey(), entry.getValue());
            }
            return jsonObjectA.toJSONString();
        } catch (Exception e) {
            return "";
        }

    }
}
