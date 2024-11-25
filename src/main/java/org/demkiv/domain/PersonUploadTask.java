package org.demkiv.domain;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.architecture.FileUploader;
import org.demkiv.domain.service.ProcessRunner;
import org.demkiv.domain.upload.Uploader;
import org.demkiv.web.model.form.PersonPhotoForm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Slf4j
@Setter
public class PersonUploadTask extends Thread {
    private FileUploader<File> s3Uploader;
    private Uploader uploader;
    private ProcessRunner processRunner;
    private PersonPhotoForm personPhotoForm;
    private Config config;

    @Override
    public void run() {
        try {
            Path tempDirectory = Files.createTempDirectory("temp_photo");
            File photo = getTempPhotoPath(tempDirectory);
            String thumbnailPath = getThumbnailPath(photo);
            String convertCommand = String.format(config.convertThumbnailCommand, photo.getAbsolutePath(), thumbnailPath);
            processRunner.runProcess(tempDirectory.toFile(), convertCommand, new StringWriter());
            File thumbnailInTempDir = new File(thumbnailPath);
            uploader.uploadThumbnail(thumbnailInTempDir);
            uploader.uploadPhoto(photo);
            uploader.saveEntity(personPhotoForm);
        } catch (Throwable ex) {
            log.error("Error when storing image. " + ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private String getThumbnailPath(File photo) {
        String photoPath = photo.getAbsolutePath();
        String dirPath = photoPath.substring(0, photoPath.lastIndexOf(File.separator));
        String fileName = photoPath.substring(photoPath.lastIndexOf(File.separator) + 1);
        String[] arr = fileName.split("\\.");
        String thumbnailName = arr[0] + "_thumbnail.gif";
        return dirPath + File.separator + thumbnailName;
    }

    private File getTempPhotoPath(Path tempDirectory) throws IOException {
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
