package com.marketplace.Digital_Marketplace.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "upload_chunks")
@Data
public class UploadChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String uploadId; // Unique identifier for the entire upload

    @Column(nullable = false)
    private int chunkNumber; // Chunk sequence number

    @Column(nullable = false)
    private int totalChunks; // Total number of chunks expected

    @Column(nullable = false)
    private String fileName; // Original file name

    @Column(nullable = false)
    private String contentType; // File content type

    @Column(nullable = false)
    private long totalFileSize; // Total file size

    @Column(nullable = false)
    private long chunkSize; // Size of this chunk

    @Lob
    @Column(nullable = false)
    private byte[] data; // Chunk data

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @Column(nullable = false)
    private boolean completed = false; // Whether this chunk has been processed

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}
