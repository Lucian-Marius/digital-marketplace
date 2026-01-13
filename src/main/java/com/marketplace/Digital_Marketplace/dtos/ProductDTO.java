package com.marketplace.Digital_Marketplace.dtos;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String previewImageUrl;
    private Long sellerId;
    private Long categoryId;
    private Boolean approved;
}
