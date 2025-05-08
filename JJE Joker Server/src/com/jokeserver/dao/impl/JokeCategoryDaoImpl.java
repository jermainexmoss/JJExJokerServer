package com.jokeserver.dao.impl;

import com.jokeserver.dao.JokeCategoryDao;
import com.jokeserver.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JokeCategoryDaoImpl implements JokeCategoryDao {
    private static final Logger LOGGER = Logger.getLogger(JokeCategoryDaoImpl.class.getName());

    @Override
    public boolean addJokeToCategory(int jokeId, int categoryId) {
        String sql = "INSERT INTO joke_categories (joke_id, category_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jokeId);
            stmt.setInt(2, categoryId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding joke ID: " + jokeId + " to category ID: " + categoryId, e);
            return false;
        }
    }

    @Override
    public boolean removeJokeFromCategory(int jokeId, int categoryId) {
        String sql = "DELETE FROM joke_categories WHERE joke_id = ? AND category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jokeId);
            stmt.setInt(2, categoryId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error removing joke ID: " + jokeId + " from category ID: " + categoryId, e);
            return false;
        }
    }

    @Override
    public List<Integer> findCategoryIdsByJokeId(int jokeId) {
        String sql = "SELECT category_id FROM joke_categories WHERE joke_id = ?";
        List<Integer> categoryIds = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jokeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categoryIds.add(rs.getInt("category_id"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding category IDs for joke ID: " + jokeId, e);
        }
        return categoryIds;
    }

    @Override
    public List<Integer> findJokeIdsByCategoryId(int categoryId) {
        String sql = "SELECT joke_id FROM joke_categories WHERE category_id = ?";
        List<Integer> jokeIds = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    jokeIds.add(rs.getInt("joke_id"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding joke IDs for category ID: " + categoryId, e);
        }
        return jokeIds;
    }
}
