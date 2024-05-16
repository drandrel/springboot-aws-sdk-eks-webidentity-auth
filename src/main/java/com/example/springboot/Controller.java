package com.example.springboot;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.Bucket;

import jakarta.websocket.server.PathParam;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class Controller {

    private final S3Service s3Service;

    public Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @GetMapping
    public String health() {
        return "UP";
    }
    @PostMapping("/createBucket/{bucketName}")
    public ResponseEntity<Bucket> createBucket(@PathVariable(value = "bucketName") String bucketName) {
        try {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(s3Service.createBucket(bucketName));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/list_buckets")
    public ResponseEntity<List<String>> listBuckets() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(s3Service.listBuckets());
    }

    @PostMapping("/upload/{useEncryption}")
    public ResponseEntity<String> uploadFile(@RequestPart(value = "file") MultipartFile file, @PathVariable(value = "useEncryption") Boolean useEncryption) {
        try {
            if(useEncryption) {
                s3Service.uploadFileWithEncryption(file);
            } else {
                s3Service.uploadFile(file);
            }
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("{ \"status\": \"ok\" }");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/upload/{bucket}/{folder}/{useEncryption}")
    public ResponseEntity<String> uploadFileToBucket(
            @RequestPart(value = "file") MultipartFile file,
            @PathVariable(value = "bucket") String bucket,
            @PathVariable(value = "folder") String folder,
            @PathVariable(value = "useEncryption") Boolean useEncryption) {
        try {
            if(useEncryption) {
                s3Service.uploadFileWithEncryption(file, bucket, folder);
            } else {
                s3Service.uploadFile(file);
            }
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("{ \"status\": \"ok\" }");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/view/{fileName}")
    public ResponseEntity<InputStreamResource> viewFile(@PathVariable String fileName) {
        var content = s3Service.getFile(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\""+fileName+"\"")
                .body(new InputStreamResource(content));
    }
}
