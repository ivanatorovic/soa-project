package com.soa.tour_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads", "keypoints")
                .toAbsolutePath()
                .normalize();

        registry.addResourceHandler("/uploads/keypoints/**")
                .addResourceLocations("file:" + uploadPath.toString() + "/");

        Path reviewsPath = Paths.get(System.getProperty("user.dir"), "uploads", "reviews")
                .toAbsolutePath()
                .normalize();

        registry.addResourceHandler("/uploads/reviews/**")
                .addResourceLocations("file:" + reviewsPath.toString() + "/");
    }
}
