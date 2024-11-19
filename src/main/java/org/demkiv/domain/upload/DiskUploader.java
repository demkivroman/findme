package org.demkiv.domain.upload;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.Config;
import org.demkiv.domain.FindMeServiceException;
import org.demkiv.domain.architecture.EntitySaver;
import org.demkiv.domain.architecture.FileUploader;
import org.demkiv.persistance.service.PersistService;
import org.demkiv.web.model.form.PersonPhotoForm;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Component("diskUploader")
public class DiskUploader implements FileUploader<MultipartFile>, EntitySaver<PersonPhotoForm, Boolean> {
    final Config config;
    final PersistService<PersonPhotoForm, Boolean> persistService;

    @Override
    public void upload(MultipartFile file) {
        storeImageOnDisk(file);
    }

    @Override
    public Boolean saveEntity(PersonPhotoForm personPhotoForm) {
        String retrievePhotoPath = String.format("%s/%s", config.getPhotosStoreUrl(), personPhotoForm.getPhoto().getOriginalFilename());
        personPhotoForm.setUrl(retrievePhotoPath);
        persistService.saveEntity(personPhotoForm);
        log.info("Person's photo is completely stored to database.");
        return null;
    }

    private void storeImageOnDisk(MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            String fileName = file.getOriginalFilename();
            if (Objects.nonNull(fileName)) {
                File image = new File(config.getPhotosStorePath(), fileName);
                Files.copy(in, Path.of(image.toURI()));
                log.info("Image is stored on the disk {}", image.getPath());
            }
        } catch (IOException e) {
            throw new FindMeServiceException(e.getMessage(), e);
        }
    }
}
