package com.jokeserver.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class TransactionManager implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(TransactionManager.class.getName());
    private final Connection connection;

    public TransactionManager() throws SQLException {
        connection = DatabaseConnection.getConnection();
        connection.setAutoCommit(false); // Disable auto-commit for transaction control
    }

    public Connection getConnection() {
        return connection;
    }

    public void commit() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.commit();
            LOGGER.info("Transaction committed");
        }
    }

    public void rollback() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.rollback();
                LOGGER.info("Transaction rolled back");
            }
        } catch (SQLException e) {
            LOGGER.severe("Error rolling back transaction: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        DatabaseConnection.closeConnection(connection);
    }
}