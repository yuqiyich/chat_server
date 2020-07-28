package com.ruqi.appserver.ruqi.request;

/**
 * Author:liangbingkun
 * Time:2020/7/22
 * Description: 明文请求实体
 */
public class SignRequest extends BaseRequest{
    private String aesKey;

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public String getAesKey() {
        return aesKey;
    }
}
