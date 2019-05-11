package com.tykj.aspect;

import com.jfinal.wxaapp.WxaConfig;
import com.jfinal.wxaapp.WxaConfigKit;
import com.tykj.wx.properties.WxProperties;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author  huran
 */
@Slf4j
@Aspect
@Component
public class AspectAdviceConfig {
    @Autowired
    WxProperties wxProperties;

    @Pointcut("execution(* com.tykj.*.controller..*.*(..))")
    public void myPointcut() {
    }

    /**
     * 前置通知，在目标方法完成之后调用通知，此时不会关 心方法的输出是什么
     */
    @Before("myPointcut()")
    public void beforeAdvice() {
        WxaConfig wxaConfig = new WxaConfig();
        wxaConfig.setAppId(wxProperties.getAppId());
        wxaConfig.setAppSecret(wxProperties.getAppSecret());
        WxaConfigKit.setWxaConfig(wxaConfig);
        System.out.println("Before--通知方法会在目标方法调用之前执行");
    }
}
