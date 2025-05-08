package impl;

import dao.UserActionDao;
import model.UserAction;
import com.jokeserver.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserActionDaoImpl implements UserActionDao {
    private static final Logger LOGGER = Logger.getLogger(UserActionDaoImpl.class.getName());

    @Override
    public int create(UserAction action) {
        String sql = "INSERT INTO user_actions (user_id, action_type, action_details, action_timestamp) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, action.getUserId());
            stmt.setString(2, action.getActionType());
            stmt.setString(3, action.getActionDetails());
            stmt.setTimestamp(4, Timestamp.valueOf(action.getActionTimestamp() != null ? action.getActionTimestamp() : LocalDateTime.now()));
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                LOGGER.log(Level.WARNING, "Creating user action failed, no rows affected");
                return -1;
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating user action", e);
        }
        return -1;
    }

    @Override
    public List<UserAction> findByUserId(int userId) {
        String sql = "SELECT * FROM user_actions WHERE user_id = ? ORDER BY action_timestamp DESC";
        List<UserAction> actions = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    actions.add(mapResultSetToUserAction(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user actions by user ID: " + userId, e);
        }
        return actions;
    }

    @Override
    public List<UserAction> findByActionType(String actionType) {
        String sql = "SELECT * FROM user_actions WHERE action_type = ? ORDER BY action_timestamp DESC";
        List<UserAction> actions = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, actionType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    actions.add(mapResultSetToUserAction(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user actions by action type: " + actionType, e);
        }
        return actions;
    }

    @Override
    public List<UserAction> findAll() {
        String sql = "SELECT * FROM user_actions ORDER BY action_timestamp DESC";
        List<UserAction> actions = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                actions.add(mapResultSetToUserAction(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all user actions", e);
        }
        return actions;
    }

    private UserAction mapResultSetToUserAction(ResultSet rs) throws SQLException {
        UserAction action = new UserAction();
        action.setActionId(rs.getInt("action_id"));
        action.setUserId(rs.getInt("user_id"));
        action.setActionType(rs.getString("action_type"));
        action.setActionDetails(rs.getString("action_details"));
        action.setActionTimestamp(rs.getTimestamp("action_timestamp").toLocalDateTime());
        return action;
    }
}