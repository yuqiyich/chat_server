package com.ruqi.appserver.ruqi.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {
    public static String getCurrentTime() {
        Date t = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(t);
    }
}