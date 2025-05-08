package dao;

import model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    int create(User user);
    Optional<User> findById(int userId);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    List<User> findByAccountType(String accountType);
    boolean update(User user);
    boolean updatePassword(int userId, String newPassword);
    boolean updateLastLogin(int userId);
    boolean updateAccountType(int userId, String newAccountType);
    boolean delete(int userId);
    boolean deactivate(int userId);
    boolean activate(int userId);
}