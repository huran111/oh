package com.tykj.config;

import com.tykj.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author huran
 */
@Configuration
public class MyWebAppConfigurer extends WebMvcConfigurerAdapter {
    /**
     * 注入拦截器bean
     */
    @Bean
    LoginInterceptor webHandlerInterceptor(){
        return  new LoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加登陆拦截器
        registry.addInterceptor(new LoginInterceptor())
                //.excludePathPatterns("/", "/rest/wx/login", "/rest/wx/logout", "/rest/wx/**")
                .excludePathPatterns("/")
                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/error", "/swagger-ui.html/**");

        super.addInterceptors(registry);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");

        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }


}
