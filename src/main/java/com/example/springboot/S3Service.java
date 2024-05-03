package com.example.springboot;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import java.util.List;

@Service
@Log4j2
public class S3Service {

    private final S3Client s3client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3Service(final S3Client s3client) {
        this.s3client = s3client;
    }

    public ResponseBytes<GetObjectResponse> getFile(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest
                .builder()
                .bucket(bucketName)
                .key(key)
                .build();
        return s3client.getObjectAsBytes(getObjectRequest);
    }

    public List<String> listBuckets() {
        return s3client.listBuckets().buckets().stream().map(bucket -> bucket.name()).toList();
    }
}
