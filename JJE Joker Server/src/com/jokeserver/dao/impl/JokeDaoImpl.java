package com.jokeserver.dao.impl;

import com.jokeserver.dao.JokeDao;
import com.jokeserver.model.Joke;
import com.jokeserver.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JokeDaoImpl implements JokeDao {
    private static final Logger LOGGER = Logger.getLogger(JokeDaoImpl.class.getName());

    @Override
    public int create(Joke joke) {
        String sql = "INSERT INTO jokes (user_id, joke_text, submission_date, approval_status, approved_by, approval_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, joke.getUserId());
            stmt.setString(2, joke.getJokeText());
            stmt.setTimestamp(3, Timestamp.valueOf(joke.getSubmissionDate() != null ? joke.getSubmissionDate() : LocalDateTime.now()));
            stmt.setString(4, joke.getApprovalStatus() != null ? joke.getApprovalStatus() : "pending");
            if (joke.getApprovedBy() != null) {
                stmt.setInt(5, joke.getApprovedBy());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            if (joke.getApprovalDate() != null) {
                stmt.setTimestamp(6, Timestamp.valueOf(joke.getApprovalDate()));
            } else {
                stmt.setNull(6, Types.TIMESTAMP);
            }
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                LOGGER.log(Level.WARNING, "Creating joke failed, no rows affected");
                return -1;
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating joke", e);
        }
        return -1;
    }

    @Override
    public Optional<Joke> findById(int jokeId) {
        String sql = "SELECT * FROM jokes WHERE joke_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jokeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Joke joke = mapResultSetToJoke(rs);
                    joke.setRating(getJokeRating(jokeId));
                    return Optional.of(joke);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding joke by ID: " + jokeId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Joke> findAll() {
        String sql = "SELECT * FROM jokes";
        List<Joke> jokes = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Joke joke = mapResultSetToJoke(rs);
                joke.setRating(getJokeRating(joke.getJokeId()));
                jokes.add(joke);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all jokes", e);
        }
        return jokes;
    }

    @Override
    public List<Joke> findByUserId(int userId) {
        String sql = "SELECT * FROM jokes WHERE user_id = ? ORDER BY submission_date DESC";
        List<Joke> jokes = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Joke joke = mapResultSetToJoke(rs);
                    joke.setRating(getJokeRating(joke.getJokeId()));
                    jokes.add(joke);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding jokes by user ID: " + userId, e);
        }
        return jokes;
    }

    @Override
    public List<Joke> findByApprovalStatus(String approvalStatus) {
        String sql = "SELECT * FROM jokes WHERE approval_status = ? ORDER BY submission_date DESC";
        List<Joke> jokes = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, approvalStatus);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Joke joke = mapResultSetToJoke(rs);
                    joke.setRating(getJokeRating(joke.getJokeId()));
                    jokes.add(joke);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding jokes by approval status: " + approvalStatus, e);
        }
        return jokes;
    }

    @Override
    public List<Joke> findTopRatedJokes(int limit) {
        String sql = "SELECT j.*, COALESCE(SUM(v.vote_value), 0) as rating " +
                "FROM jokes j LEFT JOIN votes v ON j.joke_id = v.joke_id " +
                "GROUP BY j.joke_id ORDER BY rating DESC LIMIT ?";
        List<Joke> jokes = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Joke joke = mapResultSetToJoke(rs);
                    joke.setRating(rs.getInt("rating"));
                    jokes.add(joke);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding top-rated jokes", e);
        }
        return jokes;
    }

    @Override
    public List<Joke> findRecentJokes(int limit) {
        String sql = "SELECT * FROM jokes WHERE approval_status = 'approved' ORDER BY submission_date DESC LIMIT ?";
        List<Joke> jokes = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Joke joke = mapResultSetToJoke(rs);
                    joke.setRating(getJokeRating(joke.getJokeId()));
                    jokes.add(joke);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding recent jokes", e);
        }
        return jokes;
    }

    @Override
    public boolean update(Joke joke) {
        String sql = "UPDATE jokes SET user_id = ?, joke_text = ?, submission_date = ?, approval_status = ?, approved_by = ?, approval_date = ? WHERE joke_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, joke.getUserId());
            stmt.setString(2, joke.getJokeText());
            stmt.setTimestamp(3, Timestamp.valueOf(joke.getSubmissionDate()));
            stmt.setString(4, joke.getApprovalStatus());
            if (joke.getApprovedBy() != null) {
                stmt.setInt(5, joke.getApprovedBy());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            if (joke.getApprovalDate() != null) {
                stmt.setTimestamp(6, Timestamp.valueOf(joke.getApprovalDate()));
            } else {
                stmt.setNull(6, Types.TIMESTAMP);
            }
            stmt.setInt(7, joke.getJokeId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating joke ID: " + joke.getJokeId(), e);
            return false;
        }
    }

    @Override
    public boolean updateApprovalStatus(int jokeId, String approvalStatus, int approvedBy) {
        String sql = "UPDATE jokes SET approval_status = ?, approved_by = ?, approval_date = ? WHERE joke_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, approvalStatus);
            stmt.setInt(2, approvedBy);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(4, jokeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating approval status for joke ID: " + jokeId, e);
            return false;
        }
    }

    @Override
    public boolean delete(int jokeId) {
        String sql = "DELETE FROM jokes WHERE joke_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jokeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting joke ID: " + jokeId, e);
            return false;
        }
    }

    @Override
    public int getJokeRating(int jokeId) {
        String sql = "SELECT COALESCE(SUM(vote_value), 0) as rating FROM votes WHERE joke_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jokeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("rating");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting rating for joke ID: " + jokeId, e);
        }
        return 0;
    }

    private Joke mapResultSetToJoke(ResultSet rs) throws SQLException {
        Joke joke = new Joke();
        joke.setJokeId(rs.getInt("joke_id"));
        joke.setUserId(rs.getInt("user_id"));
        joke.setJokeText(rs.getString("joke_text"));
        joke.setSubmissionDate(rs.getTimestamp("submission_date").toLocalDateTime());
        joke.setApprovalStatus(rs.getString("approval_status"));
        if (rs.getObject("approved_by") != null) {
            joke.setApprovedBy(rs.getInt("approved_by"));
        }
        if (rs.getObject("approval_date") != null) {
            joke.setApprovalDate(rs.getTimestamp("approval_date").toLocalDateTime());
        }
        return joke;
    }
}

