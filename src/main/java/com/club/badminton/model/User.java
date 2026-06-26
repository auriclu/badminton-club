package com.club.badminton.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {
    private int userId;
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    private Role role;
    private boolean hasOwnRacket;
    private LocalDateTime createdAt;

    public User() {}

    public User(int userId, String firstName, String lastName, String email, String passwordHash, Role role, boolean hasOwnRacket, LocalDateTime createdAt) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.hasOwnRacket = hasOwnRacket;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public boolean isHasOwnRacket() { return hasOwnRacket; }
    public void setHasOwnRacket(boolean hasOwnRacket) { this.hasOwnRacket = hasOwnRacket; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, email);
    }
}