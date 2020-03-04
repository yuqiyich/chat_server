package com.ruqi.appserver.ruqi.bean;

import java.util.List;

public class BasePageBean<T> {
    // 页码从0开始
    public int pageIndex;
    // 每页数量
    public int pageSize;
    // 总数量
    public long totalSize;
    public List<T> list;

    public BasePageBean() {
    }

    public BasePageBean(int pageIndex, int pageSize, long totalSize, List<T> list) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.totalSize = totalSize;
        this.list = list;
    }
}