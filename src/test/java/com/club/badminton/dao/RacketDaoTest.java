package com.club.badminton.dao;

import com.club.badminton.config.CustomConnectionPool;
import com.club.badminton.model.Racket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RacketDaoTest {

    private CustomConnectionPool pool;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private RacketDao racketDao;

    @BeforeEach
    void setUp() throws Exception {
        pool = mock(CustomConnectionPool.class);
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);

        when(pool.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        racketDao = new RacketDao(pool);
    }

    @Test
    @DisplayName("Positive: Should successfully execute insert statement for a new racket")
    void save_ShouldExecuteUpdate() throws Exception {
        Racket racket = new Racket();
        racket.setBrand("YONEX");
        racket.setModel("Astrox 99");
        racket.setSerialNumber("SN12345");

        assertDoesNotThrow(() -> racketDao.save(racket));

        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    @DisplayName("Positive: Should map and return list of rackets with assigned members")
    void findAll_ShouldReturnRacketsList() throws Exception {
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("racket_id")).thenReturn(1);
        when(resultSet.getString("brand")).thenReturn("Victor");
        when(resultSet.getString("model")).thenReturn("Thruster");
        when(resultSet.getInt("assigned_member_id")).thenReturn(10);
        when(resultSet.wasNull()).thenReturn(false); // Член клуба привязан к ракетке
        when(resultSet.getString("first_name")).thenReturn("Altyn");

        List<Racket> rackets = racketDao.findAll();

        assertNotNull(rackets);
        assertEquals(1, rackets.size());
        assertEquals("Victor", rackets.get(0).getBrand());
        assertNotNull(rackets.get(0).getAssignedMember());
        assertEquals("Altyn", rackets.get(0).getAssignedMember().getFirstName());
    }

    @Test
    @DisplayName("Positive: Should update status and set member ID to null when it is 0 or null")
    void updateStatus_ShouldSetNullMemberId() throws Exception {
        assertDoesNotThrow(() -> racketDao.updateStatus(1, "AVAILABLE", null));

        verify(preparedStatement).setNull(2, Types.INTEGER);
        verify(preparedStatement).executeUpdate();
    }

    @Test
    @DisplayName("Positive: Should update status with specific member ID when provided")
    void updateStatus_ShouldSetValidMemberId() throws Exception {
        assertDoesNotThrow(() -> racketDao.updateStatus(1, "ASSIGNED", 5));

        verify(preparedStatement).setInt(2, 5);
        verify(preparedStatement).executeUpdate();
    }

    @Test
    @DisplayName("Positive: Should return first available racket ID if found")
    void findFirstAvailableRacket_ShouldReturnId() throws Exception {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("racket_id")).thenReturn(7);

        Integer result = racketDao.findFirstAvailableRacket();

        assertEquals(7, result);
    }

    @Test
    @DisplayName("Negative: Should return null if no rackets are available")
    void findFirstAvailableRacket_ShouldReturnNull_WhenNoneAvailable() throws Exception {
        when(resultSet.next()).thenReturn(false);

        Integer result = racketDao.findFirstAvailableRacket();

        assertNull(result);
    }
}