package com.ruqi.appserver.ruqi.config;

import com.ruqi.appserver.ruqi.interceptor.UrlInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyWebAppConfigurer implements WebMvcConfigurer {
    @Bean //将自定义拦截器注册到spring bean中
    public UrlInterceptor UrlInterceptor() {
        return new UrlInterceptor();
    }

    /**
     * 注册拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(UrlInterceptor()).addPathPatterns("/swagger-ui.html");
    }
}
