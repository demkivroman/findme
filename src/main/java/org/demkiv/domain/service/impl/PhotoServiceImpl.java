package org.demkiv.domain.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.Config;
import org.demkiv.domain.FindMeServiceException;
import org.demkiv.domain.PersonUploadTask;
import org.demkiv.domain.architecture.EntitySaver;
import org.demkiv.domain.architecture.FileUploader;
import org.demkiv.domain.service.ProcessRunner;
import org.demkiv.domain.upload.Uploader;
import org.demkiv.web.model.form.PersonPhotoForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class PhotoServiceImpl implements EntitySaver<PersonPhotoForm, Boolean> {
    private final FileUploader<File> s3Uploader;
    private final Uploader uploader;
    private final ProcessRunner processRunner;
    private final Config config;

    @Autowired
    public PhotoServiceImpl(
            @Qualifier("s3Uploader") FileUploader<File> s3Uploader,
            Uploader uploader,
            ProcessRunner processRunner,
            Config config) {
        this.s3Uploader = s3Uploader;
        this.uploader = uploader;
        this.processRunner = processRunner;
        this.config = config;
    }

    @Override
    public Boolean saveEntity(PersonPhotoForm personPhotoForm) {
        try {
            PersonUploadTask uploadTask = getS3Uploader(personPhotoForm);
            uploadTask.start();
        } catch (Exception exception) {
            throw new FindMeServiceException(exception.getMessage());
        }
        return false;
    }

    private PersonUploadTask getS3Uploader(PersonPhotoForm entity) {
        PersonUploadTask uploadTask = new PersonUploadTask();
        uploadTask.setS3Uploader(s3Uploader);
        uploadTask.setUploader(uploader);
        uploadTask.setProcessRunner(processRunner);
        uploadTask.setPersonPhotoForm(entity);
        uploadTask.setConfig(config);
        return uploadTask;
    }
}
