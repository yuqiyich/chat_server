package com.ruqi.appserver.ruqi.request;

/**
 * Author:liangbingkun
 * Time:2020/7/22
 * Description: 密文请求实体
 */
public class EncryptBaseRequest extends BaseRequest{
    private String req;
    private String sign;

    public void setSign(String sign) {
        this.sign = sign;
    }

    public void setReq(String req) {
        this.req = req;
    }

    public String getSign() {
        return sign;
    }

    public String getReq() {
        return req;
    }
}
