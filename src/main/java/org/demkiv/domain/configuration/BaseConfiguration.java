package org.demkiv.domain.configuration;

import org.demkiv.domain.Config;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class BaseConfiguration {

    @Bean
    public Config getSecretConfig() {
        return Config.getInstance();
    }

    @Bean
    public DataSource getDataSource(Config config) {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(config.getDbUrl());
        dataSourceBuilder.username(config.getDbUsername());
        dataSourceBuilder.password(config.getDbPassword());
//        dataSourceBuilder.driverClassName(config.getDbDriver());
        return dataSourceBuilder.build();
    }
}
