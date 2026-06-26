package com.club.badminton.dao;

import com.club.badminton.config.CustomConnectionPool;
import com.club.badminton.exception.DatabaseAccessException;
import com.club.badminton.model.EventRegistration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for managing event registrations
 */
public class EventRegistrationDao extends BaseDao {

    public EventRegistrationDao(CustomConnectionPool connectionPool) {
        super(connectionPool);
    }

    /**
     * Execute database transaction that saves an event registration and link a racket reservation if required
     * @param reg
     * @param racketId
     */
    public void saveWithRacketReservation(EventRegistration reg, int racketId) {
        String insertRegSql = "INSERT INTO event_registrations (event_id, user_id, guest_name, guest_email, guest_phone, skill_level, needs_racket, partner_name, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String insertReserveSql = "INSERT INTO racket_reservations (racket_id, registration_id, reservation_type, start_date, status) VALUES (?, ?, 'EVENT', CURRENT_DATE, 'ACTIVE')";

        Connection conn = getConnection();
        try {
            conn.setAutoCommit(false);

            int regId;
            try (PreparedStatement psReg = conn.prepareStatement(insertRegSql, Statement.RETURN_GENERATED_KEYS)) {
                psReg.setInt(1, reg.getEventId());
                if (reg.getUserId() != null) psReg.setInt(2, reg.getUserId()); else psReg.setNull(2, Types.INTEGER);
                psReg.setString(3, reg.getGuestName());
                psReg.setString(4, reg.getGuestEmail());
                psReg.setString(5, reg.getGuestPhone());
                psReg.setString(6, reg.getSkillLevel());
                psReg.setBoolean(7, reg.isNeedsRacket());
                psReg.setString(8, reg.getPartnerName());
                psReg.setString(9, reg.getStatus().name());

                psReg.executeUpdate();
                try (ResultSet keys = psReg.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("Registration record tracking failure.");
                    regId = keys.getInt(1);
                    reg.setRegistrationId(regId);
                }
            }

            if (reg.isNeedsRacket() && racketId > 0) {
                try (PreparedStatement psReserve = conn.prepareStatement(insertReserveSql)) {
                    psReserve.setInt(1, racketId);
                    psReserve.setInt(2, regId);
                    psReserve.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                throw new DatabaseAccessException("Transaction failure followed by catastrophic roll back failure", rollbackEx);
            }
            throw new DatabaseAccessException("Atomic event registration operation aborted due to processing anomalies", e);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
            }
            releaseConnection(conn);
        }
    }

    /**
     * Get a specific event registration by its primary key
     * @param id
     * @return
     */
    public EventRegistration findById(Integer id) {
        if (id == null) return null;

        String sql = "SELECT * FROM event_registrations WHERE registration_id = ?";
        Connection conn = getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRowToRegistration(rs) : null;
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to query registration by ID: " + id, e);
        } finally {
            releaseConnection(conn);
        }
    }

    /**
     * Helper method to map a JDBC row to an EventRegistration
     * @param rs
     * @return
     * @throws SQLException
     */
    private EventRegistration mapRowToRegistration(ResultSet rs) throws SQLException {
        EventRegistration reg = new EventRegistration();
        reg.setRegistrationId(rs.getInt("registration_id"));
        reg.setEventId(rs.getInt("event_id"));

        int userId = rs.getInt("user_id");
        if (!rs.wasNull()) reg.setUserId(userId);

        reg.setGuestName(rs.getString("guest_name"));
        reg.setGuestEmail(rs.getString("guest_email"));
        reg.setGuestPhone(rs.getString("guest_phone"));
        reg.setSkillLevel(rs.getString("skill_level"));
        reg.setNeedsRacket(rs.getBoolean("needs_racket"));
        reg.setPartnerName(rs.getString("partner_name"));
        reg.setStatus(com.club.badminton.model.RegistrationStatus.valueOf(rs.getString("status")));

        return reg;
    }

    /**
     * Get all event registrations
     * @return
     */
    public List<EventRegistration> findAll() {
        String sql = "SELECT * FROM event_registrations ORDER BY registration_id DESC";
        List<EventRegistration> registrations = new ArrayList<>();
        Connection conn = getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                registrations.add(mapRowToRegistration(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to retrieve all registrations", e);
        } finally {
            releaseConnection(conn);
        }

        return registrations;
    }

    /**
     * Delete and event registration by id from the database
     * @param id
     */
    public void deleteById(int id) {
        String sql = "DELETE FROM event_registrations WHERE registration_id = ?";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseAccessException("Error deleting registration with ID: " + id, e);
        } finally {
            releaseConnection(conn);
        }
    }

    /**
     * Get all registrations according to a specific event id
     * @param eventId
     * @return
     */
    public List<EventRegistration> findAllByEventId(int eventId) {
        String sql = "SELECT * FROM event_registrations WHERE event_id = ?";
        List<EventRegistration> registrations = new ArrayList<>();
        Connection conn = getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    registrations.add(mapRowToRegistration(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Error fetching registrations for event: " + eventId, e);
        } finally {
            releaseConnection(conn);
        }
        return registrations;
    }

    /**
     * Update an existing event registration with modified data fields
     * @param reg
     */
    public void update(EventRegistration reg) {
        String sql = "UPDATE event_registrations SET event_id = ?, user_id = ?, guest_name = ?, guest_email = ?, " +
                "guest_phone = ?, skill_level = ?, needs_racket = ?, partner_name = ?, status = ? " +
                "WHERE registration_id = ?";

        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reg.getEventId());

            if (reg.getUserId() != null) {
                ps.setInt(2, reg.getUserId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            ps.setString(3, reg.getGuestName());
            ps.setString(4, reg.getGuestEmail());
            ps.setString(5, reg.getGuestPhone());
            ps.setString(6, reg.getSkillLevel());
            ps.setBoolean(7, reg.isNeedsRacket());
            ps.setString(8, reg.getPartnerName());
            ps.setString(9, reg.getStatus().name()); // Сюда запишется "APPROVED"
            ps.setInt(10, reg.getRegistrationId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseAccessException("Error updating registration with ID: " + reg.getRegistrationId(), e);
        } finally {
            releaseConnection(conn);
        }
    }
}