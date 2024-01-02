package org.demkiv.domain;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.architecture.FileUploader;
import org.demkiv.persistance.service.PersistService;
import org.demkiv.web.model.PersonForm;

import java.io.File;

@Slf4j
@Setter
public class PersonUploadTask extends Thread {
    private FileUploader<File> s3Uploader;
    private PersistService<PersonForm> persistService;
    private File filePath;
    private PersonForm entity;

    @Override
    public void run() {
        try {
            Config config = Config.getInstance();
            s3Uploader.upload(filePath);
            log.info("Upload to amazon S3 is finished. File name {}", filePath.getName());
            String retrievePhotoPath = String.format("%s/%s", config.getS3ImageRetrievePath(), entity.getPhoto().getOriginalFilename());
            persistService.saveEntity(entity, retrievePhotoPath);
            log.info("Person is completely stored to database.");
        } catch (Throwable ex) {
            Thread.currentThread().interrupt();
        }
    }
}
