package com.club.badminton.dao;

import com.club.badminton.config.CustomConnectionPool;
import com.club.badminton.model.Racket;
import com.club.badminton.model.User;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for managing rackets
 */
@Repository
public class RacketDao extends BaseDao {

    public RacketDao(CustomConnectionPool connectionPool) {
        super(connectionPool);
    }

    /**
     * Insert a new racket to a database
     * @param racket
     */
    public void save(Racket racket) {
        String sql = "INSERT INTO rackets (brand, model, serial_number, condition, status) VALUES (?, ?, ?, 'GOOD', 'AVAILABLE')";
        Connection conn = null;
        try {
            conn = getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, racket.getBrand());
                ps.setString(2, racket.getModel());
                ps.setString(3, racket.getSerialNumber());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) releaseConnection(conn);
        }
    }

    /**
     * Get all rackets in the system
     * @return
     */
    public List<Racket> findAll() {
        String sql = "SELECT r.*, u.first_name, u.last_name, u.email FROM rackets r " +
                "LEFT JOIN users u ON r.assigned_member_id = u.user_id " +
                "ORDER BY r.racket_id DESC";
        List<Racket> list = new ArrayList<>();
        Connection conn = null;
        try {
            conn = getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Racket r = new Racket();
                    r.setRacketId(rs.getInt("racket_id"));
                    r.setBrand(rs.getString("brand"));
                    r.setModel(rs.getString("model"));
                    r.setSerialNumber(rs.getString("serial_number"));
                    r.setCondition(rs.getString("condition"));
                    r.setStatus(rs.getString("status"));

                    int memberId = rs.getInt("assigned_member_id");
                    if (!rs.wasNull()) {
                        r.setAssignedMemberId(memberId);
                        User u = new User();
                        u.setFirstName(rs.getString("first_name"));
                        u.setLastName(rs.getString("last_name"));
                        u.setEmail(rs.getString("email"));
                        r.setAssignedMember(u);
                    }
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) releaseConnection(conn);
        }
        return list;
    }

    /**
     * Update status and ownership of a racket
     * @param racketId
     * @param status
     * @param memberId
     */
    public void updateStatus(int racketId, String status, Integer memberId) {
        String sql = "UPDATE rackets SET status = ?, assigned_member_id = ? WHERE racket_id = ?";
        Connection conn = null;
        try {
            conn = getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, status);
                if (memberId == null || memberId == 0) {
                    ps.setNull(2, Types.INTEGER);
                } else {
                    ps.setInt(2, memberId);
                }
                ps.setInt(3, racketId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) releaseConnection(conn);
        }
    }

    /**
     * Find id of the first racket marked as Available
     * @return
     */
    public Integer findFirstAvailableRacket() {
        String sql = "SELECT racket_id FROM rackets WHERE status = 'AVAILABLE' LIMIT 1";
        Connection conn = null;
        try {
            // These methods work here because RacketDao extends BaseDao
            conn = getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("racket_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                releaseConnection(conn);
            }
        }
        return null;
    }
}