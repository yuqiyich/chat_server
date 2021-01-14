package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.*;
import com.ruqi.appserver.ruqi.request.*;

/**
 * sentry日志配置的服务
 */
public interface ISentryConfigService {

    /**
     * 查询sentry日志配置
     *
     * @param sentryConfigRequest
     * @return SentryConfigEntity
     */
    SentryConfigEntity querySentryConfig(SentryConfigRequest sentryConfigRequest);

}
