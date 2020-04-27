package com.ruqi.appserver.ruqi.config;

import com.ruqi.appserver.ruqi.interceptor.APIInterceptor;
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

    @Bean //将自定义拦截器注册到spring bean中
    public APIInterceptor APIInterceptor() {
        return new APIInterceptor();
    }

    /**
     * 注册拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // html网页，拦截处理需要登录有token
        registry.addInterceptor(UrlInterceptor()).addPathPatterns("/**/*.html")
                .excludePathPatterns("/error.html")
        ;

        // 非html网页，就是API接口。拦截处理没有token时统一返回token无效
        registry.addInterceptor(APIInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/**/*.html", "/", "/error", "/csrf")
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg")
                .excludePathPatterns("/css/**", "/js/**", "/img/**")//排除样式、脚本、图片等资源文件
                .excludePathPatterns("/record/uploadData") // 排除安全上报接口
                .excludePathPatterns("/user/login") // 排除登录接口
                .excludePathPatterns("/wechatH5/authorize", "/wechatH5/redirectUri") // 微信公众号需要
        ;
    }
}
