package com.jokeserver.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/joke_server"; // Database name: joke_server
    private static final String USER = "root";
    private static final String PASSWORD = "root";


    private DatabaseConnection() {

    }

    /**
     * Establishes a connection to the MySQL database.
     * @return A Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        try {
             Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Please ensure the MySQL Connector/J is in the classpath.", e);
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Closes the database connection.
     * @param conn The Connection object to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
