package com.marketplace.Digital_Marketplace.repositories;

import com.marketplace.Digital_Marketplace.models.UploadChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UploadChunkRepository extends JpaRepository<UploadChunk, Long> {

    List<UploadChunk> findByUploadIdOrderByChunkNumber(String uploadId);

    Optional<UploadChunk> findByUploadIdAndChunkNumber(String uploadId, int chunkNumber);

    @Query("SELECT COUNT(c) FROM UploadChunk c WHERE c.uploadId = :uploadId AND c.completed = true")
    long countCompletedChunksByUploadId(@Param("uploadId") String uploadId);

    @Query("SELECT COUNT(DISTINCT c.chunkNumber) FROM UploadChunk c WHERE c.uploadId = :uploadId")
    long countDistinctChunksByUploadId(@Param("uploadId") String uploadId);

    void deleteByUploadId(String uploadId);

    boolean existsByUploadIdAndChunkNumber(String uploadId, int chunkNumber);
}
