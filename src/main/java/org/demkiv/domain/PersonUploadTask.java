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
import java.util.UUID;

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
            String convertedPhotoPath = getConvertedPhotoPath(photo);
            String convertPhotoCommand = String.format(config.convertPhotoCommand, photo.getAbsolutePath(), convertedPhotoPath);
            processRunner.runProcess(tempDirectory.toFile(), convertPhotoCommand, new StringWriter());
            File photoInTempDir = new File(convertedPhotoPath);
            String thumbnailPath = getThumbnailPath(photo);
            String convertCommand = String.format(config.convertThumbnailCommand, photo.getAbsolutePath(), thumbnailPath);
            processRunner.runProcess(tempDirectory.toFile(), convertCommand, new StringWriter());
            File thumbnailInTempDir = new File(thumbnailPath);
            uploader.uploadThumbnail(thumbnailInTempDir);
            uploader.saveThumbnailEntity(personPhotoForm, thumbnailInTempDir);
            uploader.uploadPhoto(photoInTempDir);
            uploader.saveEntity(personPhotoForm, photoInTempDir);
        } catch (Throwable ex) {
            log.error("Error when storing an image. " + ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private String getConvertedPhotoPath(File photo) {
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString().replace("-", "");
        String photoPath = photo.getAbsolutePath();
        String dirPath = photoPath.substring(0, photoPath.lastIndexOf(File.separator));
        String fileName = photoPath.substring(photoPath.lastIndexOf(File.separator) + 1);
        String[] arr = fileName.split("\\.");
        String photoName = String.format("%s_%s.gif", arr[0], uuidString);
        return dirPath + File.separator + photoName;
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
