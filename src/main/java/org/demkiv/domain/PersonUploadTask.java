package org.demkiv.domain;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.architecture.FileUploader;
import org.demkiv.domain.model.S3UploaderModel;
import org.demkiv.domain.service.ProcessRunner;
import org.demkiv.persistance.service.PersistService;
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
    private FileUploader<S3UploaderModel> s3Uploader;
    private PersistService<PersonPhotoForm, Boolean> persistPhotoService;
    private PersistService<PersonPhotoForm, Boolean> persistThumbnailService;
    private ProcessRunner processRunner;
    private PersonPhotoForm personPhotoForm;
    private Config config;

    @Override
    public void run() {
        try {
            Path tempDirectory = Files.createTempDirectory("temp_photo");
            File photo = getTempPhotoPath(tempDirectory);
            String convertedPhotoPath = photo.getAbsolutePath();
            String convertPhotoCommand = String.format(config.convertPhotoCommand, photo.getAbsolutePath(), convertedPhotoPath);
            processRunner.runProcess(tempDirectory.toFile(), convertPhotoCommand, new StringWriter());
            File photoInTempDir = new File(convertedPhotoPath);
            String thumbnailPath = getThumbnailPath(photo);
            String convertCommand = String.format(config.convertThumbnailCommand, photo.getAbsolutePath(), thumbnailPath);
            processRunner.runProcess(tempDirectory.toFile(), convertCommand, new StringWriter());
            File thumbnailInTempDir = new File(thumbnailPath);
            S3UploaderModel s3ThumbnailsModel = S3UploaderModel.builder()
                    .directory("thumbnails")
                    .file(thumbnailInTempDir)
                    .build();
            s3Uploader.upload(s3ThumbnailsModel);
            S3UploaderModel s3PhotosModel = S3UploaderModel.builder()
                    .directory("photos")
                    .file(photoInTempDir)
                    .build();
            s3Uploader.upload(s3PhotosModel);
            personPhotoForm.setUrl(String.format(config.getPhotosStoreUrl(), photoInTempDir.getName()));
            personPhotoForm.setThumbnailUrl(String.format(config.getThumbnailStoreUrl(), thumbnailInTempDir.getName()));
            persistPhotoService.saveEntity(personPhotoForm);
            persistThumbnailService.saveEntity(personPhotoForm);
        } catch (Throwable ex) {
            log.error("Error when storing an image. " + ex.getMessage());
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
            String fileName = convertToCorrectPhotoName(personPhotoForm.getPhoto().getOriginalFilename());
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

    private String convertToCorrectPhotoName(String photoName) {
        String fileNameSuffix = photoName.substring(photoName.lastIndexOf(".") + 1);
        String fileNamePrefix = photoName.substring(0, photoName.lastIndexOf("."));
        return fileNamePrefix.replaceAll("(\\s+)|(-+)", "_") + "." + fileNameSuffix;
    }
}
