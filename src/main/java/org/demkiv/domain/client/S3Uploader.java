package org.demkiv.domain.client;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.Config;
import org.demkiv.domain.architecture.FileUploader;

import java.io.File;

@Slf4j
public class S3Uploader implements FileUploader<File> {
    private final Config config = Config.getInstance();

    @Override
    public void upload(File file) {
        String key = config.getS3ImageKey() + "/" + file.getName();
        String bucketName = config.getS3BucketName();
        AmazonS3 client = getAmazonS3Client();
        try {
            if (!fileExist(client, key ,bucketName)) {
                client.putObject(bucketName, key, file);
                log.info("{} image is uploaded.", key);
            } else {
                log.info("{} already exists on S3", key);
            }
        } catch (Exception ex) {
            log.error("{} image is not uploaded. {}", key, ex.getMessage());
        }
    }

    private AmazonS3 getAmazonS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(
                config.getS3AccessKey(),
                config.getS3SecretKey()
        );

        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_2)
                .build();
    }

    private boolean fileExist(AmazonS3 client, String key, String awsBucketName) {
            try {
                client.getObjectMetadata(awsBucketName, key);
            } catch (AmazonS3Exception ex) {
                return false;
            }
        return true;
    }
}
