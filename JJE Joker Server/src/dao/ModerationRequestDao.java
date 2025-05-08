package dao;

import model.ModerationRequest;

import java.util.List;
import java.util.Optional;

public interface ModerationRequestDao {
    int create(ModerationRequest request);
    Optional<ModerationRequest> findById(int requestId);
    List<ModerationRequest> findByUserId(int userId);
    List<ModerationRequest> findByStatus(String status);
    boolean update(ModerationRequest request);
    boolean updateStatus(int requestId, String status, int processedBy);
    boolean delete(int requestId);
}

