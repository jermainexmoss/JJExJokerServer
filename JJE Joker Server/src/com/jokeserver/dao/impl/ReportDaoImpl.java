package com.jokeserver.dao.impl;

import com.jokeserver.dao.ReportDao;
import com.jokeserver.model.Report;
import com.jokeserver.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReportDaoImpl implements ReportDao {
    private static final Logger LOGGER = Logger.getLogger(ReportDaoImpl.class.getName());

    @Override
    public int create(Report report) {
        String sql = "INSERT INTO reports (user_id, joke_id, reason, report_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, report.getUserId());
            stmt.setInt(2, report.getJokeId());
            stmt.setString(3, report.getReason());
            stmt.setTimestamp(4, Timestamp.valueOf(report.getReportDate() != null ? report.getReportDate() : LocalDateTime.now()));
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                LOGGER.log(Level.WARNING, "Creating report failed, no rows affected");
                return -1;
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating report", e);
        }
        return -1;
    }

    @Override
    public Optional<Report> findById(int reportId) {
        String sql = "SELECT * FROM reports WHERE report_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reportId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToReport(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding report by ID: " + reportId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Report> findByJokeId(int jokeId) {
        String sql = "SELECT * FROM reports WHERE joke_id = ? ORDER BY report_date DESC";
        List<Report> reports = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jokeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapResultSetToReport(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding reports by joke ID: " + jokeId, e);
        }
        return reports;
    }

    @Override
    public List<Report> findByUserId(int userId) {
        String sql = "SELECT * FROM reports WHERE user_id = ? ORDER BY report_date DESC";
        List<Report> reports = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapResultSetToReport(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding reports by user ID: " + userId, e);
        }
        return reports;
    }

    @Override
    public boolean update(Report report) {
        String sql = "UPDATE reports SET reason = ?, report_date = ? WHERE report_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, report.getReason());
            stmt.setTimestamp(2, Timestamp.valueOf(report.getReportDate()));
            stmt.setInt(3, report.getReportId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating report ID: " + report.getReportId(), e);
            return false;
        }
    }

    @Override
    public boolean delete(int reportId) {
        String sql = "DELETE FROM reports WHERE report_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reportId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting report ID: " + reportId, e);
            return false;
        }
    }

    private Report mapResultSetToReport(ResultSet rs) throws SQLException {
        Report report = new Report();
        report.setReportId(rs.getInt("report_id"));
        report.setUserId(rs.getInt("user_id"));
        report.setJokeId(rs.getInt("joke_id"));
        report.setReason(rs.getString("reason"));
        report.setReportDate(rs.getTimestamp("report_date").toLocalDateTime());
        return report;
    }
}