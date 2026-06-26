package com.club.badminton.dao;

import com.club.badminton.config.CustomConnectionPool;
import com.club.badminton.exception.DatabaseAccessException;
import java.sql.Connection;
import java.sql.SQLException; // Added import

/**
 * Base class for all DAOs
 * Provides protected methods for securely acquiring and releasing database connections
 */
public abstract class BaseDao {
    protected final CustomConnectionPool connectionPool;

    protected BaseDao(CustomConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    /**
     * Acquires a database connection pool
     * @return
     */
    protected Connection getConnection() {
        try {
            return connectionPool.getConnection();
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to fetch connection from custom pool", e);
        }
    }

    /**
     * Safely return a used connection to the connection pool
     * @param connection
     */
    protected void releaseConnection(Connection connection) {
        try {
            connectionPool.releaseConnection(connection);
        } catch (Exception e) {
            throw new DatabaseAccessException("Failed to return connection to safe pool", e);
        }
    }
}