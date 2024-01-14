package org.demkiv.domain.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.PersonUploadTask;
import org.demkiv.domain.architecture.EntitySaver;
import org.demkiv.domain.architecture.FileUploader;
import org.demkiv.persistance.service.PersistService;
import org.demkiv.web.model.form.PersonPhotoForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
public class PhotoServiceImpl implements EntitySaver<PersonPhotoForm, Boolean> {
    private final FileUploader<File> s3Uploader;
    private final PersistService<PersonPhotoForm, Boolean> persistService;

    @Autowired
    public PhotoServiceImpl(
            FileUploader<File> s3Uploader,
            @Qualifier("photoService") PersistService<PersonPhotoForm, Boolean> persistService) {
        this.s3Uploader = s3Uploader;
        this.persistService = persistService;
    }

    @Override
    public Boolean saveEntity(PersonPhotoForm entity) {
        try {
            Path tempDirectory = Files.createTempDirectory("temp_photo");
            PersonUploadTask uploadTask = getS3Uploader(entity, tempDirectory);
            uploadTask.start();
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
        return false;
    }

    private PersonUploadTask getS3Uploader(PersonPhotoForm entity, Path tempDir) {
        PersonUploadTask uploadTask = new PersonUploadTask();
        uploadTask.setS3Uploader(s3Uploader);
        uploadTask.setPersistService(persistService);
        uploadTask.setTempDirectory(tempDir);
        uploadTask.setPersonPhotoForm(entity);
        return uploadTask;
    }
}
