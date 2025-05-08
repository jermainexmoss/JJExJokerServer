package com.jokeserver.dao.impl;

import com.jokeserver.dao.CommentDao;
import com.jokeserver.model.Comment;
import com.jokeserver.util.DatabaseConnection;

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
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, comment.getJokeId());
            stmt.setInt(2, comment.getUserId());
            stmt.setString(3, comment.getCommentText());
            stmt.setTimestamp(4, Timestamp.valueOf(comment.getCommentDate() != null ? comment.getCommentDate() : LocalDateTime.now()));
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                LOGGER.log(Level.WARNING, "Creating comment failed, no rows affected");
                return -1;
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating comment", e);
        }
        return -1;
    }

    @Override
    public Optional<Comment> findById(int commentId) {
        String sql = "SELECT * FROM comments WHERE comment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, commentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToComment(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding comment by ID: " + commentId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Comment> findByJokeId(int jokeId) {
        String sql = "SELECT * FROM comments WHERE joke_id = ? ORDER BY comment_date DESC";
        List<Comment> comments = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jokeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(mapResultSetToComment(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding comments by joke ID: " + jokeId, e);
        }
        return comments;
    }

    @Override
    public List<Comment> findByUserId(int userId) {
        String sql = "SELECT * FROM comments WHERE user_id = ? ORDER BY comment_date DESC";
        List<Comment> comments = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(mapResultSetToComment(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding comments by user ID: " + userId, e);
        }
        return comments;
    }

    @Override
    public boolean update(Comment comment) {
        String sql = "UPDATE comments SET comment_text = ?, comment_date = ? WHERE comment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, comment.getCommentText());
            stmt.setTimestamp(2, Timestamp.valueOf(comment.getCommentDate()));
            stmt.setInt(3, comment.getCommentId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating comment ID: " + comment.getCommentId(), e);
            return false;
        }
    }

    @Override
    public boolean delete(int commentId) {
        String sql = "DELETE FROM comments WHERE comment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, commentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting comment ID: " + commentId, e);
            return false;
        }
    }

    private Comment mapResultSetToComment(ResultSet rs) throws SQLException {
        Comment comment = new Comment();
        comment.setCommentId(rs.getInt("comment_id"));
        comment.setJokeId(rs.getInt("joke_id"));
        comment.setUserId(rs.getInt("user_id"));
        comment.setCommentText(rs.getString("comment_text"));
        comment.setCommentDate(rs.getTimestamp("comment_date").toLocalDateTime());
        return comment;
    }
}




