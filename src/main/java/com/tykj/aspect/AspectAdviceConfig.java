package com.tykj.aspect;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.wxaapp.WxaConfig;
import com.jfinal.wxaapp.WxaConfigKit;
import com.tykj.wx.entity.SysThirdReqLog;
import com.tykj.wx.properties.WxProperties;
import com.tykj.wx.service.ISysThirdReqLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.DateUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

/**
 * @author huran
 */
@Slf4j
@Aspect
@Component
public class AspectAdviceConfig {
    @Autowired
    ISysThirdReqLogService sysThirdReqLogService;
    @Autowired
    WxProperties wxProperties;

    @Pointcut("execution(* com.tykj.*.controller..*.*(..))")
    public void myPointcut() {
    }

    @Pointcut("execution(* com.tykj.*.controller..*.*(..))")
    public void webLog() {
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
    }

    /**
     * 声明环绕通知
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        log.info("开始拦截..............................");
        SysThirdReqLog thirdReqLog = combineRecord(pjp);
        long begin = System.nanoTime();
        Object o = null;
        try {
            o = pjp.proceed();
            long end = System.nanoTime();
            saveRecordBySuccess(thirdReqLog, String.valueOf((end - begin) / 1000000), o);
        } catch (Exception e) {
            e.printStackTrace();
            saveRecordByFailure(thirdReqLog, e);
        }

        return o;
    }

    /**
     * 保存调用失败的信息
     *
     * @param thirdReqLog
     * @param e
     */
    private void saveRecordByFailure(SysThirdReqLog thirdReqLog, Exception e) {

        thirdReqLog.setMsg("调用失败").setFlag("1").setErrMsg(e.getMessage());
        sysThirdReqLogService.save(thirdReqLog);
    }

    /**
     * 保存调用成功的信息
     *
     * @param thirdReqLog
     * @param time
     * @param o
     */
    private void saveRecordBySuccess(SysThirdReqLog thirdReqLog, String time, Object o) {
        thirdReqLog.setMsg("调用成功").setFlag("0").setComTime(time + "ms");
        if (o.toString().length() >= 2000) {
            thirdReqLog.setRetArgs(JSONObject.toJSONString(o).substring(0, 2000));
        } else {
            thirdReqLog.setRetArgs(JSONObject.toJSONString(o));
        }
        sysThirdReqLogService.save(thirdReqLog);

    }

    /**
     * 拼接消息
     *
     * @param pjp
     * @return
     */
    private SysThirdReqLog combineRecord(ProceedingJoinPoint pjp) {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        SysThirdReqLog thirdReqLog = new SysThirdReqLog();
        System.out.println(pjp.getArgs()[0]);
        Object[] args = pjp.getArgs();
        Object[] arguments = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof ServletRequest || args[i] instanceof ServletResponse || args[i] instanceof
                    MultipartFile) {
                continue;
            }
            arguments[i] = args[i];
        }
        String paramter = "";
        if (arguments != null) {
            try {
                paramter = JSONObject.toJSONString(arguments[0]);
            } catch (Exception e) {
                paramter = e.getMessage();
            }
        }
        thirdReqLog.setId(UUID.randomUUID().toString().replace("-", "")).setReqUrl(request.getRequestURI().toString()
        ).setClassMethod(pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName()).setReqTime
                (DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss")).setHttpMethod(request.getMethod()).setReqIp
                (request.getRemoteAddr()).setReqArgs(paramter).setErrMsg("0");
        return thirdReqLog;
    }
}
