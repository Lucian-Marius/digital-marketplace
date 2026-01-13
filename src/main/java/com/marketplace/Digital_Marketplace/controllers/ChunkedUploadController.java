package com.marketplace.Digital_Marketplace.controllers;

import com.marketplace.Digital_Marketplace.services.ChunkedUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/upload")
public class ChunkedUploadController {

    @Autowired
    private ChunkedUploadService chunkedUploadService;

    /**
     * Initialize a new chunked upload
     */
    @PostMapping("/init")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> initializeUpload(
            @RequestParam("fileName") String fileName,
            @RequestParam("fileSize") long fileSize,
            @RequestParam("contentType") String contentType,
            @RequestParam("totalChunks") int totalChunks,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String uploadId = chunkedUploadService.generateUploadId();

        Map<String, Object> response = new HashMap<>();
        response.put("uploadId", uploadId);
        response.put("chunkSize", 1024 * 1024); // 1MB chunks
        response.put("status", "initialized");

        return ResponseEntity.ok(response);
    }

    /**
     * Upload a chunk
     */
    @PostMapping("/chunk")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadChunk(
            @RequestParam("uploadId") String uploadId,
            @RequestParam("chunkNumber") int chunkNumber,
            @RequestParam("totalChunks") int totalChunks,
            @RequestParam("fileName") String fileName,
            @RequestParam("contentType") String contentType,
            @RequestParam("totalFileSize") long totalFileSize,
            @RequestParam("chunk") MultipartFile chunk,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            // Check if chunk already exists
            if (chunkedUploadService.chunkExists(uploadId, chunkNumber)) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "chunk_exists");
                response.put("chunkNumber", chunkNumber);
                return ResponseEntity.ok(response);
            }

            // Save the chunk
            chunkedUploadService.saveChunk(uploadId, chunkNumber, totalChunks,
                                         fileName, contentType, totalFileSize, chunk);

            // Check if upload is complete
            boolean isComplete = chunkedUploadService.isUploadComplete(uploadId, totalChunks);
            int progress = chunkedUploadService.getUploadProgress(uploadId, totalChunks);

            Map<String, Object> response = new HashMap<>();
            response.put("status", isComplete ? "complete" : "chunk_uploaded");
            response.put("chunkNumber", chunkNumber);
            response.put("progress", progress);
            response.put("uploadId", uploadId);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to save chunk: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Complete the upload and assemble the file
     */
    @PostMapping("/complete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> completeUpload(
            @RequestParam("uploadId") String uploadId,
            @RequestParam("folder") String folder,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String fileUrl = chunkedUploadService.assembleAndStoreFile(uploadId, folder);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("fileUrl", fileUrl);
            response.put("uploadId", uploadId);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to assemble file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get upload progress
     */
    @GetMapping("/progress/{uploadId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProgress(
            @PathVariable String uploadId,
            @RequestParam("totalChunks") int totalChunks,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        int progress = chunkedUploadService.getUploadProgress(uploadId, totalChunks);

        Map<String, Object> response = new HashMap<>();
        response.put("uploadId", uploadId);
        response.put("progress", progress);
        response.put("completed", progress == 100);

        return ResponseEntity.ok(response);
    }

    /**
     * Cancel an upload and clean up chunks
     */
    @DeleteMapping("/cancel/{uploadId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cancelUpload(
            @PathVariable String uploadId,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Note: In a real implementation, you'd want to clean up chunks
        // For now, we'll just return success

        Map<String, Object> response = new HashMap<>();
        response.put("status", "cancelled");
        response.put("uploadId", uploadId);

        return ResponseEntity.ok(response);
    }
}
