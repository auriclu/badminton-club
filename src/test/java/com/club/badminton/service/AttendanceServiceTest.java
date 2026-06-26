package com.club.badminton.service;

import com.club.badminton.dao.TrainingAttendanceDao; // Подправьте имя DAO, если у вас оно называется иначе
import com.club.badminton.model.TrainingAttendance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AttendanceServiceTest {

    private TrainingAttendanceDao attendanceDao;
    private AttendanceService attendanceService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        attendanceDao = mock(TrainingAttendanceDao.class);
        userService = mock(UserService.class);
        attendanceService = new AttendanceService(attendanceDao, userService);
    }

    @Test
    @DisplayName("Positive: Should return attendance records for a specific user")
    void findByUserId_ShouldReturnRecords() {
        TrainingAttendance record1 = new TrainingAttendance();
        record1.setUserId(42);
        record1.setStatus("PRESENT");

        TrainingAttendance record2 = new TrainingAttendance();
        record2.setUserId(42);
        record2.setStatus("ABSENT");

        when(attendanceDao.findByUserId(42)).thenReturn(List.of(record1, record2));

        List<TrainingAttendance> result = attendanceService.findByUserId(42);

        assertEquals(2, result.size());
        assertEquals("PRESENT", result.get(0).getStatus());
        verify(attendanceDao, times(1)).findByUserId(42);
    }

    @Test
    @DisplayName("Boundary/Negative: Should return empty list if user has no attendance history")
    void findByUserId_ShouldReturnEmptyList_WhenNoRecordsFound() {
        when(attendanceDao.findByUserId(999)).thenReturn(Collections.emptyList());

        List<TrainingAttendance> result = attendanceService.findByUserId(999);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}