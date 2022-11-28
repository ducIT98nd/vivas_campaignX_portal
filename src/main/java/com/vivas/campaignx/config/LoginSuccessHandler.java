package com.vivas.campaignx.config;

import com.vivas.campaignx.entity.Users;
import com.vivas.campaignx.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;


@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private UsersService usersService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Optional<Users> user = usersService.findUserByUsernameAndStatus(currentUser.getUsername());
        if (user.isPresent()) {
            if (user.get().getFailedAttempt() == null || user.get().getFailedAttempt() > 0) {
                usersService.resetFailedAttempts(user.get().getUsername());
            }
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
