package com.example.healthcompanion;

/**
 * User model class for storing user information in Firebase
 */
public class User {
    private String userId;
    private String fullName;
    private String email;
    private long createdAt;

    // Required empty constructor for Firebase
    public User() {
    }

    public User(String userId, String fullName, String email, long createdAt) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}