package com.example.taskapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Enumerated(EnumType.STRING)
    public ERole role = ERole.USER_ROLE;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "profile_picture_path")
    private String profilePhotoUrl;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    public User() {
        this.creationDate = LocalDateTime.now();
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.creationDate = LocalDateTime.now();
    }
}
