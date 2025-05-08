package view;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginPage extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerLink;
    private JLabel statusLabel;

    public LoginPage() {
        setTitle("Login Page");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
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

        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        add(passwordField, gbc);

        // Login Button
        gbc.gridx = 1; gbc.gridy = 2;
        loginButton = new JButton("Login");
        add(loginButton, gbc);

        // Register Link
        gbc.gridy = 3;
        registerLink = new JButton("Don't have an account? Register");
        registerLink.setBorderPainted(false);
        registerLink.setContentAreaFilled(false);
        registerLink.setForeground(Color.BLUE);
        add(registerLink, gbc);

        // Status
        gbc.gridy = 4;
        statusLabel = new JLabel("");
        add(statusLabel, gbc);

        // Action listeners
        loginButton.addActionListener(e -> authenticate());
        registerLink.addActionListener(e -> {
            dispose(); // Close login window
            new RegisterUser(); // Open registration
        });

        setVisible(true);
    }

    private void authenticate() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/ChatServerDB", "root", "root"
            );

            String sql = "SELECT * FROM Users WHERE username = ? AND password_hash = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password); // hash in production
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                statusLabel.setText("Login successful!");
                // Proceed to chat GUI
            } else {
                statusLabel.setText("Invalid credentials.");
            }

            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            statusLabel.setText("Database error.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginPage::new);
    }
}