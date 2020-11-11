package com.ruqi.appserver.ruqi.utils;

import com.ruqi.appserver.ruqi.service.PointSaveConsumer;

public class ThreadUtils {
    public static int  getAllThreadNum(){
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        ThreadGroup topGroup = group;
        // 遍历线程组树，获取根线程组
        while (group != null) {
            topGroup = group;
            group = group.getParent();
        }
        // 激活的线程数再加一倍，防止枚举时有可能刚好有动态线程生成
        int slackSize = topGroup.activeCount() * 2;
        Thread[] slackThreads = new Thread[slackSize];
        // 获取根线程组下的所有线程，返回的actualSize便是最终的线程数
        int actualSize = topGroup.enumerate(slackThreads);
        Thread[] atualThreads = new Thread[actualSize];
        // 复制slackThreads中有效的值到atualThreads
        System.arraycopy(slackThreads, 0, atualThreads, 0, actualSize);
//        if (atualThreads.length > 3600 &&atualThreads.length <3650) {
//            for (Thread thread : atualThreads) {
//                System.out.println("Thread group:" + thread.getThreadGroup().getName() + "Thread name : " + thread.getName());
//            }
//        }

        return atualThreads.length;
    }
}