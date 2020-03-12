package com.ruqi.appserver.ruqi.config;

import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
//@EnableAutoConfiguration
//@Profile({"dev", "test"})//在生产环境不开启
//@Profile("dev")//在生产环境不开启
//@ConditionalOnProperty(name = "swagger.enable", havingValue = "true")
public class SwaggerConfig {
    @Value("${swagger.enable:false}")
    private boolean mSwaggerEnable = false;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(mSwaggerEnable)
                .pathMapping("/")
                .select()
                // 目录下全部生产文档
                .apis(RequestHandlerSelectors.any())// 对所有api进行监控
                .apis(RequestHandlerSelectors.basePackage("com.ruqi.appserver.ruqi.controller"))
                .paths(PathSelectors.regex("/.*"))// 对根下所有路径进行监控
                //不显示错误的接口地址
                .paths(Predicates.not(PathSelectors.regex("/error.*")))//错误路径不监控
//                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("APP记录平台接口文档")
                .description("安卓端数据上报")
                .termsOfServiceUrl("")
                .version("1.0")
                .build();
    }
}