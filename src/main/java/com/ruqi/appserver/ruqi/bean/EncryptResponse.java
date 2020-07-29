package com.ruqi.appserver.ruqi.bean;

public class EncryptResponse {

    public EncryptResponse(boolean encryData){
        if(encryData){
            this.encryptFlag = 1;
        }
    }
    public String data = "";
    /**
     * 默认0代表不加密
     */
    public int encryptFlag;
}