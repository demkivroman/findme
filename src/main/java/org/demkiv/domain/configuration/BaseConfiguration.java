package org.demkiv.domain.configuration;

import org.demkiv.domain.ConfigFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

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

    public DataSource getDataSource(ConfigFile configFile) {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(configFile.getDbUrl());
        dataSourceBuilder.username(configFile.getDbUsername());
        dataSourceBuilder.password(configFile.getDbPassword());
//        dataSourceBuilder.driverClassName(config.getDbDriver());
        return dataSourceBuilder.build();
    }
}
