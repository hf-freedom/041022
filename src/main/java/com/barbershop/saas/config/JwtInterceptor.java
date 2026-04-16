package com.barbershop.saas.config;

import com.barbershop.saas.common.BusinessException;
import com.barbershop.saas.common.JwtUtil;
import com.barbershop.saas.common.MerchantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String servletPath = request.getServletPath();
        if (servletPath.contains("/merchant/register") ||
            servletPath.contains("/merchant/login") ||
            servletPath.contains("/admin/")) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            throw new BusinessException(401, "未登录，请先登录");
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(401, "token无效或已过期");
        }

        Long merchantId = jwtUtil.getMerchantIdFromToken(token);
        MerchantContext.setMerchantId(merchantId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MerchantContext.clear();
    }
}