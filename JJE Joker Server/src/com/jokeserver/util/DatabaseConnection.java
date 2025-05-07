package com.jokeserver.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/joke_server";
    private static final String USERNAME = "root"; // Change as needed
    private static final String PASSWORD = "password"; // Change as needed

    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                LOGGER.log(Level.INFO, "Database connection established");
            } catch (ClassNotFoundException | SQLException e) {
                LOGGER.log(Level.SEVERE, "Error connecting to database", e);
                throw new RuntimeException("Database connection failed", e);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                LOGGER.log(Level.INFO, "Database connection closed");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing database connection", e);
            }
        }
    }
}
