package com.waterx.user.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Firebase UID
    @Column(unique = true, nullable = false)
    private String firebaseUid;

    private String name;
    
    @Column(unique = true)
    private String phone;
    
    @Column(unique = true)
    private String email;

    private LocalDateTime createdAt;
    
    private LocalDateTime lastLogin;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastLogin = LocalDateTime.now();
    }
}
