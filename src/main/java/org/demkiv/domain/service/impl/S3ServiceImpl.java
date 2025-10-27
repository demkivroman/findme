package org.demkiv.domain.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.demkiv.domain.ConfigFile;
import org.demkiv.domain.FindMeServiceException;
import org.demkiv.domain.model.S3UploaderModel;
import org.demkiv.domain.service.S3Service;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3ServiceImpl implements S3Service {

    private static final String S3_PHOTO_DIRECTORY = "photos";
    private final ConfigFile config;

    @Override
    public void upload(String key, File photo) {
        try {
            AmazonS3 client = getAmazonS3Client();
            client.putObject(config.getS3BucketName(), key, photo);
            log.info("Uploaded file to S3 {}", key);
        } catch (Exception ex) {
            log.error("File is not uploaded to S3.", ex);
            throw new FindMeServiceException("File is not uploaded to S3", ex);
        }
    }

    @Override
    public void deletePhotoFromS3(String photoUrl) {
        String photoName = StringUtils.substringAfterLast(photoUrl, "/");
        String key = String.format("%s/%s", S3_PHOTO_DIRECTORY, photoName);
        AmazonS3 client = getAmazonS3Client();
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(config.getS3BucketName(), key);
        client.deleteObject(deleteObjectRequest);
        log.info("Deleted file from S3 {}", key);
    }

    private AmazonS3 getAmazonS3Client() {
        return AmazonS3ClientBuilder.standard()
                .withRegion(config.getS3Region())
                .build();
    }
}
