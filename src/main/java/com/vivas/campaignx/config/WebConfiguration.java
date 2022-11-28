package com.vivas.campaignx.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Configuration
public class WebConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private VerifyAccessInterceptor verifyAccessInterceptor;

    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = { "classpath:/META-INF/resources/",
            "classpath:/resources/", "classpath:/static/", "classpath:/public/" };

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);

    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        List<String> listExcludePathPatterns = new ArrayList<>();
        listExcludePathPatterns.add("/static/**");
        listExcludePathPatterns.add("/css/**");
        listExcludePathPatterns.add("/js/**");
        listExcludePathPatterns.add("/scss/**");
        listExcludePathPatterns.add("/images/**");
        listExcludePathPatterns.add("/assets/**");
        listExcludePathPatterns.add("/error");
        registry.addInterceptor(verifyAccessInterceptor).addPathPatterns("/**").excludePathPatterns(listExcludePathPatterns);
    }
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.removeConvertible(String.class, Collection.class);
        registry.addConverter(String.class, Collection.class, noCommaSplitStringToCollectionConverter());
    }

    public Converter<String, Collection> noCommaSplitStringToCollectionConverter() {
        return Collections::singletonList;
    }

}
