package com.federal.web;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Log4j2
public class CustomMvcConfigurerAdapter implements WebMvcConfigurer {

    private final static String FOLDER = "tutorials";

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        log.info("-----------------------------------");
        log.info("-----------------------------------");
        log.info("Registry " + registry.toString());
        registry.addViewController("/" + FOLDER)
                .setViewName("redirect:/" + FOLDER + "/");
        registry.addViewController("/docs" + FOLDER + "/")
                .setViewName("forward:/" + FOLDER  + "/index.html");
    }
}