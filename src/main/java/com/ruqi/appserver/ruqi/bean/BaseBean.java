package com.ruqi.appserver.ruqi.bean;

public class BaseBean<T> {
    // 0表示成功，非0则表示不同的错误
    public int errorCode = 0;
    public String errorMsg = "";
    public T data;
}