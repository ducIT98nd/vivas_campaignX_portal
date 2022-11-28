package com.vivas.campaignx.service;

public interface MailService {

    void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml);

    void sendEmailWithTemplate(String email, String templateName);

    void sendForgotPasswordMail(String email);
}
