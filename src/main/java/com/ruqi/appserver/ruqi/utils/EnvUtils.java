package com.ruqi.appserver.ruqi.utils;

import org.springframework.beans.factory.annotation.Value;

public class EnvUtils {
    @Value("${spring.profiles.active}")
    private static String mEnv = "";

    private static final String ENV_DEV = "dev";
    private static final String ENV_TEST = "test";
    private static final String ENV_PROD = "prod";

    public static boolean isEnvProd() {
        return MyStringUtils.isEqueals(ENV_PROD, mEnv);
    }
}
