package com.harshal.auth_service.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    private String id;

    private String name;
    private String email;
    private String password;

    private String role; // "ADMIN", "STAFF", "CUSTOMER"
    private boolean active;
    private LocalDateTime createdAt;

}
