package org.demkiv.domain.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.Config;
import org.demkiv.domain.FindMeServiceException;
import org.demkiv.domain.PersonUploadTask;
import org.demkiv.domain.architecture.EntitySaver;
import org.demkiv.domain.architecture.FileUploader;
import org.demkiv.domain.model.S3UploaderModel;
import org.demkiv.domain.service.ProcessRunner;
import org.demkiv.persistance.service.PersistService;
import org.demkiv.web.model.form.PersonPhotoForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PhotoServiceImpl implements EntitySaver<PersonPhotoForm, Boolean> {
    private final FileUploader<S3UploaderModel> s3Uploader;
    private final PersistService<PersonPhotoForm, Boolean> persistPhotoService;
    private final PersistService<PersonPhotoForm, Boolean> persistThumbnailService;
    private final ProcessRunner processRunner;
    private final Config config;

    @Autowired
    public PhotoServiceImpl(
            @Qualifier("s3Uploader") FileUploader<S3UploaderModel> s3Uploader,
            @Qualifier("photoService") PersistService<PersonPhotoForm, Boolean> persistPhotoService,
            @Qualifier("thumbnailService") PersistService<PersonPhotoForm, Boolean> persistThumbnailService,
            ProcessRunner processRunner,
            Config config) {
        this.s3Uploader = s3Uploader;
        this.persistPhotoService = persistPhotoService;
        this.persistThumbnailService = persistThumbnailService;
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

    private PersonUploadTask getS3Uploader(PersonPhotoForm personPhotoForm) {
        PersonUploadTask uploadTask = new PersonUploadTask();
        uploadTask.setS3Uploader(s3Uploader);
        uploadTask.setProcessRunner(processRunner);
        uploadTask.setPersistPhotoService(persistPhotoService);
        uploadTask.setPersistThumbnailService(persistThumbnailService);
        uploadTask.setPersonPhotoForm(personPhotoForm);
        uploadTask.setConfig(config);
        return uploadTask;
    }
}
