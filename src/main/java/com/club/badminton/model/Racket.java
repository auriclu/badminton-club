package com.club.badminton.model;

public class Racket {
    private int racketId;
    private String brand;
    private String model;
    private String serialNumber;
    private String condition; // 'GOOD', 'NEEDS_RESTRING', 'BROKEN'
    private String status;    // 'AVAILABLE', 'TEMPORARILY_BORROWED', 'PERMANENTLY_BORROWED'
    private Integer assignedMemberId;
    private User assignedMember;
    // Getters and Setters
    public int getRacketId() { return racketId; }
    public void setRacketId(int racketId) { this.racketId = racketId; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getAssignedMemberId() { return assignedMemberId; }
    public void setAssignedMemberId(Integer assignedMemberId) { this.assignedMemberId = assignedMemberId; }
    public User getAssignedMember() { return assignedMember; }
    public void setAssignedMember(User assignedMember) { this.assignedMember = assignedMember; }
}