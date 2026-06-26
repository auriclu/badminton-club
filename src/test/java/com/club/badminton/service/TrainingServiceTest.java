package com.club.badminton.service;

import com.club.badminton.dao.TrainingAttendanceDao;
import com.club.badminton.model.TrainingAttendance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceTest {

    private TrainingAttendanceDao trainingAttendanceDao;
    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        trainingAttendanceDao = mock(TrainingAttendanceDao.class);
        trainingService = new TrainingService(trainingAttendanceDao);
    }

    @Test
    @DisplayName("Positive: Should fetch training attendance roster for a specific date")
    void findAllByDate_ShouldReturnRosterList() {
        TrainingAttendance record = new TrainingAttendance();
        when(trainingAttendanceDao.findByDate("2026-06-26")).thenReturn(List.of(record));

        List<TrainingAttendance> result = trainingService.findAllByDate("2026-06-26");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(trainingAttendanceDao, times(1)).findByDate("2026-06-26");
    }

    @Test
    @DisplayName("Positive: Should update status to PRESENT for individual record")
    void markAsPresent_ShouldCallDaoUpdate() {
        trainingService.markAsPresent(77);

        verify(trainingAttendanceDao, times(1)).updateStatus(77, "PRESENT");
    }

    @Test
    @DisplayName("Positive: Should bulk-cancel training session for given date")
    void cancelTrainingSession_ShouldCallDaoCancel() {
        trainingService.cancelTrainingSession("2026-06-26");

        verify(trainingAttendanceDao, times(1)).cancelSessionByDate("2026-06-26");
    }
}