package com.tykj.interceptor;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.cache.IAccessTokenCache;
import com.tykj.common.SysConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStreamWriter;

/**
 * 登陆拦截器
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    /**
     * 访问controller之前被调用
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
      /*  System.out.println(">>>>>>>>>>在请求处理之前进行调用（Controller方法调用之前）");
        System.out.println(request.getMethod());
        System.out.println(request.getRequestURI());
        System.out.println(request.getRequestURL());

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
        }*/
        log.info("======================>>>拦截开始..............");
        return true;
    }

    /**
     * 访问controller之后 访问视图之前被调用
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 访问视图之后被调用
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param e
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }

}
