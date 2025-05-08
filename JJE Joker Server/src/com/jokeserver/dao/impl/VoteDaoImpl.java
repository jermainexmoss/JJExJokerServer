package com.jokeserver.dao.impl;

import com.jokeserver.dao.VoteDao;
import com.jokeserver.model.Vote;
import com.jokeserver.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VoteDaoImpl implements VoteDao {
    private static final Logger LOGGER = Logger.getLogger(VoteDaoImpl.class.getName());

    @Override
    public int create(Vote vote) {
        String sql = "INSERT INTO votes (joke_id, user_id, vote_value, vote_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, vote.getJokeId());
            stmt.setInt(2, vote.getUserId());
            stmt.setInt(3, vote.getVoteValue());
            stmt.setTimestamp(4, Timestamp.valueOf(vote.getVoteDate() != null ? vote.getVoteDate() : LocalDateTime.now()));
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                LOGGER.log(Level.WARNING, "Creating vote failed, no rows affected");
                return -1;
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating vote", e);
        }
        return -1;
    }

    @Override
    public Optional<Vote> findById(int voteId) {
        String sql = "SELECT * FROM votes WHERE vote_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, voteId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToVote(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding vote by ID: " + voteId, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Vote> findByJokeAndUser(int jokeId, int userId) {
        String sql = "SELECT * FROM votes WHERE joke_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jokeId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToVote(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding vote by joke ID: " + jokeId + " and user ID: " + userId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Vote> findByJokeId(int jokeId) {
        String sql = "SELECT * FROM votes WHERE joke_id = ? ORDER BY vote_date DESC";
        List<Vote> votes = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jokeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    votes.add(mapResultSetToVote(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding votes by joke ID: " + jokeId, e);
        }
        return votes;
    }

    @Override
    public List<Vote> findByUserId(int userId) {
        String sql = "SELECT * FROM votes WHERE user_id = ? ORDER BY vote_date DESC";
        List<Vote> votes = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    votes.add(mapResultSetToVote(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding votes by user ID: " + userId, e);
        }
        return votes;
    }

    @Override
    public boolean update(Vote vote) {
        String sql = "UPDATE votes SET vote_value = ?, vote_date = ? WHERE vote_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vote.getVoteValue());
            stmt.setTimestamp(2, Timestamp.valueOf(vote.getVoteDate()));
            stmt.setInt(3, vote.getVoteId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating vote ID: " + vote.getVoteId(), e);
            return false;
        }
    }

    @Override
    public boolean delete(int voteId) {
        String sql = "DELETE FROM votes WHERE vote_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, voteId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting vote ID: " + voteId, e);
            return false;
        }
    }

    @Override
    public boolean deleteByJokeAndUser(int jokeId, int userId) {
        String sql = "DELETE FROM votes WHERE joke_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jokeId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting vote for joke ID: " + jokeId + " and user ID: " + userId, e);
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

    private Vote mapResultSetToVote(ResultSet rs) throws SQLException {
        Vote vote = new Vote();
        vote.setVoteId(rs.getInt("vote_id"));
        vote.setJokeId(rs.getInt("joke_id"));
        vote.setUserId(rs.getInt("user_id"));
        vote.setVoteValue(rs.getInt("vote_value"));
        vote.setVoteDate(rs.getTimestamp("vote_date").toLocalDateTime());
        return vote;
    }
}