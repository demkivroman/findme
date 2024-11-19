package org.demkiv.domain.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.Config;
import org.demkiv.domain.FindMeServiceException;
import org.demkiv.domain.PersonUploadTask;
import org.demkiv.domain.architecture.EntitySaver;
import org.demkiv.domain.architecture.FileUploader;
import org.demkiv.domain.upload.DiskUploader;
import org.demkiv.persistance.service.PersistService;
import org.demkiv.web.model.form.PersonPhotoForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
public class PhotoServiceImpl implements EntitySaver<PersonPhotoForm, Boolean> {
    private final FileUploader<File> s3Uploader;
    private final DiskUploader diskUploader;
    private final PersistService<PersonPhotoForm, Boolean> persistService;

    @Autowired
    public PhotoServiceImpl(
            @Qualifier("s3Uploader") FileUploader<File> s3Uploader,
            DiskUploader diskUploader,
            @Qualifier("photoService") PersistService<PersonPhotoForm, Boolean> persistService,
            Config config) {
        this.s3Uploader = s3Uploader;
        this.diskUploader = diskUploader;
        this.persistService = persistService;
    }

    @Override
    public Boolean saveEntity(PersonPhotoForm personPhotoForm) {
        try {
            Path tempDirectory = Files.createTempDirectory("temp_photo");
            PersonUploadTask uploadTask = getS3Uploader(personPhotoForm, tempDirectory);
            uploadTask.start();
        } catch (Exception exception) {
            throw new FindMeServiceException(exception.getMessage());
        }
        return false;
    }

    private PersonUploadTask getS3Uploader(PersonPhotoForm entity, Path tempDir) {
        PersonUploadTask uploadTask = new PersonUploadTask();
        uploadTask.setS3Uploader(s3Uploader);
        uploadTask.setDiskUploader(diskUploader);
        uploadTask.setPersistService(persistService);
        uploadTask.setTempDirectory(tempDir);
        uploadTask.setPersonPhotoForm(entity);
        return uploadTask;
    }
}
