package com.ruqi.appserver.ruqi.utils;

import sun.security.provider.MD5;

public class GenerateAppKeyUtil {
    public static final String  salt="2020*.ruqi#!888";
    public static String appName= "com.ruqi.travel";
//    public static String appName= "com.tencent.gac.driver";
    public static void main(String[] args) {
        System.out.println("app packagename:"+appName+"=====appkey:"+Md5Util.md5StrBySalt(appName,salt).substring(0,16));

    }
}
