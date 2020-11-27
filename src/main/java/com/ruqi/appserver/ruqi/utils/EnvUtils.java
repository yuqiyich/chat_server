package com.ruqi.appserver.ruqi.utils;

import org.springframework.beans.factory.annotation.Value;

public class EnvUtils {
    public static final int APP_DRIVER = 1;//司机端应用id
    public static final int APP_CLIENT = 2;//乘客端应用id
    public static final int APP_CLIENT_DEV = 3;//乘客端DEV应用id
    public static final int APP_DRIVER_DEV = 4;//司机端DEV应用id

    private static final String APP_ENV_DEV = "dev";
    private static final String APP_ENV_PRO = "prod";

    @Value("${spring.profiles.active}")
    private static String mEnv = "";

    private static final String ENV_DEV = "dev";
    private static final String ENV_TEST = "test";
    private static final String ENV_PROD = "prod";

    public static boolean isEnvProd() {
        return MyStringUtils.isEqueals(ENV_PROD, mEnv);
    }

    public static boolean isEnvProd(String evn) {
        return MyStringUtils.isEqueals(ENV_PROD, evn);
    }

    public static String getAppEnvStr(int appId) {
        if (APP_DRIVER == appId || APP_CLIENT == appId) {
            return APP_ENV_PRO;
        } else {
            return APP_ENV_DEV;
        }
    }
}
