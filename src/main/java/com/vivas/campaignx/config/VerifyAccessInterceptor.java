package com.vivas.campaignx.config;

import javax.servlet.http.*;

import org.apache.logging.log4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.*;
import org.springframework.web.servlet.*;

@Component
public class VerifyAccessInterceptor implements HandlerInterceptor {
    protected final Logger logger = LogManager.getLogger(this.getClass().getName());

    @Autowired
    private UserDetailServiceImpl userDetailServiceImpl;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.getName() != null) {
            UserDetails userDetails = this.userDetailServiceImpl.loadUserByUsername(authentication.getName());
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails,
                    userDetails.getPassword(), userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        return true;
    }
}
