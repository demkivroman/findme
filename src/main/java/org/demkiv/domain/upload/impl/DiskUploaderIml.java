package org.demkiv.domain.upload.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.Config;
import org.demkiv.domain.FindMeServiceException;
import org.demkiv.domain.upload.Uploader;
import org.demkiv.persistance.service.PersistService;
import org.demkiv.web.model.form.PersonPhotoForm;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Component("diskPhotoUploader")
public class DiskUploaderIml implements Uploader {
    final Config config;
    final PersistService<PersonPhotoForm, Boolean> persistService;

    @Override
    public void uploadPhoto(File source) {
        try {
            if (Objects.nonNull(source)) {
                File image = new File(config.getPhotosStorePath(), source.getName());
                Files.copy(source.toPath(), Path.of(image.toURI()));
                log.info("Image is stored on the disk {}", image.getPath());
            }
        } catch (IOException e) {
            throw new FindMeServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void uploadThumbnail(String source) {

    }

    @Override
    public Boolean saveEntity(PersonPhotoForm personPhotoForm) {
        String retrievePhotoPath = String.format("%s/%s", config.getPhotosStoreUrl(), personPhotoForm.getPhoto().getOriginalFilename());
        personPhotoForm.setUrl(retrievePhotoPath);
        persistService.saveEntity(personPhotoForm);
        log.info("Person's photo is completely stored to database.");
        return null;
    }
}
