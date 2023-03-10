package com.ruqi.appserver.ruqi.utils;

import org.apache.commons.lang.StringUtils;

public class GeoStringBuilder  {
    public static final String SPLIT_FLAG=",";
    private StringBuilder inner;
    public GeoStringBuilder(){
        inner=new StringBuilder();
    }

    public GeoStringBuilder append(String attr){
        if (!StringUtils.isEmpty(inner.toString())&&!StringUtils.isEmpty(attr)){//ε¦ζδΉε
            inner.append(SPLIT_FLAG).append(attr);
        }else {
            inner.append(attr);
        }
        return this;

    }

    @Override
    public String toString() {
        return inner.toString();
    }
}
