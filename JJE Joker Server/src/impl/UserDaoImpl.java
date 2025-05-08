package impl;

import dao.UserDao;
import model.User;
import com.jokeserver.util.DatabaseConnection;
import com.jokeserver.util.TransactionManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDaoImpl implements UserDao {
    private static final Logger LOGGER = Logger.getLogger(UserDaoImpl.class.getName());

    @Override
    public int create(User user) {
        String sql = "INSERT INTO users (username, password_hash, email, account_type, registration_date, last_login, is_active, preferences) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getAccountType());
            stmt.setTimestamp(5, Timestamp.valueOf(user.getRegistrationDate() != null ? user.getRegistrationDate() : LocalDateTime.now()));
            if (user.getLastLogin() != null) {
                stmt.setTimestamp(6, Timestamp.valueOf(user.getLastLogin()));
            } else {
                stmt.setNull(6, Types.TIMESTAMP);
            }
            stmt.setBoolean(7, user.isActive());
            stmt.setString(8, user.getPreferences() != null ? user.getPreferences() : "{}");
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                LOGGER.log(Level.WARNING, "Creating user failed, no rows affected");
                return -1;
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating user", e);
        }
        return -1;
    }

    @Override
    public Optional<User> findById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by ID: " + userId, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by username: " + username, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all users", e);
        }
        return users;
    }

    @Override
    public List<User> findByAccountType(String accountType) {
        return null;
    }

    @Override
    public boolean update(User user) {
        String sql = "UPDATE users SET username = ?, password_hash = ?, email = ?, account_type = ?, registration_date = ?, last_login = ?, is_active = ?, preferences = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getAccountType());
            stmt.setTimestamp(5, Timestamp.valueOf(user.getRegistrationDate()));
            if (user.getLastLogin() != null) {
                stmt.setTimestamp(6, Timestamp.valueOf(user.getLastLogin()));
            } else {
                stmt.setNull(6, Types.TIMESTAMP);
            }
            stmt.setBoolean(7, user.isActive());
            stmt.setString(8, user.getPreferences());
            stmt.setInt(9, user.getUserId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating user ID: " + user.getUserId(), e);
            return false;
        }
    }

    @Override
    public boolean updatePassword(int userId, String newPassword) {
        return false;
    }

    @Override
    public boolean updateLastLogin(int userId) {
        return false;
    }

    @Override
    public boolean updateAccountType(int userId, String newAccountType) {
        return false;
    }

    @Override
    public boolean delete(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting user ID: " + userId, e);
            return false;
        }
    }

    @Override
    public boolean deactivate(int userId) {
        return false;
    }

    @Override
    public boolean activate(int userId) {
        return false;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setEmail(rs.getString("email"));
        user.setAccountType(rs.getString("account_type"));
        user.setRegistrationDate(rs.getTimestamp("registration_date").toLocalDateTime());
        if (rs.getObject("last_login") != null) {
            user.setLastLogin(rs.getTimestamp("last_login").toLocalDateTime());
        }
        user.setActive(rs.getBoolean("is_active"));
        user.setPreferences(rs.getString("preferences"));
        return user;
    }
}