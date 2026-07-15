package com.skillbridge.model;

import java.time.LocalDateTime;

public class User {

    private int userId;
    private String fullName;
    private String enrollmentNo;
    private String department;
    private int semester;
    private String email;
    private String passwordHash;
    private String phone;
    private String bio;
    private boolean isActive;
    private LocalDateTime createdAt;

    // 1. No-argument constructor
    public User() {
    }

    // 2. Constructor for Registration (excluding auto-generated DB fields)
    public User(String fullName, String enrollmentNo, String department, int semester,
                String email, String passwordHash, String phone, String bio) {
        this.fullName = fullName;
        this.enrollmentNo = enrollmentNo;
        this.department = department;
        this.semester = semester;
        this.email = email;
        this.passwordHash = passwordHash;
        this.phone = phone;
        this.bio = bio;
    }

    // 3. All-arguments constructor (used when fetching from Database)
    public User(int userId, String fullName, String enrollmentNo, String department,
                int semester, String email, String passwordHash, String phone,
                String bio, boolean isActive, LocalDateTime createdAt) {
        this.userId = userId;
        this.fullName = fullName;
        this.enrollmentNo = enrollmentNo;
        this.department = department;
        this.semester = semester;
        this.email = email;
        this.passwordHash = passwordHash;
        this.phone = phone;
        this.bio = bio;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEnrollmentNo() { return enrollmentNo; }
    public void setEnrollmentNo(String enrollmentNo) { this.enrollmentNo = enrollmentNo; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "User Profile: [" +
                "ID=" + userId +
                ", Name='" + fullName + '\'' +
                ", Enrollment No='" + enrollmentNo + '\'' +
                ", Department='" + department + '\'' +
                ", Semester=" + semester +
                ", Email='" + email + '\'' +
                ", Phone='" + phone + '\'' +
                ", Bio='" + bio + '\'' +
                ", Active=" + isActive +
                ']';
    }
}