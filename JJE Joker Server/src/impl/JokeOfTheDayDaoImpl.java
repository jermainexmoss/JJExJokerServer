package impl;

import dao.JokeOfTheDayDao;
import model.JokeOfTheDay;
import com.jokeserver.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JokeOfTheDayDaoImpl implements JokeOfTheDayDao {
    private static final Logger LOGGER = Logger.getLogger(JokeOfTheDayDaoImpl.class.getName());

    @Override
    public int create(JokeOfTheDay jotd) {
        String sql = "INSERT INTO joke_of_the_day (joke_id, date) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, jotd.getJokeId());
            stmt.setDate(2, Date.valueOf(jotd.getDate()));
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                LOGGER.log(Level.WARNING, "Creating joke of the day failed, no rows affected");
                return -1;
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating joke of the day", e);
        }
        return -1;
    }

    @Override
    public Optional<JokeOfTheDay> findById(int jotdId) {
        String sql = "SELECT * FROM joke_of_the_day WHERE jotd_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jotdId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToJokeOfTheDay(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding joke of the day by ID: " + jotdId, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<JokeOfTheDay> findByDate(LocalDate date) {
        String sql = "SELECT * FROM joke_of_the_day WHERE date = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToJokeOfTheDay(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding joke of the day by date: " + date, e);
        }
        return Optional.empty();
    }

    @Override
    public List<JokeOfTheDay> findAll() {
        String sql = "SELECT * FROM joke_of_the_day ORDER BY date DESC";
        List<JokeOfTheDay> jotds = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                jotds.add(mapResultSetToJokeOfTheDay(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all jokes of the day", e);
        }
        return jotds;
    }

    @Override
    public boolean update(JokeOfTheDay jotd) {
        String sql = "UPDATE joke_of_the_day SET joke_id = ?, date = ? WHERE jotd_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jotd.getJokeId());
            stmt.setDate(2, Date.valueOf(jotd.getDate()));
            stmt.setInt(3, jotd.getJotdId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating joke of the day ID: " + jotd.getJotdId(), e);
            return false;
        }
    }

    @Override
    public boolean delete(int jotdId) {
        String sql = "DELETE FROM joke_of_the_day WHERE jotd_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jotdId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting joke of the day ID: " + jotdId, e);
            return false;
        }
    }

    @Override
    public boolean selectJokeOfTheDay() {
        String sql = "INSERT INTO joke_of_the_day (joke_id, date) " +
                "SELECT joke_id, CURDATE() FROM jokes " +
                "WHERE approval_status = 'approved' AND joke_id NOT IN (SELECT joke_id FROM joke_of_the_day) " +
                "ORDER BY RAND() LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error selecting joke of the day", e);
            return false;
        }
    }

    private JokeOfTheDay mapResultSetToJokeOfTheDay(ResultSet rs) throws SQLException {
        JokeOfTheDay jotd = new JokeOfTheDay();
        jotd.setJotdId(rs.getInt("jotd_id"));
        jotd.setJokeId(rs.getInt("joke_id"));
        jotd.setDate(rs.getDate("date").toLocalDate());
        return jotd;
    }
}