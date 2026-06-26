package com.club.badminton.dao;

import com.club.badminton.config.CustomConnectionPool;
import com.club.badminton.exception.DatabaseAccessException;
import com.club.badminton.model.Event;
import com.club.badminton.model.EventType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO from managing Events
 */
public class EventDao extends BaseDao {

    public EventDao(CustomConnectionPool connectionPool) {
        super(connectionPool);
    }

    /**
     * Gets a chronological list of all events
     * @return
     */
    public List<Event> findAllUpcoming() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE event_date >= CURRENT_DATE ORDER BY event_date ASC";
        Connection conn = getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                events.add(mapRowToEvent(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to fetch upcoming club events", e);
        } finally {
            releaseConnection(conn);
        }
        return events;
    }

    /**
     * Find event by its unique id
     * @param eventId
     * @return
     */
    public Optional<Event> findById(int eventId) {
        String sql = "SELECT * FROM events WHERE event_id = ?";
        Connection conn = getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToEvent(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to query event by ID: " + eventId, e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    /**
     * Calculate total number of users registered for events
     * @param eventId
     * @return
     */
    public int countParticipantsByEventId(int eventId) {
        String sql = "SELECT COUNT(*) FROM event_registrations WHERE event_id = ? AND status != 'REJECTED'";
        Connection conn = getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to compute participant headroom counts", e);
        } finally {
            releaseConnection(conn);
        }
        return 0;
    }

    /**
     * Maps a single JDBC row to an event
     * @param rs
     * @return
     * @throws SQLException
     */
    private Event mapRowToEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setEventId(rs.getInt("event_id"));
        event.setName(rs.getString("name"));
        event.setDescription(rs.getString("description"));
        event.setEventDate(rs.getDate("event_date").toLocalDate());
        event.setStartTime(rs.getTime("start_time").toLocalTime());
        event.setEndTime(rs.getTime("end_time").toLocalTime());
        event.setEventType(EventType.valueOf(rs.getString("event_type")));
        event.setRequiresPartner(rs.getBoolean("requires_partner"));
        event.setMaxParticipants(rs.getInt("max_participants"));
        event.setRegistrationDeadline(rs.getTimestamp("registration_deadline").toLocalDateTime());
        return event;
    }

    /**
     * Insert a new created event into the database
     * @param event
     */
    public void save(Event event) {
        String sql = "INSERT INTO events (name, description, event_date, start_time, end_time, event_type, requires_partner, max_participants, registration_deadline) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.getName());
            ps.setString(2, event.getDescription());
            ps.setDate(3, Date.valueOf(event.getEventDate()));
            ps.setTime(4, Time.valueOf(event.getStartTime()));
            ps.setTime(5, Time.valueOf(event.getEndTime()));
            ps.setString(6, event.getEventType().name());
            ps.setBoolean(7, event.isRequiresPartner());
            ps.setInt(8, event.getMaxParticipants());
            ps.setTimestamp(9, Timestamp.valueOf(event.getRegistrationDeadline()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseAccessException("Error saving new event", e);
        } finally {
            releaseConnection(conn);
        }
    }

    /**
     * Get all events ordered chronologically
     * @return
     */
    public List<Event> findAll() {
        List<Event> events = new ArrayList<>();
        // Order by date to show chronologically
        String sql = "SELECT * FROM events ORDER BY event_date ASC";
        Connection conn = getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                events.add(mapRowToEvent(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to retrieve all events", e);
        } finally {
            releaseConnection(conn);
        }
        return events;
    }

    /**
     * Delete an event from the database by its id
     * @param id
     */
    public void deleteById(int id) {
        String sql = "DELETE FROM events WHERE event_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseAccessException("Could not delete event", e);
        }
    }
}