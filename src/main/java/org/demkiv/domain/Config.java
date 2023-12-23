package org.demkiv.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.util.PropertyLoader;

import java.io.File;

@Slf4j
@Setter
@Getter
public class Config {
    private volatile static Config uniqueInstance;
    private static File secretFile;
    private static final String CONFIG_PATH = "/findme/secret.props";

    private String s3AccessKey;

    private Config() {
    }

    public static Config getInstance() {
        if (uniqueInstance == null) {
            synchronized (Config.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new Config();

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
