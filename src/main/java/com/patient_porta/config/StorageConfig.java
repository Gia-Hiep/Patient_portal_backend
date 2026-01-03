package com.patient_porta.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StorageConfig {

    @Value("${files.storage-root:./}")
    private String storageRoot;

    @PostConstruct
    public void ensureUploadsFolder() throws Exception {
        Path root = Paths.get(storageRoot).toAbsolutePath().normalize();
        Path uploads = root.resolve("uploads");
        if (!Files.exists(uploads)) {
            Files.createDirectories(uploads);
        }
    }
}
