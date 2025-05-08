package com.jokeserver.model;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User implements Serializable {
    private int userId;
    private String username;
    private String password; // Hashed password
    private String email;
    private String accountType; // regular, creator, moderator, admin
    private LocalDateTime creationDate;
    private LocalDateTime lastLogin;
    private boolean isActive;
    private String preferences; // JSON string
}