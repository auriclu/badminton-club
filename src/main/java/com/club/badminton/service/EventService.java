package com.club.badminton.service;

import com.club.badminton.dao.EventDao;
import com.club.badminton.exception.InvalidDateException;
import com.club.badminton.model.Event;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventService {

    private final EventDao eventDao;

    public EventService(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    @Transactional
    public void save(Event event) {
        if (event.getRegistrationDeadline().isBefore(java.time.LocalDateTime.now())) {
            throw new InvalidDateException("Deadline cannot be in the past.");
        }
        eventDao.save(event);
    }

    public List<Event> findAll() {
        return eventDao.findAll();
    }

    public Event findById(int id) {
        return eventDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + id));
    }

    @Transactional
    public void deleteById(int id) {
        eventDao.deleteById(id);
    }

    public List<Event> findAllUpcoming() {
        return eventDao.findAllUpcoming();
    }
}