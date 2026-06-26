package com.club.badminton.service;

import com.club.badminton.dao.EventDao;
import com.club.badminton.model.Event;
import com.club.badminton.model.EventRegistration;
import com.club.badminton.model.RegistrationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ApprovalStrategyFactoryTest {

    private EventDao eventDao;
    private ApprovalStrategyFactory factory;

    @BeforeEach
    void setUp() {
        eventDao = mock(EventDao.class);
        factory = new ApprovalStrategyFactory(eventDao);
    }

    @Test
    @DisplayName("Positive: Should set status to APPROVED when capacity is not reached")
    void shouldApproveWhenUnderCapacity() {
        Event event = new Event();
        event.setEventId(1);
        event.setMaxParticipants(10);
        EventRegistration reg = new EventRegistration();

        // Mock: 5 participants currently registered out of 10
        when(eventDao.countParticipantsByEventId(1)).thenReturn(5);

        factory.approve(reg, event);

        assertEquals(RegistrationStatus.APPROVED, reg.getStatus());
        verify(eventDao, times(1)).countParticipantsByEventId(1);
    }

    @Test
    @DisplayName("Boundary/Negative: Should set status to PENDING (Waitlist) when capacity is exactly reached")
    void shouldSetPendingWhenAtCapacity() {
        Event event = new Event();
        event.setEventId(2);
        event.setMaxParticipants(10);
        EventRegistration reg = new EventRegistration();

        // Mock: 10 participants currently registered out of 10 (Full)
        when(eventDao.countParticipantsByEventId(2)).thenReturn(10);

        factory.approve(reg, event);

        assertEquals(RegistrationStatus.PENDING, reg.getStatus());
    }

    @Test
    @DisplayName("Negative: Should set status to PENDING when capacity is exceeded")
    void shouldSetPendingWhenOverCapacity() {
        Event event = new Event();
        event.setEventId(3);
        event.setMaxParticipants(10);
        EventRegistration reg = new EventRegistration();

        // Mock: Somehow 12 participants are registered out of 10
        when(eventDao.countParticipantsByEventId(3)).thenReturn(12);

        factory.approve(reg, event);

        assertEquals(RegistrationStatus.PENDING, reg.getStatus());
    }
}