package com.ruqi.appserver.ruqi.utils;

import java.util.ArrayList;
import java.util.Collections;

public class WxUtils {
    public static final String TOKEN = "ruQi2020";

    public static String checkSignature(String str) {
        String[] content = str.split("&");
        String signature = content[0].split("=")[1];
        String timestamp = content[2].split("=")[1];
        String nonce = content[3].split("=")[1];

        ArrayList<String> list = new ArrayList<String>();
        list.add(nonce);
        list.add(timestamp);
        list.add(TOKEN);

        //字典序排序
        Collections.sort(list);
        //SHA1加密
        String checksignature = SHA1Util.encode(list.get(0) + list.get(1) + list.get(2));
        System.out.println(signature);
        System.out.println(checksignature);

        if (checksignature.equals(signature)) {
            return content[1].split("=")[1];
        }
        return null;
    }
}
