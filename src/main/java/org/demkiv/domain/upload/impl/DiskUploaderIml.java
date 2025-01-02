package org.demkiv.domain.upload.impl;

import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.Config;
import org.demkiv.domain.FindMeServiceException;
import org.demkiv.domain.upload.Uploader;
import org.demkiv.persistance.service.PersistService;
import org.demkiv.web.model.form.PersonPhotoForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Slf4j
@Component("diskPhotoUploader")
public class DiskUploaderIml implements Uploader {
    Config config;
    PersistService<PersonPhotoForm, Boolean> persistService;
    PersistService<PersonPhotoForm, Boolean> thumbnailPersistService;

    @Autowired
    public DiskUploaderIml(
            Config config,
            @Qualifier("photoService")
            PersistService<PersonPhotoForm, Boolean> persistService,
            @Qualifier("thumbnailService")
            PersistService<PersonPhotoForm, Boolean> thumbnailPersistService) {
        this.config = config;
        this.persistService = persistService;
        this.thumbnailPersistService = thumbnailPersistService;
    }

    @Override
    public void uploadPhoto(File source) {
        try {
            if (Objects.nonNull(source)) {
                File image = new File(config.getPhotosStorePath(), source.getName());
                if (!image.exists()) {
                    Files.copy(source.toPath(), Path.of(image.toURI()));
                    log.info("Image is stored on the disk {}", image.getPath());
                }
            }
        } catch (IOException e) {
            throw new FindMeServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void uploadThumbnail(File source) {
        try {
            if (Objects.nonNull(source)) {
                File thumbnail = new File(config.getThumbnailStorePath(), source.getName());
                if (!thumbnail.exists()) {
                    Files.copy(source.toPath(), Path.of(thumbnail.toURI()));
                    log.info("Thumbnail is stored on the disk {}", thumbnail.getPath());
                }
            }
        } catch (IOException e) {
            throw new FindMeServiceException(e.getMessage(), e);
        }
    }

    @Override
    public Boolean saveEntity(PersonPhotoForm personPhotoForm) {
        String retrievePhotoPath = String.format("%s/%s", config.getPhotosStoreUrl(), personPhotoForm.getPhoto().getOriginalFilename());
        personPhotoForm.setUrl(retrievePhotoPath);
        persistService.saveEntity(personPhotoForm);
        log.info("Person's photo is completely stored to database.");
        return null;
    }

    @Override
    public Boolean saveThumbnailEntity(PersonPhotoForm personPhotoForm, File thumbnailSource) {
        String retrieveThumbnailPath = String.format("%s/%s", config.getThumbnailStoreUrl(), thumbnailSource.getName());
        personPhotoForm.setThumbnailUrl(retrieveThumbnailPath);
        thumbnailPersistService.saveEntity(personPhotoForm);
        log.info("Person's thumbnail is completely stored to database.");
        return null;
    }
}
