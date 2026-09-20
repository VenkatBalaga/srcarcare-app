package com.srcarcare.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${srcarcare.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = new File(uploadDir).getAbsolutePath().replace("\\", "/") + "/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + location + "/");
    }
}
