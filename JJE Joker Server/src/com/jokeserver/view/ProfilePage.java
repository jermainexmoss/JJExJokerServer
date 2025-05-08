package com.jokeserver.view;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ProfilePage extends JFrame {
    private int userId;
    private JLabel nameLabel, emailLabel;

    public ProfilePage(int userId) {
        this.userId = userId;
        setTitle("User Profile");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        nameLabel = new JLabel();
        emailLabel = new JLabel();

        JButton homeBtn = new JButton("🏠 Home");
        JButton settingsBtn = new JButton("⚙️ Settings");

        homeBtn.addActionListener(e -> {
            dispose();
            new HomePage(userId);
        });

        settingsBtn.addActionListener(e -> {
            dispose();
            new UserSettingsPage(userId);
        });

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.add(new JLabel("👤 Profile"));
        panel.add(nameLabel);
        panel.add(emailLabel);
        panel.add(homeBtn);
        panel.add(settingsBtn);

        add(panel);
        loadUserInfo();
        setVisible(true);
    }

    private void loadUserInfo() {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/ChatServerDB", "root", "yourpassword")) {

            String sql = "SELECT name, email FROM Users WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nameLabel.setText("Name: " + rs.getString("name"));
                emailLabel.setText("Email: " + rs.getString("email"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}