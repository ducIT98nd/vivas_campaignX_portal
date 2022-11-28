package com.vivas.campaignx.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public CustomRequestLoggingFilter logFilter() {
        CustomRequestLoggingFilter filter = new CustomRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(false);
        filter.setIncludeClientInfo(true);
        filter.setAfterMessagePrefix("Finished request: ");
        filter.setBeforeMessagePrefix("Received request: ");
        return filter;
    }
}
