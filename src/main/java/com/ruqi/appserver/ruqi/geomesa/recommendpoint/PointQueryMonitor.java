package com.ruqi.appserver.ruqi.geomesa.recommendpoint;

import com.ruqi.appserver.ruqi.geomesa.RPHandleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 性能的监控
 *
 */
public class PointQueryMonitor {
    private static Logger logger = LoggerFactory.getLogger(RPHandleManager.class);
    private static String LOG_PRIFIX="pointquerymonitor";
    /**
     * 是否开启日志
     */
    private static boolean isLogOpen = true;

    public  static int  LOG_LEVEL_I=0;
    public  static int  LOG_LEVEL_D=1;
    public  static int  LOG_LEVEL_W=2;
    public  static int  LOG_LEVEL_E=3;

    public static void i(String msg){
        if (isLogOpen)logger.info("["+LOG_PRIFIX+"]"+msg);
    }

    public static void w(String msg){
        if (isLogOpen)logger.warn("["+LOG_PRIFIX+"]"+msg);
    }

    public static void e(String msg){
        if (isLogOpen)logger.error("["+LOG_PRIFIX+"]"+msg);
    }

    public static void d(String msg){
        if (isLogOpen)logger.debug("["+LOG_PRIFIX+"]"+msg);
    }

}
