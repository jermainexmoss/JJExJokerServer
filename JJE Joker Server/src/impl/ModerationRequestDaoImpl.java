package impl;

import dao.ModerationRequestDao;
import model.ModerationRequest;
import com.jokeserver.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModerationRequestDaoImpl implements ModerationRequestDao {
    private static final Logger LOGGER = Logger.getLogger(ModerationRequestDaoImpl.class.getName());

    @Override
    public int create(ModerationRequest request) {
        String sql = "INSERT INTO moderation_requests (joke_id, user_id, request_type, request_details, request_date, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, request.getJokeId());
            pstmt.setInt(2, request.getUserId());
            pstmt.setString(3, request.getRequestType());
            pstmt.setString(4, request.getRequestDetails());
            pstmt.setTimestamp(5, Timestamp.valueOf(request.getRequestDate()));
            pstmt.setString(6, request.getStatus());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating moderation request", e);
        }
        return -1;
    }

    @Override
    public Optional<ModerationRequest> findById(int requestId) {
        return Optional.empty();
    }

    @Override
    public List<ModerationRequest> findByJokeId(int jokeId) {
        List<ModerationRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM moderation_requests WHERE joke_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, jokeId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ModerationRequest request = new ModerationRequest();
                request.setRequestId(rs.getInt("request_id"));
                request.setJokeId(rs.getInt("joke_id"));
                request.setUserId(rs.getInt("user_id"));
                request.setRequestType(rs.getString("request_type"));
                request.setRequestDetails(rs.getString("request_details"));
                request.setRequestDate(rs.getTimestamp("request_date").toLocalDateTime());
                request.setStatus(rs.getString("status"));
                request.setResolutionDate(rs.getTimestamp("resolution_date") != null ? rs.getTimestamp("resolution_date").toLocalDateTime() : null);
                request.setResolutionDetails(rs.getString("resolution_details"));
                requests.add(request);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding moderation requests by jokeId", e);
        }
        return requests;
    }

    @Override
    public List<ModerationRequest> findByUserId(int userId) {
        List<ModerationRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM moderation_requests WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ModerationRequest request = new ModerationRequest();
                request.setRequestId(rs.getInt("request_id"));
                request.setJokeId(rs.getInt("joke_id"));
                request.setUserId(rs.getInt("user_id"));
                request.setRequestType(rs.getString("request_type"));
                request.setRequestDetails(rs.getString("request_details"));
                request.setRequestDate(rs.getTimestamp("request_date").toLocalDateTime());
                request.setStatus(rs.getString("status"));
                request.setResolutionDate(rs.getTimestamp("resolution_date") != null ? rs.getTimestamp("resolution_date").toLocalDateTime() : null);
                request.setResolutionDetails(rs.getString("resolution_details"));
                requests.add(request);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding moderation requests by userId", e);
        }
        return requests;
    }

    @Override
    public List<ModerationRequest> findByStatus(String status) {
        return null;
    }

    @Override
    public boolean update(ModerationRequest request) {
        String sql = "UPDATE moderation_requests SET status = ?, resolution_date = ?, resolution_details = ? WHERE request_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, request.getStatus());
            pstmt.setTimestamp(2, request.getResolutionDate() != null ? Timestamp.valueOf(request.getResolutionDate()) : null);
            pstmt.setString(3, request.getResolutionDetails());
            pstmt.setInt(4, request.getRequestId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating moderation request", e);
            return false;
        }
    }

    @Override
    public boolean updateStatus(int requestId, String status, int processedBy) {
        return false;
    }

    @Override
    public boolean delete(int requestId) {
        String sql = "DELETE FROM moderation_requests WHERE request_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, requestId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting moderation request", e);
            return false;
        }
    }
}
