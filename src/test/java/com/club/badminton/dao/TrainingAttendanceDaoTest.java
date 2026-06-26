package com.club.badminton.dao;

import com.club.badminton.config.CustomConnectionPool;
import com.club.badminton.model.TrainingAttendance;
import com.club.badminton.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingAttendanceDaoTest {

    private CustomConnectionPool pool;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private TrainingAttendanceDao attendanceDao;

    @BeforeEach
    void setUp() throws Exception {
        pool = mock(CustomConnectionPool.class);
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);

        when(pool.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Базовая заглушка для SQL даты, чтобы избежать NPE во время маппинга строк
        when(resultSet.getDate(anyString())).thenReturn(Date.valueOf(LocalDate.now()));

        attendanceDao = new TrainingAttendanceDao(pool);
    }

    @Test
    @DisplayName("Positive: Should return attendance roster for a specific date")
    void findByDate_ShouldReturnRoster() throws Exception {
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id")).thenReturn(100);
        when(resultSet.getString("status")).thenReturn("PRESENT");
        when(resultSet.getString("first_name")).thenReturn("John");

        List<TrainingAttendance> result = attendanceDao.findByDate("2026-06-26");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("PRESENT", result.get(0).getStatus());
        assertEquals("John", result.get(0).getUser().getFirstName());
    }

    @Test
    @DisplayName("Positive: Should parse distinct training dates chronologically")
    void findDistinctTrainingDates_ShouldReturnLocalDates() throws Exception {
        Date firstDate = Date.valueOf(LocalDate.of(2026, 6, 25));
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getDate("training_date")).thenReturn(firstDate);

        List<LocalDate> dates = attendanceDao.findDistinctTrainingDates();

        assertNotNull(dates);
        assertEquals(1, dates.size());
        assertEquals(LocalDate.of(2026, 6, 25), dates.get(0));
    }

    @Test
    @DisplayName("Positive: Should check if training roster exists for given date")
    void existsByTrainingDate_ShouldReturnTrue_WhenExists() throws Exception {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getBoolean(1)).thenReturn(true);

        boolean exists = attendanceDao.existsByTrainingDate("2026-06-26");

        assertTrue(exists);
    }

    @Test
    @DisplayName("Positive: Should execute batch commands and commit transaction during initialization")
    void initializeRosterForDate_ShouldExecuteBatchAndCommit() throws Exception {
        User u1 = new User();
        u1.setUserId(1);
        User u2 = new User();
        u2.setUserId(2);

        assertDoesNotThrow(() -> attendanceDao.initializeRosterForDate("2026-06-26", List.of(u1, u2)));

        verify(connection).setAutoCommit(false);
        verify(preparedStatement, times(2)).addBatch();
        verify(preparedStatement).executeBatch();
        verify(connection).commit();
    }

    @Test
    @DisplayName("Positive: Should run update query to change single record status")
    void updateStatus_ShouldExecuteUpdate() throws Exception {
        assertDoesNotThrow(() -> attendanceDao.updateStatus(50, "ABSENT"));
        verify(preparedStatement).executeUpdate();
    }

    @Test
    @DisplayName("Positive: Should bulk-update all records to CANCELLED for specific date")
    void cancelSessionByDate_ShouldExecuteUpdate() throws Exception {
        assertDoesNotThrow(() -> attendanceDao.cancelSessionByDate("2026-06-26"));
        verify(preparedStatement).executeUpdate();
    }

    @Test
    @DisplayName("Positive: Should extract and return personal training history for user")
    void findByUserId_ShouldReturnHistoryList() throws Exception {
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("user_id")).thenReturn(7);
        when(resultSet.getString("status")).thenReturn("PRESENT");

        List<TrainingAttendance> history = attendanceDao.findByUserId(7);

        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals(7, history.get(0).getUserId());
    }
}