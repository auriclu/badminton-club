package com.club.badminton.dao;

import com.club.badminton.config.CustomConnectionPool;
import com.club.badminton.model.Event;
import com.club.badminton.model.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventDaoTest {

    private CustomConnectionPool pool;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private EventDao eventDao;

    @BeforeEach
    void setUp() throws Exception {
        pool = mock(CustomConnectionPool.class);
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);

        when(pool.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        java.sql.Date mockDate = java.sql.Date.valueOf(java.time.LocalDate.now());
        java.sql.Timestamp mockTimestamp = java.sql.Timestamp.valueOf(java.time.LocalDateTime.now());
        java.sql.Time mockTime = java.sql.Time.valueOf(java.time.LocalTime.now());

        when(resultSet.getDate(anyString())).thenReturn(mockDate);
        when(resultSet.getTimestamp(anyString())).thenReturn(mockTimestamp);
        when(resultSet.getTime(anyString())).thenReturn(mockTime);

        String dynamicEnumName = EventType.values()[0].name();
        when(resultSet.getString("event_type")).thenReturn(dynamicEnumName);
        when(resultSet.getString("name")).thenReturn("Badminton Match");
        when(resultSet.getString("description")).thenReturn("Regular club training session");

        eventDao = new EventDao(pool);
    }

    @Test
    @DisplayName("Positive: Should parse and return upcoming events list")
    void findAllUpcoming_ShouldReturnEvents() throws Exception {
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("event_id")).thenReturn(1, 2);

        List<Event> events = eventDao.findAllUpcoming();

        assertNotNull(events);
        assertEquals(2, events.size());
        assertEquals("Badminton Match", events.get(0).getName());
    }

    @Test
    @DisplayName("Positive: Should find event by ID and return Optional with data")
    void findById_ShouldReturnOptionalEvent_WhenExists() throws Exception {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("event_id")).thenReturn(42);

        Optional<Event> result = eventDao.findById(42);

        assertTrue(result.isPresent());
        assertEquals(42, result.get().getEventId());
    }

    @Test
    @DisplayName("Negative: Should return empty Optional when event ID does not exist")
    void findById_ShouldReturnEmptyOptional_WhenNotExists() throws Exception {
        when(resultSet.next()).thenReturn(false);

        Optional<Event> result = eventDao.findById(999);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Positive: Should compute total approved or pending participants count")
    void countParticipantsByEventId_ShouldReturnCount() throws Exception {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(12);

        int count = eventDao.countParticipantsByEventId(1);

        assertEquals(12, count);
        verify(preparedStatement, times(1)).setInt(1, 1);
    }

    @Test
    @DisplayName("Positive: Should map all fields and call executeUpdate on save")
    void save_ShouldInsertEventIntoDb() throws Exception {
        Event event = new Event();
        event.setName("Club Tournament");
        event.setDescription("Open stage");
        event.setEventDate(java.time.LocalDate.now());
        event.setStartTime(java.time.LocalTime.now());
        event.setEndTime(java.time.LocalTime.now());
        event.setEventType(EventType.values()[0]);
        event.setRequiresPartner(false);
        event.setMaxParticipants(24);
        event.setRegistrationDeadline(java.time.LocalDateTime.now());

        assertDoesNotThrow(() -> eventDao.save(event));
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    @DisplayName("Positive: Should return all historical and future events")
    void findAll_ShouldReturnFullChronologicalList() throws Exception {
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("event_id")).thenReturn(100);

        List<Event> result = eventDao.findAll();

        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getEventId());
    }

    @Test
    @DisplayName("Positive: Should call executeUpdate when deleting event by ID")
    void deleteById_ShouldExecuteDeleteQuery() throws Exception {
        assertDoesNotThrow(() -> eventDao.deleteById(5));
        verify(preparedStatement, times(1)).executeUpdate();
    }
}