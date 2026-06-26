package com.club.badminton.dao;

import com.club.badminton.config.CustomConnectionPool;
import com.club.badminton.model.Role;
import com.club.badminton.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDaoTest {

    private CustomConnectionPool pool;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private UserDao userDao;

    @BeforeEach
    void setUp() throws Exception {
        pool = mock(CustomConnectionPool.class);
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        java.sql.Timestamp mockTimestamp = java.sql.Timestamp.valueOf(java.time.LocalDateTime.now());
        when(resultSet.getTimestamp(anyString())).thenReturn(mockTimestamp);

        when(pool.getConnection()).thenReturn(connection);
        userDao = new UserDao(pool);
    }

    @Test
    @DisplayName("Positive: Should find user by ID and map all fields")
    void findById_ShouldReturnUser_WhenExists() throws Exception {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("user_id")).thenReturn(7);
        when(resultSet.getString("role")).thenReturn("MEMBER");

        Optional<User> result = userDao.findById(7);

        assertTrue(result.isPresent());
        assertEquals(7, result.get().getUserId());
        assertEquals(Role.MEMBER, result.get().getRole());
    }

    @Test
    @DisplayName("Negative: Should return empty Optional if user not found in DB")
    void findById_ShouldReturnEmpty_WhenNotExists() throws Exception {
        when(resultSet.next()).thenReturn(false);

        Optional<User> result = userDao.findById(999);

        assertFalse(result.isPresent());
    }
}