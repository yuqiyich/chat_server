package com.ruqi.appserver.ruqi.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class HeaderMapUtils {
    private static final List<String> sInfoHeaderNameList = new ArrayList<String>() {
        {
            add("x-real-ip");
            add("user-agent");
            add("app_key");
        }
    };

    public static Map<String, String> getAllHeaderParamMaps(HttpServletRequest request) {
        Map<String, String> resultMap = new HashMap<>();
        if (null != request) {
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
//                if (sInfoHeaderNameList.contains(headerName)) {
                    resultMap.put(headerName, request.getHeader(headerName));
//                }
            }
        }
        return resultMap;
    }
}
