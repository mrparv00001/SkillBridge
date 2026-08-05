package com.skillbridge.model;

import java.time.LocalDateTime;

public class Manager {
    private int managerId;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private LocalDateTime createdAt;

    public Manager() {}

    public Manager(int managerId, String username, String passwordHash, String fullName, String email, LocalDateTime createdAt) {
        this.managerId = managerId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.createdAt = createdAt;
    }

    public int getManagerId() { return managerId; }
    public void setManagerId(int managerId) { this.managerId = managerId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}