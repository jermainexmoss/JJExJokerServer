package impl;

import dao.ModerationRequestDao;
import model.ModerationRequest;
import com.jokeserver.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModerationRequestDaoImpl implements ModerationRequestDao {
    private static final Logger LOGGER = Logger.getLogger(ModerationRequestDaoImpl.class.getName());

    @Override
    public int create(ModerationRequest request) {
        String sql = "INSERT INTO moderation_requests (user_id, request_date, request_status) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, request.getUserId());
            stmt.setTimestamp(2, Timestamp.valueOf(request.getRequestDate() != null ? request.getRequestDate() : LocalDateTime.now()));
            stmt.setString(3, request.getRequestStatus());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                LOGGER.log(Level.WARNING, "Creating moderation request failed, no rows affected");
                return -1;
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating moderation request", e);
        }
        return -1;
    }

    @Override
    public Optional<ModerationRequest> findById(int requestId) {
        String sql = "SELECT * FROM moderation_requests WHERE request_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, requestId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToModerationRequest(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding moderation request by ID: " + requestId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<ModerationRequest> findByUserId(int userId) {
        String sql = "SELECT * FROM moderation_requests WHERE user_id = ? ORDER BY request_date DESC";
        List<ModerationRequest> requests = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToModerationRequest(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding moderation requests by user ID: " + userId, e);
        }
        return requests;
    }

    @Override
    public List<ModerationRequest> findByStatus(String status) {
        String sql = "SELECT * FROM moderation_requests WHERE request_status = ? ORDER BY request_date DESC";
        List<ModerationRequest> requests = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToModerationRequest(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding moderation requests by status: " + status, e);
        }
        return requests;
    }

    @Override
    public boolean update(ModerationRequest request) {
        String sql = "UPDATE moderation_requests SET request_status = ?, processed_by = ?, processing_date = ? WHERE request_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, request.getRequestStatus());
            if (request.getProcessedBy() != null) {
                stmt.setInt(2, request.getProcessedBy());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            if (request.getProcessingDate() != null) {
                stmt.setTimestamp(3, Timestamp.valueOf(request.getProcessingDate()));
            } else {
                stmt.setNull(3, Types.TIMESTAMP);
            }
            stmt.setInt(4, request.getRequestId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating moderation request ID: " + request.getRequestId(), e);
            return false;
        }
    }

    @Override
    public boolean updateStatus(int requestId, String status, int processedBy) {
        String sql = "UPDATE moderation_requests SET request_status = ?, processed_by = ?, processing_date = ? WHERE request_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, processedBy);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(4, requestId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating status for moderation request ID: " + requestId, e);
            return false;
        }
    }

    @Override
    public boolean delete(int requestId) {
        String sql = "DELETE FROM moderation_requests WHERE request_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, requestId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting moderation request ID: " + requestId, e);
            return false;
        }
    }

    private ModerationRequest mapResultSetToModerationRequest(ResultSet rs) throws SQLException {
        ModerationRequest request = new ModerationRequest();
        request.setRequestId(rs.getInt("request_id"));
        request.setUserId(rs.getInt("user_id"));
        request.setRequestDate(rs.getTimestamp("request_date").toLocalDateTime());
        request.setRequestStatus(rs.getString("request_status"));
        if (rs.getObject("processed_by") != null) {
            request.setProcessedBy(rs.getInt("processed_by"));
        }
        if (rs.getObject("processing_date") != null) {
            request.setProcessingDate(rs.getTimestamp("processing_date").toLocalDateTime());
        }
        return request;
    }
}

