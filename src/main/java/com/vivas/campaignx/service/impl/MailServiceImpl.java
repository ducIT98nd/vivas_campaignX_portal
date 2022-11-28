package com.vivas.campaignx.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class MailServiceImpl {

    private final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);

    private static final String LINK = "link";

    private static final String SEND_EMAIL_FORGOT_PASSWORD_SUBJECT = "Campaign X – Yêu cầu đặt lại mật khẩu";

    private final JavaMailSender javaMailSender;

    private final SpringTemplateEngine springTemplateEngine;

    public MailServiceImpl(JavaMailSender javaMailSender, SpringTemplateEngine springTemplateEngine
    ) {
        this.javaMailSender = javaMailSender;
        this.springTemplateEngine = springTemplateEngine;
    }

    @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}", isMultipart, isHtml, to, subject, content);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", to);
        } catch (MailException | MessagingException e) {
            log.warn("Email could not be sent to user '{}'", to, e);
        }
    }

    @Async
    public void sendEmailWithTemplate(String email, String link, String templateName, String subject) {
        Locale locale = Locale.forLanguageTag("vi");
        Context context = new Context(locale);
        context.setVariable(LINK, link);
        String content = springTemplateEngine.process(templateName, context);
        sendEmail(email, subject, content, false, true);
    }

    @Async
    public void sendForgotPasswordMail(String email, String link) {
        log.debug("Sending forgot password email to '{}'", email);
        sendEmailWithTemplate(email, link, "mail/forgotPasswordEmail", SEND_EMAIL_FORGOT_PASSWORD_SUBJECT);
    }


}
