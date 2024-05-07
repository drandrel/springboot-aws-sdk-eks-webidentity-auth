package com.example.springboot;

import com.amazonaws.auth.WebIdentityTokenCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.nio.file.Path;
import java.util.UUID;

@Configuration
public class S3Config {

    @Bean
    public AmazonS3 s3client() {
        var awsS3Config = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.EU_CENTRAL_1)
                .withCredentials(WebIdentityTokenCredentialsProvider
                        .builder()
                        .roleArn(System.getenv("AWS_ROLE_ARN"))
                        .roleSessionName(UUID.randomUUID().toString())
                        .webIdentityTokenFile(System.getenv("AWS_WEB_IDENTITY_TOKEN_FILE"))
                        .build())
                .build();
        return awsS3Config;
    }
}
