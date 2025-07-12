package org.demkiv.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.upload.S3Uploader;
import org.demkiv.domain.util.PropertyLoader;

import java.io.File;

@Slf4j
@Setter
@Getter
public class ConfigFile {
    private volatile static ConfigFile uniqueInstance;
    private static File secretFile;
    private static final String CONFIG_PATH = "/findme/secret.props";

    private String s3AccessKey;
    private String s3SecretKey;
    private String s3BucketName;
    private String s3Region;

    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private String dbDriver;

    private ConfigFile() {
    }

    public static ConfigFile getInstance() {
        if (uniqueInstance == null) {
            synchronized (ConfigFile.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new ConfigFile();

                    if (secretFile == null) {
                        secretFile = new File(System.getProperty( "user.home" ) + CONFIG_PATH);
                    }
                    if (secretFile.exists()) {
                        log.info("Initializing properties from {} file", secretFile);
                        PropertyLoader.loadProperties(secretFile, uniqueInstance);
                        log.info("Properties initialized from {} file", secretFile);
                    }
                }
            }
        }
        return uniqueInstance;
    }
}
