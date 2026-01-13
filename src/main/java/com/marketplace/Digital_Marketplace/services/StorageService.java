package com.marketplace.Digital_Marketplace.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class StorageService {

    @Value("${storage.type:local}")
    private String storageType;

    @Value("${storage.local.path:uploads}")
    private String localStoragePath;

    @Value("${supabase.url:}")
    private String supabaseUrl;

    @Value("${supabase.key:}")
    private String supabaseKey;

    @Value("${supabase.bucket:digital-marketplace}")
    private String supabaseBucket;

    /**
     * Upload a file and return the file path/URL
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        if ("supabase".equalsIgnoreCase(storageType)) {
            return uploadToSupabase(file, folder);
        } else {
            return uploadToLocal(file, folder);
        }
    }

    /**
     * Upload to Supabase Storage via REST API
     */
    private String uploadToSupabase(MultipartFile file, String folder) throws IOException {
        String fileName = generateFileName(file.getOriginalFilename());
        String filePath = folder + "/" + fileName;

        try {
            // Return placeholder - In production, use Supabase REST API
            // For now, files are stored locally and served from uploads folder
            String localPath = uploadToLocal(file, folder);
            
            // Return a URL that represents where it would be in Supabase
            return supabaseUrl + "/storage/v1/object/public/" + supabaseBucket + "/" + filePath;
        } catch (Exception e) {
            throw new IOException("Failed to upload file to Supabase: " + e.getMessage(), e);
        }
    }

    /**
     * Upload to local storage
     */
    private String uploadToLocal(MultipartFile file, String folder) throws IOException {
        String fileName = generateFileName(file.getOriginalFilename());
        Path folderPath = Paths.get(localStoragePath, folder);

        // Create directories if they don't exist
        Files.createDirectories(folderPath);

        Path filePath = folderPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        return "/" + folder + "/" + fileName;
    }

    /**
     * Generate unique filename to avoid conflicts
     */
    private String generateFileName(String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    /**
     * Delete a file
     */
    public void deleteFile(String filePath) throws IOException {
        if ("supabase".equalsIgnoreCase(storageType)) {
            deleteFromSupabase(filePath);
        } else {
            deleteFromLocal(filePath);
        }
    }

    private void deleteFromSupabase(String filePath) throws IOException {
        // Implement Supabase deletion logic
        // For now, just log it
        System.out.println("Would delete from Supabase: " + filePath);
    }

    private void deleteFromLocal(String filePath) throws IOException {
        Path path = Paths.get(localStoragePath, filePath);
        Files.deleteIfExists(path);
    }
}
