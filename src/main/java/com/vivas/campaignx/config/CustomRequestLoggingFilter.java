package com.vivas.campaignx.config;

import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

public class CustomRequestLoggingFilter extends CommonsRequestLoggingFilter {
    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return logger.isDebugEnabled() &&
                (!request.getRequestURL().toString().contains(".js") && !request.getRequestURL().toString().contains(".css")
                 && !request.getRequestURL().toString().contains(".jpg") && !request.getRequestURL().toString().contains(".png")
                 && !request.getRequestURL().toString().contains("/ws")
                 && !request.getRequestURL().toString().contains(".woff") && !request.getRequestURL().toString().contains(".ttf"));
    }
}
