package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.geomesa.db.GeoDbHandler;
import com.ruqi.appserver.ruqi.utils.EnvUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 *
 * 初始化配置
 *
 */
@Component
public class GaiaInitializer  implements CommandLineRunner {

    @Value("${spring.profiles.active}")
    private String mEnv = "";
    public static Boolean DEBUG=false;
    @Override
    public void run(String... args) throws Exception {
        DEBUG=!EnvUtils.isEnvProd(mEnv);
        GeoDbHandler.setDebug(DEBUG);//开发环境打开db调试
    }
}
