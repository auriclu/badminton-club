package com.club.badminton.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Custom thread-safe connection pool
 */
public class CustomConnectionPool {

    private final BlockingQueue<Connection> connectionPool;


    /**
     * Initialize pool, populate with active connections
     * @param url
     * @param username
     * @param password
     * @param driver
     * @param poolSize
     * @throws Exception
     */
    public CustomConnectionPool(
            String url,
            String username,
            String password,
            String driver,
            int poolSize) throws Exception {

        Class.forName(driver);

        connectionPool = new LinkedBlockingQueue<>(poolSize);

        for (int i = 0; i < poolSize; i++) {
            connectionPool.add(
                    DriverManager.getConnection(
                            url,
                            username,
                            password
                    )
            );
        }
    }

    /**
     * Borrow connection from the pool
     * If empty, the thread will wait until a connection becomes available
     * @return
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        try {
            return connectionPool.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Unable to get connection", e);
        }
    }

    /**
     * Return connection back for future reuse
     * @param connection
     */
    public void releaseConnection(Connection connection) {
        if (connection != null) {
            connectionPool.offer(connection);
        }
    }


    /**
     * Close all open connections
     * @throws SQLException
     */
    public void shutdown() throws SQLException {
        for (Connection connection : connectionPool) {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        }
    }
}