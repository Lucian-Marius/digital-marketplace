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
    private String uploadId;

    @Column(nullable = false)
    private int chunkNumber;

    @Column(nullable = false)
    private int totalChunks;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private long totalFileSize;

    @Column(nullable = false)
    private long chunkSize;

    @Column(nullable = false)
    private String chunkPath; // path to the temp chunk file on disk

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @Column(nullable = false)
    private boolean completed = false;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}