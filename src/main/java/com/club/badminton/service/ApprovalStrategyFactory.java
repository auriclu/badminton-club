package com.club.badminton.service;

import com.club.badminton.dao.EventDao;
import com.club.badminton.model.Event;
import com.club.badminton.model.EventRegistration;
import com.club.badminton.model.RegistrationStatus;
import org.springframework.stereotype.Component;

@Component
public class ApprovalStrategyFactory {

    private final EventDao eventDao;

    public ApprovalStrategyFactory(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    /**
     * Applies capacity-based approval logic for ALL event types.
     */
    public void approve(EventRegistration registration, Event event) {
        int currentParticipants = eventDao.countParticipantsByEventId(event.getEventId());

        if (currentParticipants < event.getMaxParticipants()) {
            registration.setStatus(RegistrationStatus.APPROVED);
        } else {
            registration.setStatus(RegistrationStatus.PENDING);
        }
    }
}