package com.club.badminton.service;

import com.club.badminton.dao.RacketDao;
import com.club.badminton.exception.BusinessValidationException;
import com.club.badminton.model.Racket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RacketServiceTest {

    private RacketDao racketDao;
    private RacketService racketService;

    @BeforeEach
    void setUp() {
        racketDao = mock(RacketDao.class);
        racketService = new RacketService(racketDao);
    }

    @Test
    @DisplayName("Positive: Should delegate save to DAO when adding new racket")
    void addNewRacket_ShouldCallDaoSave() {
        Racket racket = new Racket();

        racketService.addNewRacket(racket);

        verify(racketDao, times(1)).save(racket);
    }

    @Test
    @DisplayName("Positive: Should return list of all rackets")
    void getAllRackets_ShouldReturnList() {
        Racket r1 = new Racket();
        when(racketDao.findAll()).thenReturn(List.of(r1));

        List<Racket> result = racketService.getAllRackets();

        assertEquals(1, result.size());
        verify(racketDao, times(1)).findAll();
    }

    @Test
    @DisplayName("Positive: Should assign permanent racket with correct parameters")
    void assignPermanentRacket_ShouldCallDaoUpdate() {
        racketService.assignPermanentRacket(10, 5);

        verify(racketDao, times(1)).updateStatus(10, "PERMANENTLY_BORROWED", 5);
    }

    @Test
    @DisplayName("Positive: Should handle AVAILABLE status in update branch")
    void updateRacketStatus_WhenAvailable_ShouldCallDaoWithNullMember() {
        racketService.updateRacketStatus(10, "AVAILABLE");

        verify(racketDao, times(1)).updateStatus(10, "AVAILABLE", null);
    }

    @Test
    @DisplayName("Positive: Should handle non-AVAILABLE status in update branch")
    void updateRacketStatus_WhenOtherStatus_ShouldCallDaoWithNullMember() {
        racketService.updateRacketStatus(10, "BROKEN");

        verify(racketDao, times(1)).updateStatus(10, "BROKEN", null);
    }

    @Test
    @DisplayName("Positive: Should successfully reserve racket when one is available")
    void autoReserveRacketForGuest_Success_ShouldReturnRacketId() {
        when(racketDao.findFirstAvailableRacket()).thenReturn(42);

        int reservedId = racketService.autoReserveRacketForGuest();

        assertEquals(42, reservedId);
        verify(racketDao, times(1)).updateStatus(42, "TEMPORARILY_BORROWED", null);
    }

    @Test
    @DisplayName("Negative: Should throw BusinessValidationException when no available rackets left")
    void autoReserveRacketForGuest_NoRackets_ShouldThrowException() {
        when(racketDao.findFirstAvailableRacket()).thenReturn(null);

        BusinessValidationException exception = assertThrows(
                BusinessValidationException.class,
                () -> racketService.autoReserveRacketForGuest()
        );

        assertEquals("Cannot complete booking: No available rental rackets left in stock.", exception.getMessage());

        verify(racketDao, never()).updateStatus(anyInt(), anyString(), any());
    }
}