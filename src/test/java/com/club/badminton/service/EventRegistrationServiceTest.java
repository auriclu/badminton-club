package com.club.badminton.service;

import com.club.badminton.dao.EventDao;
import com.club.badminton.dao.EventRegistrationDao;
import com.club.badminton.exception.BusinessValidationException;
import com.club.badminton.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventRegistrationServiceTest {

    private EventRegistrationDao registrationDao;
    private EventDao eventDao;
    private ApprovalStrategyFactory strategyFactory;
    private EventRegistrationService service;

    @BeforeEach
    void setUp() {
        registrationDao = mock(EventRegistrationDao.class);
        eventDao = mock(EventDao.class);
        strategyFactory = mock(ApprovalStrategyFactory.class);
        service = new EventRegistrationService(registrationDao, eventDao, strategyFactory);
    }

    @Test
    @DisplayName("Positive: Should successfully register and pass racketId if needed")
    void registerForEvent_Success_WithRacket() {
        Event event = new Event();
        event.setEventId(1);
        event.setMaxParticipants(20);
        event.setRegistrationDeadline(LocalDateTime.now().plusDays(1));
        event.setRequiresPartner(false);

        EventRegistration registration = new EventRegistration();
        registration.setNeedsRacket(true);

        service.registerForEvent(registration, event, 99);

        verify(strategyFactory, times(1)).approve(registration, event);
        verify(registrationDao, times(1)).saveWithRacketReservation(registration, 99);
    }

    @Test
    @DisplayName("Negative: Should throw exception if registration deadline has passed")
    void registerForEvent_ThrowsException_WhenDeadlinePassed() {
        Event pastEvent = new Event();
        pastEvent.setRegistrationDeadline(LocalDateTime.now().minusHours(1));
        EventRegistration registration = new EventRegistration();

        BusinessValidationException ex = assertThrows(BusinessValidationException.class,
                () -> service.registerForEvent(registration, pastEvent, 1));

        assertTrue(ex.getMessage().contains("deadline"));
        verifyNoInteractions(registrationDao);
    }

    @Test
    @DisplayName("Negative: Should throw exception if event requires partner but name is blank")
    void registerForEvent_ThrowsException_WhenPartnerMissing() {
        Event event = new Event();
        event.setRegistrationDeadline(LocalDateTime.now().plusDays(1));
        event.setRequiresPartner(true);

        EventRegistration reg = new EventRegistration();
        reg.setPartnerName(""); // Blank partner name

        assertThrows(BusinessValidationException.class,
                () -> service.registerForEvent(reg, event, 1));

        verifyNoInteractions(registrationDao);
    }

    @Test
    @DisplayName("Positive: Should call update on DAO when saving an existing registration")
    void save_ShouldCallDaoUpdate() {
        EventRegistration reg = new EventRegistration();
        reg.setRegistrationId(10); // Existing ID

        service.save(reg);

        verify(registrationDao, times(1)).update(reg);
    }

    @Test
    @DisplayName("Positive: Should delegate findAll to DAO")
    void findAll_ShouldCallDao() {
        when(registrationDao.findAll()).thenReturn(java.util.List.of(new EventRegistration()));

        java.util.List<EventRegistration> result = service.findAll();

        assertEquals(1, result.size());
        verify(registrationDao, times(1)).findAll();
    }

    @Test
    @DisplayName("Positive: Should delegate findById to DAO")
    void findById_ShouldCallDao() {
        EventRegistration expected = new EventRegistration();
        when(registrationDao.findById(5)).thenReturn(expected);

        EventRegistration result = service.findById(5);

        assertSame(expected, result);
        verify(registrationDao, times(1)).findById(5);
    }
}