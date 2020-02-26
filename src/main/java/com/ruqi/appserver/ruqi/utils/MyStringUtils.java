package com.ruqi.appserver.ruqi.utils;

public class MyStringUtils {
   public static boolean isEmpty(String str) {
       return null == str || str.trim().length() == 0;
   }
}