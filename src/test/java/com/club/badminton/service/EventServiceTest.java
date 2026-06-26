package com.club.badminton.service;

import com.club.badminton.dao.EventDao;
import com.club.badminton.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceTest {

    private EventDao eventDao;
    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventDao = mock(EventDao.class);
        eventService = new EventService(eventDao);
    }

    @Test
    @DisplayName("Positive: Should return all upcoming events")
    void findAllUpcoming_ShouldReturnList() {
        Event event = new Event();
        event.setEventId(1);
        event.setName("Morning Free Play");

        when(eventDao.findAllUpcoming()).thenReturn(List.of(event));

        List<Event> result = eventService.findAllUpcoming();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Morning Free Play", result.get(0).getName());
        verify(eventDao, times(1)).findAllUpcoming();
    }

    @Test
    @DisplayName("Positive: Should return all events from database")
    void findAll_ShouldReturnFullList() {
        Event e1 = new Event();
        Event e2 = new Event();
        when(eventDao.findAll()).thenReturn(List.of(e1, e2));

        List<Event> result = eventService.findAll();

        assertEquals(2, result.size());
        verify(eventDao, times(1)).findAll();
    }

    @Test
    @DisplayName("Boundary: Should return empty list if no upcoming events exist")
    void findAllUpcoming_ShouldReturnEmptyList_WhenNoneExist() {
        when(eventDao.findAllUpcoming()).thenReturn(Collections.emptyList());

        List<Event> result = eventService.findAllUpcoming();

        assertTrue(result.isEmpty());
    }
}