package org.demkiv.domain;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@PropertySource(value = "classpath:application.properties")
@Data
public class Config {
    @Value("${photosStorePath}")
    String photosStorePath;
    @Value("${photosStoreUrl}")
    String photosStoreUrl;
    @Value("${thumbnailStorePath}")
    String thumbnailStorePath;
    @Value("${thumbnailStoreUrl}")
    String thumbnailStoreUrl;
    @Value("${convertThumbnailCommand}")
    String convertThumbnailCommand;
}
