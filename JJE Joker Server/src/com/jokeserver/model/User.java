package com.jokeserver.model;

import java.time.LocalDateTime;

public class User {
    private int userId;
    private String username;
    private String password; // This would be the hashed password
    private String email;
    private String accountType; // 'regular', 'creator', 'moderator', 'admin'
    private LocalDateTime creationDate;
    private LocalDateTime lastLogin;
    private boolean isActive;

    // Constructors
    public User() {}

    public User(int userId, String username, String password, String email,
                String accountType, LocalDateTime creationDate,
                LocalDateTime lastLogin, boolean isActive) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.accountType = accountType;
        this.creationDate = creationDate;
        this.lastLogin = lastLogin;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", accountType='" + accountType + '\'' +
                ", creationDate=" + creationDate +
                ", isActive=" + isActive +
                '}';
    }
}