package org.demkiv.domain.configuration;

import org.demkiv.domain.ConfigFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class BaseConfiguration implements WebMvcConfigurer {
    @Value("${cross-origins.allowed.patterns}")
    String crossOriginPattern;

    @Bean
    public ConfigFile getSecretConfig() {
        return ConfigFile.getInstance();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .maxAge(3600)
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowedOriginPatterns(crossOriginPattern.split(","))
                .allowCredentials(true);
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
