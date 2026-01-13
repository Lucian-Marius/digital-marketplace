package com.marketplace.Digital_Marketplace.services;

import com.marketplace.Digital_Marketplace.models.UploadChunk;
import com.marketplace.Digital_Marketplace.repositories.UploadChunkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.UUID;

@Service
public class ChunkedUploadService {

    @Autowired
    private UploadChunkRepository uploadChunkRepository;

    @Autowired
    private StorageService storageService;

    private static final String TEMP_DIR = "temp/uploads/";

    /**
     * Generate a unique upload ID
     */
    public String generateUploadId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Check if a chunk already exists
     */
    public boolean chunkExists(String uploadId, int chunkNumber) {
        return uploadChunkRepository.findByUploadIdAndChunkNumber(uploadId, chunkNumber).isPresent();
    }

    /**
     * Save a chunk to the database and temporary storage
     */
    public void saveChunk(String uploadId, int chunkNumber, int totalChunks,
                         String fileName, String contentType, long totalFileSize,
                         MultipartFile chunk) throws IOException {

        // Create temp directory if it doesn't exist
        Path tempDir = Paths.get(TEMP_DIR);
        if (!Files.exists(tempDir)) {
            Files.createDirectories(tempDir);
        }

        // Save chunk data to temp file
        Path chunkPath = tempDir.resolve(uploadId + "_chunk_" + chunkNumber);
        Files.write(chunkPath, chunk.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);

        // Save chunk metadata to database
        UploadChunk uploadChunk = new UploadChunk();
        uploadChunk.setUploadId(uploadId);
        uploadChunk.setChunkNumber(chunkNumber);
        uploadChunk.setTotalChunks(totalChunks);
        uploadChunk.setFileName(fileName);
        uploadChunk.setContentType(contentType);
        uploadChunk.setTotalFileSize(totalFileSize);
        uploadChunk.setChunkSize(chunk.getSize());
        uploadChunk.setChunkPath(chunkPath.toString());

        uploadChunkRepository.save(uploadChunk);
    }

    /**
     * Check if upload is complete
     */
    public boolean isUploadComplete(String uploadId, int totalChunks) {
        long uploadedChunks = uploadChunkRepository.countByUploadId(uploadId);
        return uploadedChunks == totalChunks;
    }

    /**
     * Get upload progress as percentage
     */
    public int getUploadProgress(String uploadId, int totalChunks) {
        long uploadedChunks = uploadChunkRepository.countByUploadId(uploadId);
        return (int) ((uploadedChunks * 100.0) / totalChunks);
    }

    /**
     * Assemble chunks into final file and store it
     */
    public String assembleAndStoreFile(String uploadId, String folder) throws IOException {
        // Get all chunks for this upload
        List<UploadChunk> chunks = uploadChunkRepository.findByUploadIdOrderByChunkNumber(uploadId);

        if (chunks.isEmpty()) {
            throw new IOException("No chunks found for upload: " + uploadId);
        }

        // Create temp file for assembly
        Path tempDir = Paths.get(TEMP_DIR);
        String fileName = chunks.get(0).getFileName();
        Path assembledFile = tempDir.resolve(uploadId + "_assembled_" + fileName);

        // Assemble chunks
        try {
            for (UploadChunk chunk : chunks) {
                Path chunkPath = Paths.get(chunk.getChunkPath());
                if (Files.exists(chunkPath)) {
                    byte[] chunkData = Files.readAllBytes(chunkPath);
                    Files.write(assembledFile, chunkData,
                              Files.exists(assembledFile) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
                }
            }

            // Store the assembled file using StorageService
            String fileUrl = storageService.storeFile(assembledFile.toFile(), folder);

            // Clean up temp files and database records
            cleanupUpload(uploadId);

            return fileUrl;

        } catch (Exception e) {
            // Clean up on error
            if (Files.exists(assembledFile)) {
                Files.delete(assembledFile);
            }
            throw new IOException("Failed to assemble file: " + e.getMessage(), e);
        }
    }

    /**
     * Clean up temporary files and database records
     */
    private void cleanupUpload(String uploadId) {
        try {
            // Delete temp chunk files
            List<UploadChunk> chunks = uploadChunkRepository.findByUploadId(uploadId);
            for (UploadChunk chunk : chunks) {
                Path chunkPath = Paths.get(chunk.getChunkPath());
                if (Files.exists(chunkPath)) {
                    Files.delete(chunkPath);
                }
            }

            // Delete database records
            uploadChunkRepository.deleteByUploadId(uploadId);

        } catch (Exception e) {
            // Log error but don't throw - cleanup is best effort
            System.err.println("Warning: Failed to cleanup upload " + uploadId + ": " + e.getMessage());
        }
    }
}
