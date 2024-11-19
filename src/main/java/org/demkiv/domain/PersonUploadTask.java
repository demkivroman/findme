package org.demkiv.domain;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.architecture.FileUploader;
import org.demkiv.domain.upload.DiskUploader;
import org.demkiv.persistance.service.PersistService;
import org.demkiv.web.model.form.PersonPhotoForm;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Slf4j
@Setter
public class PersonUploadTask extends Thread {
    private FileUploader<File> s3Uploader;
    private DiskUploader diskUploader;
    private PersistService<PersonPhotoForm, Boolean> persistService;
    private Path tempDirectory;
    private PersonPhotoForm personPhotoForm;

    @Override
    public void run() {
        try {
            diskUploader.upload(personPhotoForm.getPhoto());
            diskUploader.saveEntity(personPhotoForm);

//            ConfigFile configFile = ConfigFile.getInstance();
//            File filePath = getTempPhotoPath();
////            s3Uploader.upload(filePath);
////            log.info("Upload to amazon S3 is finished. File name {}", filePath.getName());

        } catch (Throwable ex) {
            log.error("Error when storing image. " + ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private File getTempPhotoPath() throws IOException {
        try (InputStream in = personPhotoForm.getPhoto().getInputStream()) {
            String fileName = personPhotoForm.getPhoto().getOriginalFilename();
            if (Objects.nonNull(fileName)) {
                File image = new File(tempDirectory.toFile(), fileName);
                Files.copy(in, Path.of(image.toURI()));
                log.info("Temp image file is created {}", image.getPath());
                return image;
            }
        }
        log.error("Temp image file is not created.");
        throw new FindMeServiceException("Temp image file is not created.");
    }
}
