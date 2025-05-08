package view;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class HomePage extends JFrame {

    private int loggedInUserId;

    public HomePage(int userId) {
        this.loggedInUserId = userId;
        setTitle("Home Page - Joke Feed");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        JButton profileBtn = new JButton("👤 Profile");
        JButton logoutBtn = new JButton("🚪 Logout");
        JButton settingsBtn = new JButton("⚙️ Settings");

        logoutBtn.addActionListener(e -> {
            dispose(); // close current window
            new LoginPage(); // return to login
        });
        profileBtn.addActionListener(e -> {
            dispose();
            new ProfilePage(loggedInUserId);
        });

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topBar.add(profileBtn);
        topBar.add(settingsBtn);
        topBar.add(logoutBtn);
        add(topBar, BorderLayout.NORTH);


        JPanel mainPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        add(scrollPane);

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/ChatServerDB", "root", "root")) {

            String sql = "SELECT p.post_id, p.content, " +
                    "(SELECT COUNT(*) FROM PostReactions WHERE post_id = p.post_id AND reaction_type = 'like') AS likes, " +
                    "(SELECT COUNT(*) FROM PostReactions WHERE post_id = p.post_id AND reaction_type = 'dislike') AS dislikes " +
                    "FROM Posts p ORDER BY p.created_at DESC";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int postId = rs.getInt("post_id");
                String content = rs.getString("content");
                int likes = rs.getInt("likes");
                int dislikes = rs.getInt("dislikes");

                JPanel postPanel = new JPanel(new BorderLayout());
                JTextArea contentArea = new JTextArea(content);
                contentArea.setLineWrap(true);
                contentArea.setWrapStyleWord(true);
                contentArea.setEditable(false);
                contentArea.setBackground(new Color(245, 245, 245));
                contentArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
                postPanel.add(contentArea, BorderLayout.CENTER);

                JPanel reactionPanel = new JPanel();
                JLabel likeLabel = new JLabel("👍 " + likes);
                JLabel dislikeLabel = new JLabel("👎 " + dislikes);
                JButton likeButton = new JButton("Like");
                JButton dislikeButton = new JButton("Dislike");

                likeButton.addActionListener(e -> reactToPost(postId, "like", likeLabel, dislikeLabel));
                dislikeButton.addActionListener(e -> reactToPost(postId, "dislike", likeLabel, dislikeLabel));

                reactionPanel.add(likeLabel);
                reactionPanel.add(dislikeLabel);
                reactionPanel.add(likeButton);
                reactionPanel.add(dislikeButton);

                postPanel.add(reactionPanel, BorderLayout.SOUTH);
                mainPanel.add(postPanel);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load posts.");
        }

        setVisible(true);
    }

    private void reactToPost(int postId, String reactionType, JLabel likeLabel, JLabel dislikeLabel) {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/ChatServerDB", "root", "root")) {

            String check = "SELECT * FROM PostReactions WHERE user_id = ? AND post_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(check);
            checkStmt.setInt(1, loggedInUserId);
            checkStmt.setInt(2, postId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "You already reacted to this post.");
                return;
            }

            String insert = "INSERT INTO PostReactions (user_id, post_id, reaction_type) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insert);
            stmt.setInt(1, loggedInUserId);
            stmt.setInt(2, postId);
            stmt.setString(3, reactionType);
            stmt.executeUpdate();

            // Refresh labels
            String countSql = "SELECT " +
                    "SUM(reaction_type = 'like') AS likes, " +
                    "SUM(reaction_type = 'dislike') AS dislikes " +
                    "FROM PostReactions WHERE post_id = ?";
            PreparedStatement countStmt = conn.prepareStatement(countSql);
            countStmt.setInt(1, postId);
            ResultSet counts = countStmt.executeQuery();
            if (counts.next()) {
                likeLabel.setText("👍 " + counts.getInt("likes"));
                dislikeLabel.setText("👎 " + counts.getInt("dislikes"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Replace '1' with the actual logged-in user's ID
        SwingUtilities.invokeLater(() -> new HomePage(1));
    }
}