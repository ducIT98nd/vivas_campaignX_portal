package com.vivas.campaignx.service.impl;

import com.vivas.campaignx.common.CodeConstants;
import com.vivas.campaignx.common.CommonConstants;
import com.vivas.campaignx.common.MessageConstants;
import com.vivas.campaignx.common.ResponseEntity;
import com.vivas.campaignx.controllers.AuthController;
import com.vivas.campaignx.entity.Users;
import com.vivas.campaignx.repository.UsersRepository;
import com.vivas.campaignx.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MailServiceImpl mailService;

    @Override
    public ResponseEntity forgotPassword(String email) {
        ResponseEntity responseEntity = new ResponseEntity();

        Optional<Users> usersOptional = usersRepository.getByEmailIgnoreCase(email);
        Long count = usersRepository.countEmailIgoreStatus(email);
        if(count == 0) {
            responseEntity.setCode(CodeConstants.NOT_FOUND);
            responseEntity.setMessage(MessageConstants.SEND_EMAIL_LOCK);
            return responseEntity;
        }
        String otpToken = UUID.randomUUID().toString();
        Users users = usersOptional.get();
        users.setOtpToken(otpToken);
        users.setForgotPasswordDatetime(LocalDateTime.now());
        usersRepository.save(users);

        String token = Base64.getEncoder().encodeToString(otpToken.getBytes(StandardCharsets.UTF_8));
        String linkForgotPassword = createLinkForForgotPasswordMail(token);
        try {
            linkForgotPassword = URLDecoder.decode(linkForgotPassword, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("encoding exception" + e);
        }
        mailService.sendForgotPasswordMail(email, linkForgotPassword);
        responseEntity.setCode(CodeConstants.OK);
        responseEntity.setMessage(MessageConstants.SEND_EMAIL_SUCCESS);

        return responseEntity;
    }

    @Override
    public Optional<Users> findByEmail(String email) {
        return usersRepository.getByEmailIgnoreCase(email);
    }

    public String createLinkForForgotPasswordMail(String token) {

        StringBuilder sb = new StringBuilder();
        try {
            ServletUriComponentsBuilder uriComponentsBuilder = ServletUriComponentsBuilder.fromCurrentServletMapping();
            uriComponentsBuilder.removePathExtension();
            sb.append(uriComponentsBuilder.build());
        } catch (Exception e) {
            sb.append(CommonConstants.BASE_URL);
        }
        sb.append("/page-set-password?token=");
        sb.append(token);
        return sb.toString();
    }



}
