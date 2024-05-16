package com.example.springboot;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

@Service
public class S3Service {

    private final AmazonS3 s3client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3Service(final AmazonS3 s3client) {
        this.s3client = s3client;
    }

    public Bucket createBucket(String bucketName) throws SdkClientException, AmazonServiceException {
        var bucket = this.s3client.createBucket(bucketName);
        return bucket;
    }

    public S3ObjectInputStream getFile(String key) {
        return s3client.getObject(bucketName, key).getObjectContent();
    }

    public List<String> listBuckets() {
        return s3client.listBuckets().stream().map(bucket -> bucket.getName()).toList();
    }

    public void uploadFile(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        uploadFile(file, s3client, this.bucketName, "/", false);
    }

    public void uploadFileWithEncryption(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        uploadFile(file, s3client, this.bucketName, "/", true);
    }

    public void uploadFileWithEncryption(MultipartFile file, String bucketName, String folder) throws IOException, NoSuchAlgorithmException {
        uploadFile(file, s3client, bucketName, folder, true);
    }

    public void uploadFile(MultipartFile file, AmazonS3 s3client, String bucketName, String folder, Boolean encrypt) throws IOException, NoSuchAlgorithmException {
        String fileName = new Date().toString().replace(" ", "-") + "-" + file.getOriginalFilename().replace(" ", "_");
        var objMetadata = new ObjectMetadata();
        byte[] objectBytes = file.getBytes();
        objMetadata.setContentLength(objectBytes.length);

        if(!folder.equals("/")) {
            if(encrypt){
                final InputStream im = new InputStream() {
                    @Override
                    public int read() throws IOException {
                        return -1;
                    }
                };
                var folderMetadata = new ObjectMetadata();
                folderMetadata.setContentLength(0L);
                folderMetadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
                var putObjectRequest = new PutObjectRequest(bucketName, folder, im, folderMetadata);
                var putObjectResult = s3client.putObject(putObjectRequest);
                System.out.println("FOlder created with sse result: " + putObjectResult.getSSEAlgorithm());
            } else {
                final InputStream im = new InputStream() {
                    @Override
                    public int read() throws IOException {
                        return -1;
                    }
                };
                var folderMetadata = new ObjectMetadata();
                folderMetadata.setContentLength(0L);
                var putObjectRequest = new PutObjectRequest(bucketName, folder, im, objMetadata);
                var putObjectResult = s3client.putObject(putObjectRequest);
                System.out.println("FOlder created with sse result: " + putObjectResult.getSSEAlgorithm());
            }
        }

        if (encrypt) {
            objMetadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
            var putObjectRequest = new PutObjectRequest(bucketName, !folder.equals("/") ? folder + "/" + fileName : fileName, new ByteArrayInputStream(objectBytes), objMetadata);
            var putObjectResult = s3client.putObject(putObjectRequest);
            System.out.println("Upload with sse result: " + putObjectResult.getSSEAlgorithm());
        } else {
            var putObjectRequest = new PutObjectRequest(bucketName, !folder.equals("/") ? folder + "/" + fileName : fileName, new ByteArrayInputStream(objectBytes), objMetadata);
            var putObjectResult = s3client.putObject(putObjectRequest);
            System.out.println("Upload with sse result: " + putObjectResult.getSSEAlgorithm());
        }

    }
}
