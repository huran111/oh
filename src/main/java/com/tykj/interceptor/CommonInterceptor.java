package com.tykj.interceptor;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.cache.IAccessTokenCache;
import com.tykj.common.SysConstant;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStreamWriter;

@Component
public class CommonInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String sessionId = request.getParameter(SysConstant.SESSION_KEY);
        IAccessTokenCache accessTokenCache = ApiConfigKit.getAccessTokenCache();
        String sessionJson = accessTokenCache.get("wxa:session:" + sessionId);
        if (StringUtils.isEmpty(sessionJson)) {
            System.out.println("getContextPath:" + request.getContextPath());
            ServletOutputStream out = response.getOutputStream();
            OutputStreamWriter ow = new OutputStreamWriter(out, "UTF-8");
            ow.write(SysConstant.NOT_LOGIN);
            ow.flush();
            ow.close();
            // response.sendRedirect(request.getContextPath()+"/");
            return false;
        }
        return true;
    }
}
