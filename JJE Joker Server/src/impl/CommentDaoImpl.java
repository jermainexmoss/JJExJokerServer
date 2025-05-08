package impl;

import dao.CommentDao;
import model.Comment;
import com.jokeserver.util.DatabaseConnection;
import dao.CommentDao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommentDaoImpl implements CommentDao {
    private static final Logger LOGGER = Logger.getLogger(CommentDaoImpl.class.getName());

    @Override
    public int create(Comment comment) {
        String sql = "INSERT INTO comments (joke_id, user_id, comment_text, comment_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, comment.getJokeId());
            pstmt.setInt(2, comment.getUserId());
            pstmt.setString(3, comment.getCommentText());
            pstmt.setTimestamp(4, Timestamp.valueOf(comment.getCommentDate()));
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                LOGGER.warning("Creating comment failed, no rows affected");
                return -1;
            }
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating comment", e);
        }
        return -1;
    }

    @Override
    public Optional<Comment> findById(int commentId) {
        return Optional.empty();
    }

    @Override
    public List<Comment> findByJokeId(int jokeId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE joke_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, jokeId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Comment comment = new Comment();
                comment.setCommentId(rs.getInt("comment_id"));
                comment.setJokeId(rs.getInt("joke_id"));
                comment.setUserId(rs.getInt("user_id"));
                comment.setCommentText(rs.getString("comment_text"));
                comment.setCommentDate(rs.getTimestamp("comment_date").toLocalDateTime());
                comments.add(comment);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding comments by jokeId", e);
        }
        return comments;
    }

    @Override
    public List<Comment> findByUserId(int userId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Comment comment = new Comment();
                comment.setCommentId(rs.getInt("comment_id"));
                comment.setJokeId(rs.getInt("joke_id"));
                comment.setUserId(rs.getInt("user_id"));
                comment.setCommentText(rs.getString("comment_text"));
                comment.setCommentDate(rs.getTimestamp("comment_date").toLocalDateTime());
                comments.add(comment);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding comments by userId", e);
        }
        return comments;
    }

    @Override
    public boolean update(Comment comment) {
        String sql = "UPDATE comments SET comment_text = ?, comment_date = ? WHERE comment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, comment.getCommentText());
            pstmt.setTimestamp(2, Timestamp.valueOf(comment.getCommentDate()));
            pstmt.setInt(3, comment.getCommentId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating comment", e);
            return false;
        }
    }

    @Override
    public boolean delete(int commentId) {
        String sql = "DELETE FROM comments WHERE comment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, commentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting comment", e);
            return false;
        }
    }
}