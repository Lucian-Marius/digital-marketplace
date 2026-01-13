package com.marketplace.Digital_Marketplace.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_files")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String fileName;
    
    @Column(nullable = false)
    private String fileUrl;
    
    private String fileType;
    
    private Long fileSize;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
