package com.example.config;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.serializer.impl.SaSerializerTemplateForJdkUseBase64;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SaTokenConfigure implements WebMvcConfigurer {

    private final WhitelistConfig whitelistConfig;

    @Value("${file.path}")
    private String filePath;

    @Value("${file.url-prefix}")
    private String urlPrefix;

    @PostConstruct
    public void rewriteComponent() {
        SaManager.setSaSerializerTemplate(new SaSerializerTemplateForJdkUseBase64());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> {
                    SaRouter.match("/**", r -> {
                        List<String> whitelist = whitelistConfig.getWhitelist();
                        SaRouter.match("/**")
                                .notMatch(whitelist.toArray(new String[0]));
                    });
                }))
                .addPathPatterns("/**")
                // 把AI模块的路径进行放行，登录校验在方法中进行处理
                .excludePathPatterns("/book/ai/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler(urlPrefix + "**").addResourceLocations("file:" + filePath);
    }
}
