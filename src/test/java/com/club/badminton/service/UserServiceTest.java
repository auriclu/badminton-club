package com.club.badminton.service;

import com.club.badminton.dao.UserDao;
import com.club.badminton.model.Role;
import com.club.badminton.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserDao dao;
    private UserService service;

    @BeforeEach
    void setup() {
        dao = mock(UserDao.class);
        service = new UserService(dao);
    }

    @Test
    @DisplayName("Positive: Should return list of active members")
    void shouldReturnMembers() {
        User u1 = new User();
        u1.setRole(Role.MEMBER);
        List<User> users = List.of(u1);

        when(dao.findAllActiveMembers()).thenReturn(users);

        List<User> result = service.findAllActiveMembers();
        assertEquals(1, result.size());
        verify(dao, times(1)).findAllActiveMembers();
    }

    @Test
    @DisplayName("Positive: Should return user when valid ID is provided")
    void shouldReturnUserById_WhenExists() {
        User mockUser = new User();
        mockUser.setUserId(5);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");

        when(dao.findById(5)).thenReturn(Optional.of(mockUser));

        Optional<User> result = service.findById(5);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());
    }

    @Test
    @DisplayName("Negative: Should return empty Optional for non-existent ID")
    void shouldReturnEmpty_WhenUserDoesNotExist() {
        when(dao.findById(999)).thenReturn(Optional.empty());

        Optional<User> result = service.findById(999);

        assertFalse(result.isPresent());
    }
}