package com.club.badminton.service;

import com.club.badminton.dao.EventRegistrationDao;
import com.club.badminton.dao.EventDao;
import com.club.badminton.exception.BusinessValidationException;
import com.club.badminton.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventRegistrationService {
    private final EventRegistrationDao registrationDao;
    private final EventDao eventDao;
    private final ApprovalStrategyFactory strategyFactory;

    public EventRegistrationService(EventRegistrationDao registrationDao,
                                    EventDao eventDao,
                                    ApprovalStrategyFactory strategyFactory) {
        this.registrationDao = registrationDao;
        this.eventDao = eventDao;
        this.strategyFactory = strategyFactory;
    }

    @Transactional
    public EventRegistration registerForEvent(EventRegistration registration, Event event, int availableRacketId) {
        validate(registration, event);

        strategyFactory.approve(registration, event);

        registrationDao.saveWithRacketReservation(registration,
                registration.isNeedsRacket() ? availableRacketId : 0);

        return registration;
    }

    private void validate(EventRegistration reg, Event event) {
        if (event.getRegistrationDeadline() != null && LocalDateTime.now().isAfter(event.getRegistrationDeadline())) {
            throw new BusinessValidationException("The registration deadline for this event has passed.");
        }

        if (eventDao.countParticipantsByEventId(event.getEventId()) >= event.getMaxParticipants()) {
            throw new BusinessValidationException("Cannot register: the maximum capacity for this event has been exceeded.");
        }

        if (event.isRequiresPartner() && (reg.getPartnerName() == null || reg.getPartnerName().isBlank())) {
            throw new BusinessValidationException("Partner name required.");
        }
    }

    public EventRegistration findById(Integer id) {
        return (id == null) ? null : registrationDao.findById(id);
    }

    public List<EventRegistration> findAll() {
        return registrationDao.findAll();
    }

    public void deleteById(int id) {
        registrationDao.deleteById(id);
    }

    public List<EventRegistration> findByEventId(int id) {
        return registrationDao.findAllByEventId(id);
    }

    @Transactional
    public void save(EventRegistration reg) {
        if (reg == null || reg.getRegistrationId() == 0) {
            throw new BusinessValidationException("Cannot update a registration without a valid ID.");
        }
        registrationDao.update(reg);
    }
}