package com.ruqi.appserver.ruqi.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class HeaderMapUtils {
    public static Map<String, String> getAllHeaderParamMaps(HttpServletRequest request) {
        Map<String, String> resultMap = new HashMap<>();
        if (null != request) {
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                resultMap.put(headerName, request.getHeader(headerName));
            }
        }
        return resultMap;
    }
}
