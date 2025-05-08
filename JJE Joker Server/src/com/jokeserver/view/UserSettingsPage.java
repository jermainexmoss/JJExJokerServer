package com.jokeserver.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UserSettingsPage extends JFrame {
    private int userId;
    private JTextField nameField, emailField;
    private JPasswordField oldPasswordField, newPasswordField;

    public UserSettingsPage(int userId) {
        this.userId = userId;
        setTitle("User Settings");
        setSize(400, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        nameField = new JTextField();
        emailField = new JTextField();
        oldPasswordField = new JPasswordField();
        newPasswordField = new JPasswordField();

        setPlaceholder(nameField, "Change your name");
        setPlaceholder(emailField, "Email address");

        JButton saveBtn = new JButton("Save Changes");
        JButton profileBtn = new JButton("Back to Profile");
        JButton changePasswordBtn = new JButton("Change Password");

        saveBtn.addActionListener(e -> saveChanges());
        profileBtn.addActionListener(e -> {
            dispose();
            new ProfilePage(userId);
        });

        changePasswordBtn.addActionListener(e -> changePassword());

        JLabel titleLabel = new JLabel("✏️ Update Info");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel formPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        formPanel.add(nameField);
        formPanel.add(emailField);
        formPanel.add(new JLabel("Old Password"));
        formPanel.add(oldPasswordField);
        formPanel.add(new JLabel("New Password"));
        formPanel.add(newPasswordField);
        formPanel.add(changePasswordBtn);
        formPanel.add(saveBtn);
        formPanel.add(profileBtn);
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        container.add(titleLabel);
        container.add(Box.createVerticalStrut(20));
        container.add(formPanel);

        add(container);

        // Important fix: Load current user data
        loadCurrentData();

        setVisible(true);
    }

    private void setPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
    }

    private void loadCurrentData() {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/ChatServerDB", "root", "root")) {

            String sql = "SELECT name, email FROM Users WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                nameField.setForeground(Color.BLACK);
                emailField.setText(rs.getString("email"));
                emailField.setForeground(Color.BLACK);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void saveChanges() {
        String newName = nameField.getText();
        String newEmail = emailField.getText();

        if (newName.equals("Change your name") || newEmail.equals("Email address")) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields before saving.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/ChatServerDB", "root", "root")) {

            String sql = "UPDATE Users SET name = ?, email = ? WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newName);
            stmt.setString(2, newEmail);
            stmt.setInt(3, userId);
            int updated = stmt.executeUpdate();

            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Information updated successfully.");
                dispose();
                new ProfilePage(userId);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update information.");
        }
    }

    private void changePassword() {
        String oldPass = new String(oldPasswordField.getPassword());
        String newPass = new String(newPasswordField.getPassword());

        if (oldPass.isEmpty() || newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Both password fields must be filled.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/ChatServerDB", "root", "root")) {

            String checkSql = "SELECT password FROM Users WHERE user_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, userId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getString("password").equals(oldPass)) {
                String updateSql = "UPDATE Users SET password = ? WHERE user_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, newPass);
                updateStmt.setInt(2, userId);
                updateStmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Password updated.");
            } else {
                JOptionPane.showMessageDialog(this, "Old password incorrect.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Password update failed.");
        }
    }
}
