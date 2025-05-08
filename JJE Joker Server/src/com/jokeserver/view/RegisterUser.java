package com.jokeserver.view;

import com.jokeserver.view.LoginPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterUser extends JFrame {

    private JTextField usernameField, emailField;
    private JPasswordField passwordField;
    private JButton registerButton, loginLink;
    private JLabel statusLabel;

    public RegisterUser() {
        setTitle("Register User");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        add(usernameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(15);
        add(emailField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        add(passwordField, gbc);

        // Register button
        gbc.gridy = 3;
        registerButton = new JButton("Register");
        add(registerButton, gbc);

        // Login Link
        gbc.gridy = 4;
        loginLink = new JButton("Already have an account? Login");
        loginLink.setBorderPainted(false);
        loginLink.setContentAreaFilled(false);
        loginLink.setForeground(Color.BLUE);
        add(loginLink, gbc);

        // Status
        gbc.gridy = 5;
        statusLabel = new JLabel("");
        add(statusLabel, gbc);

        // Event listeners
        registerButton.addActionListener(e -> register());
        loginLink.addActionListener(e -> {
            dispose(); // Close this window
            new LoginPage(); // Open login
        });

        setVisible(true);
    }

    private void register() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/ChatServerDB", "root", "root"
            );

            String sql = "INSERT INTO Users (username, email, password_hash) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password); // hash in production

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                statusLabel.setText("User registered successfully!");
            }

            conn.close();
        } catch (SQLIntegrityConstraintViolationException ex) {
            statusLabel.setText("Username already exists.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            statusLabel.setText("Error saving user.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegisterUser::new);
    }
}