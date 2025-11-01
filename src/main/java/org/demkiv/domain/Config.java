package org.demkiv.domain;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@PropertySource(value = "classpath:application.properties")
@Data
public class Config {

    @Value("${bucketPhotoDirectory}")
    String bucketPhotoDirectory;
    @Value("${photosStoreUrl}")
    String photosStoreUrl;
    @Value("${photosStoreConvertedUrl}")
    String photoStoreConvertedUrl;
    @Value("${domain}")
    String domain;
    @Value("${emailFrom}")
    String emailFrom;
}
