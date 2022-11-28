package com.vivas.campaignx.config;

import com.vivas.campaignx.entity.Users;
import com.vivas.campaignx.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UsersService usersService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");
        logger.info("exception: ");
        logger.info("A failed login attempt with username: "
                + username + ". Reason: " + exception.getMessage());

        Optional<Users> u = usersService.findUserByUsernameAndStatus(username);
        if (u.isPresent()) {
            Users user = u.get();
            if (user.getStatus() == 0) {
                super.setDefaultFailureUrl("/login?errorCode=" + 2);
            }
            if (user.getStatus() == 1) {
                if (user.getFailedAttempt() < UsersService.MAX_FAILED_ATTEMPTS - 1) {
                    usersService.increaseFailedAttempts(user);
                    super.setDefaultFailureUrl("/login?errorCode=" + 1);
                } else {
                    usersService.lock(user);
                    super.setDefaultFailureUrl("/login?errorCode=" + 4);
                }
            }
        } else if (!u.isPresent()) {
            super.setDefaultFailureUrl("/login?errorCode=" + 1);
        } else {
            super.setDefaultFailureUrl("/login?errorCode=" + 3);
        }


        super.onAuthenticationFailure(request, response, exception);

    }
}
