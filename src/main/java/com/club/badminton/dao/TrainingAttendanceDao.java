package com.club.badminton.dao;

import com.club.badminton.config.CustomConnectionPool;
import com.club.badminton.model.TrainingAttendance;
import com.club.badminton.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for managing TrainingAttendance records
 */
@Repository
public class TrainingAttendanceDao extends BaseDao {

    @Autowired
    public TrainingAttendanceDao(CustomConnectionPool connectionPool) {
        super(connectionPool);
    }

    /**
     * Get attendance for a specific training date
     * @param date
     * @return
     */
    public List<TrainingAttendance> findByDate(String date) {
        String sql = "SELECT ta.*, u.first_name, u.last_name, u.email " +
                "FROM training_attendance ta " +
                "JOIN users u ON ta.user_id = u.user_id " +
                "WHERE ta.training_date = CAST(? AS DATE) " +
                "ORDER BY u.last_name ASC";

        List<TrainingAttendance> list = new ArrayList<>();
        Connection conn = null;

        try {
            conn = getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, date);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        TrainingAttendance ta = new TrainingAttendance();
                        ta.setId(rs.getInt("id"));
                        ta.setUserId(rs.getInt("user_id"));
                        ta.setTrainingDate(rs.getDate("training_date").toLocalDate());
                        ta.setStatus(rs.getString("status"));

                        User user = new User();
                        user.setUserId(rs.getInt("user_id"));
                        user.setFirstName(rs.getString("first_name"));
                        user.setLastName(rs.getString("last_name"));
                        user.setEmail(rs.getString("email"));
                        ta.setUser(user);

                        list.add(ta);
                    }
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
     * Get distinct list of dates
     * @return
     */
    public List<LocalDate> findDistinctTrainingDates() {
        String sql = "SELECT DISTINCT training_date FROM training_attendance ORDER BY training_date DESC";
        List<LocalDate> dates = new ArrayList<>();
        Connection conn = null;

        try {
            conn = getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date sqlDate = rs.getDate("training_date");
                    if (sqlDate != null) {
                        dates.add(sqlDate.toLocalDate());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) releaseConnection(conn);
        }
        return dates;
    }

    /**
     * Check if a training sessions has been initialized for a specific date
     * @param date
     * @return
     */
    public boolean existsByTrainingDate(String date) {
        String sql = "SELECT EXISTS(SELECT 1 FROM training_attendance WHERE training_date = CAST(? AS DATE))";
        Connection conn = null;
        try {
            conn = getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, date);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getBoolean(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) releaseConnection(conn);
        }
        return false;
    }

    /**
     * Initialize training session
     * @param date
     * @param activeMembers
     */
    public void initializeRosterForDate(String date, List<User> activeMembers) {
        String sql = "INSERT INTO training_attendance (user_id, training_date, status) VALUES (?, CAST(? AS DATE), 'PENDING')";
        Connection conn = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Enable batch transaction management

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (User member : activeMembers) {
                    ps.setInt(1, member.getUserId());
                    ps.setString(2, date);
                    ps.addBatch();
                }
                ps.executeBatch();
                conn.commit();
            } catch (SQLException batchException) {
                conn.rollback();
                throw batchException;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) releaseConnection(conn);
        }
    }

    /**
     * Update status of individual attendance record
     * @param id
     * @param status
     */
    public void updateStatus(int id, String status) {
        String sql = "UPDATE training_attendance SET status = ? WHERE id = ?";
        Connection conn = null;
        try {
            conn = getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, status);
                ps.setInt(2, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) releaseConnection(conn);
        }
    }

    /**
     * Perform a bulk update, status of all records for a given date is set to CANCELLED
     * @param date
     */
    public void cancelSessionByDate(String date) {
        String sql = "UPDATE training_attendance SET status = 'CANCELLED' WHERE training_date = CAST(? AS DATE)";
        Connection conn = null;
        try {
            conn = getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, date);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) releaseConnection(conn);
        }
    }

    /**
     * Get training history for specific user
     * @param userId
     * @return
     */
    public List<TrainingAttendance> findByUserId(int userId) {
        String sql = "SELECT ta.*, u.first_name, u.last_name, u.email " +
                "FROM training_attendance ta " +
                "JOIN users u ON ta.user_id = u.user_id " +
                "WHERE ta.user_id = ? " +
                "ORDER BY ta.training_date DESC";

        List<TrainingAttendance> list = new ArrayList<>();
        Connection conn = null;

        try {
            conn = getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        TrainingAttendance ta = new TrainingAttendance();
                        ta.setId(rs.getInt("id"));
                        ta.setUserId(rs.getInt("user_id"));
                        ta.setTrainingDate(rs.getDate("training_date").toLocalDate());
                        ta.setStatus(rs.getString("status"));

                        User user = new User();
                        user.setUserId(rs.getInt("user_id"));
                        user.setFirstName(rs.getString("first_name"));
                        user.setLastName(rs.getString("last_name"));
                        user.setEmail(rs.getString("email"));
                        ta.setUser(user);

                        list.add(ta);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) releaseConnection(conn);
        }
        return list;
    }
}