package com.example.springboot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import java.nio.file.Path;
import java.util.UUID;

@Configuration
public class S3Config {

    @Bean
    public S3Client s3client() {
        var awsS3Config = S3Client.builder()
                .credentialsProvider(WebIdentityTokenFileCredentialsProvider
                        .builder()
                        .roleArn(System.getenv("AWS_ROLE_ARN"))
                        .roleSessionName(UUID.randomUUID().toString())
                        .webIdentityTokenFile(Path.of(System.getenv("AWS_WEB_IDENTITY_TOKEN_FILE")))
                        .build())
                .region(Region.EU_CENTRAL_1)
                .build();

        return awsS3Config;
    }
}