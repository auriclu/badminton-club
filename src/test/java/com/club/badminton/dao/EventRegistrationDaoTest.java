package com.club.badminton.dao;

import com.club.badminton.config.CustomConnectionPool;
import com.club.badminton.model.EventRegistration;
import com.club.badminton.model.RegistrationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventRegistrationDaoTest {

    private CustomConnectionPool pool;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private EventRegistrationDao dao;

    @BeforeEach
    void setUp() throws Exception {
        pool = mock(CustomConnectionPool.class);
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);

        when(pool.getConnection()).thenReturn(connection);
        dao = new EventRegistrationDao(pool);
    }

    @Test
    @DisplayName("Positive: Should successfully map and return registration by ID")
    void findById_ShouldReturnRegistration_WhenFound() throws Exception {
        String sql = "SELECT * FROM event_registrations WHERE registration_id = ?";

        // Настраиваем цепочку вызовов JDBC
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Симулируем, что в ResultSet есть одна строка данных
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("registration_id")).thenReturn(10);
        when(resultSet.getInt("event_id")).thenReturn(1);
        when(resultSet.getInt("user_id")).thenReturn(5);
        when(resultSet.wasNull()).thenReturn(false);
        when(resultSet.getString("status")).thenReturn("APPROVED");

        EventRegistration result = dao.findById(10);

        // Проверяем, что маппинг полей сработал корректно
        assertNotNull(result);
        assertEquals(10, result.getRegistrationId());
        assertEquals(1, result.getEventId());
        assertEquals(5, result.getUserId());
        assertEquals(RegistrationStatus.APPROVED, result.getStatus());
    }

    @Test
    @DisplayName("Positive: Should execute update when deleting record")
    void deleteById_ShouldExecuteDeleteStatement() throws Exception {
        String sql = "DELETE FROM event_registrations WHERE registration_id = ?";
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);

        assertDoesNotThrow(() -> dao.deleteById(10));

        // Проверяем, что метод executeUpdate() действительно дернули
        verify(preparedStatement, times(1)).executeUpdate();
    }
}