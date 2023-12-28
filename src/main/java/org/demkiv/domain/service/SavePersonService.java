package org.demkiv.domain.service;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.architecture.EntitySaver;
import org.demkiv.domain.architecture.FileUploader;
import org.demkiv.web.model.PersonForm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Slf4j
@Setter
public class SavePersonService implements EntitySaver<PersonForm, Boolean> {
    private FileUploader<File> s3Uploader;

    @Override
    public Boolean saveEntity(PersonForm entity) {
        try {
            Path tempDirectory = Files.createTempDirectory("temp_photo");
            File tempPhotoPath = getTempPhotoPath(entity, tempDirectory.toFile());
            s3Uploader.upload(tempPhotoPath);
            log.info("Upload to amazon S3 is finished. File name {}", tempPhotoPath.getPath());
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }

        return false;
    }

    private File getTempPhotoPath(PersonForm personForm, File tempDirectory) throws IOException {
        try (InputStream in = personForm.getPhoto().getInputStream()) {
            String fileName = personForm.getPhoto().getOriginalFilename();
            if (Objects.nonNull(fileName)) {
                File image = new File(tempDirectory, fileName);
                Files.copy(in, Path.of(image.toURI()));
                log.info("Temp image file is created {}", image.getPath());
                return image;
            }
        }
        log.error("Temp image file is not created.");
        throw new RuntimeException("Temp image file is not created.");
    }
}
