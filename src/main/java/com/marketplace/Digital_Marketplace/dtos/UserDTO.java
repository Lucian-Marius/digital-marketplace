package com.marketplace.Digital_Marketplace.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private Boolean enabled;
}
