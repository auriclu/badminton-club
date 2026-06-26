package com.club.badminton.dao;

import com.club.badminton.config.CustomConnectionPool;
import com.club.badminton.exception.DatabaseAccessException;
import com.club.badminton.model.User;
import com.club.badminton.model.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO for managing users
 */
public class UserDao extends BaseDao {

    public UserDao(CustomConnectionPool connectionPool) {
        super(connectionPool);
    }

    /**
     * Save a new User record in the database
     * @param user
     * @return
     */
    public User save(User user) {
        String sql = "INSERT INTO users (first_name, last_name, email, password_hash, role, has_own_racket) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getRole().name());
            ps.setBoolean(6, user.isHasOwnRacket());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setUserId(keys.getInt(1));
                }
            }
            return user;
        } catch (SQLException e) {
            throw new DatabaseAccessException("Error saving new user configuration to persistence context", e);
        } finally {
            releaseConnection(conn);
        }
    }

    /**
     * Look up a user based on their email
     * @param email
     * @return
     */
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed running query target findByEmail", e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    /**
     * Look up a user based on their id (PK)
     * @param id
     * @return
     */
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Error locating user records by ID", e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(Role.valueOf(rs.getString("role")));
        user.setHasOwnRacket(rs.getBoolean("has_own_racket"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }

    /**
     * Gets a list of all registrered users
     * @return
     */
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Error retrieving all user records", e);
        } finally {
            releaseConnection(conn);
        }
        return users;
    }

    /**
     * Update existing user's role
     * @param user
     */
    public void update(User user) {
        String sql = "UPDATE users SET role = ? WHERE user_id = ?";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getRole().name());
            ps.setInt(2, user.getUserId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseAccessException("Error updating user role for ID: " + user.getUserId(), e);
        } finally {
            releaseConnection(conn);
        }
    }

    /**
     * Get all user with Member role
     * @return
     */
    public List<User> findAllActiveMembers() {
        String sql = "SELECT user_id, first_name, last_name, email, role FROM users WHERE role = 'MEMBER' ORDER BY last_name, first_name";
        List<User> users = new ArrayList<>();
        Connection conn = getConnection(); // Clean instantiation matching other class methods

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));

                String roleStr = rs.getString("role");
                if (roleStr != null) {
                    user.setRole(Role.valueOf(roleStr.toUpperCase()));
                }

                users.add(user);
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Error executing targeted pool extraction for active database members", e);
        } finally {
            releaseConnection(conn);
        }
        return users;
    }
}