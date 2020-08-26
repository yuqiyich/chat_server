package com.ruqi.appserver.ruqi.utils;

public class MyStringUtils {
    public static boolean isEmpty(String str) {
        return null == str || str.trim().length() == 0;
    }

    public static boolean isEqueals(String a, String b) {
        if (a == b) return true;
        int length;
        if (a != null && b != null && (length = a.length()) == b.length()) {
            if (a instanceof String && b instanceof String) {
                return a.equals(b);
            } else {
                for (int i = 0; i < length; i++) {
                    if (a.charAt(i) != b.charAt(i)) return false;
                }
                return true;
            }
        }
        return false;
    }
}
