package com.tykj.interceptor;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.cache.IAccessTokenCache;
import com.tykj.common.SysConstant;
import com.tykj.wx.entity.InvalidQrparam;
import com.tykj.wx.service.IInvalidQrparamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private IInvalidQrparamService iInvalidQrparamService;

    /**
     * 访问controller之前被调用
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
            Exception {
        log.info("======================>>>拦截开始..............[{}]", handler.toString());
      /*
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
        String qrParam = request.getParameter("qrParam");
        if (StringUtils.isNotEmpty(qrParam)) {
            InvalidQrparam invalidQrparam = iInvalidQrparamService.getById(qrParam);
            if (null != invalidQrparam) {
                return false;
            }
        }
        return true;
    }

    /**
     * 访问controller之后 访问视图之前被调用
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
                           ModelAndView modelAndView) throws Exception {

    }

    /**
     * 访问视图之后被调用
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param e
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                Object o, Exception e) throws Exception {
    }

}
