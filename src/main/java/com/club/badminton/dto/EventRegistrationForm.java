package com.club.badminton.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EventRegistrationForm {

    private int registrationId;
    private int eventId;
    private Integer userId;
    private String status = "PENDING";
    @Size(max = 100, message = "{validation.name.size}")
    private String guestName;

    @Email(message = "{validation.email.invalid}")
    private String guestEmail;

    @Size(max = 20, message = "{validation.phone.size}")
    private String guestPhone;

    @NotBlank(message = "{validation.skill.required}")
    private String skillLevel;

    private boolean needsRacket;
    private String partnerName;

    // --- Your Existing Getters and Setters ---
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public String getGuestEmail() { return guestEmail; }
    public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }
    public String getGuestPhone() { return guestPhone; }
    public void setGuestPhone(String guestPhone) { this.guestPhone = guestPhone; }
    public String getSkillLevel() { return skillLevel; }
    public void setSkillLevel(String skillLevel) { this.skillLevel = skillLevel; }
    public boolean isNeedsRacket() { return needsRacket; }
    public void setNeedsRacket(boolean needsRacket) { this.needsRacket = needsRacket; }
    public String getPartnerName() { return partnerName; }
    public void setPartnerName(String partnerName) { this.partnerName = partnerName; }

    public int getRegistrationId() { return registrationId; }
    public void setRegistrationId(int registrationId) { this.registrationId = registrationId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}