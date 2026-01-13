package com.marketplace.Digital_Marketplace.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long id;
    private String name;
    private String description;
    private String allowedFileTypes;
}
