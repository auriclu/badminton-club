package com.club.badminton.model;

import java.time.LocalDate;

public class TrainingAttendance {
    private int id;
    private int userId;
    private User user;
    private LocalDate trainingDate;
    private String status; // "PRESENT", "ABSENT", "PENDING", "EXCUSED", "CANCELLED"

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDate getTrainingDate() { return trainingDate; }
    public void setTrainingDate(LocalDate trainingDate) { this.trainingDate = trainingDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}