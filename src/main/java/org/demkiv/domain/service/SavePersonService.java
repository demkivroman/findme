package org.demkiv.domain.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.Config;
import org.demkiv.domain.PersonUploadTask;
import org.demkiv.domain.architecture.EntitySaver;
import org.demkiv.domain.architecture.FileUploader;
import org.demkiv.persistance.service.PersistService;
import org.demkiv.web.model.PersonForm;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Service
public class SavePersonService implements EntitySaver<PersonForm, Boolean> {
    private final FileUploader<File> s3Uploader;
    private final PersistService<PersonForm> persistService;

    @Override
    public Boolean saveEntity(PersonForm entity) {
        try {
            Path tempDirectory = Files.createTempDirectory("temp_photo");
            File tempPhotoPath = getTempPhotoPath(entity, tempDirectory.toFile());
            PersonUploadTask uploadTask = getS3Uploader(tempPhotoPath, entity);
            uploadTask.start();
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

    private PersonUploadTask getS3Uploader(File uploadFilePath, PersonForm entity) {
        PersonUploadTask uploadTask = new PersonUploadTask();
        uploadTask.setS3Uploader(s3Uploader);
        uploadTask.setPersistService(persistService);
        uploadTask.setFilePath(uploadFilePath);
        uploadTask.setEntity(entity);
        return uploadTask;
    }
}
