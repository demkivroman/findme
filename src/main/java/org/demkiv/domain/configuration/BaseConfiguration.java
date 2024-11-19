package org.demkiv.domain.configuration;

import org.demkiv.domain.ConfigFile;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class BaseConfiguration {

    @Bean
    public ConfigFile getSecretConfig() {
        return ConfigFile.getInstance();
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
