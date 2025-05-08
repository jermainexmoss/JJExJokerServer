package com.jokeserver.dao;

import com.jokeserver.model.Joke;

import java.util.List;
import java.util.Optional;

public interface JokeDao {
    int create(Joke joke);
    Optional<Joke> findById(int jokeId);
    List<Joke> findAll();
    List<Joke> findByUserId(int userId);
    List<Joke> findByApprovalStatus(String approvalStatus);
    List<Joke> findTopRatedJokes(int limit);
    List<Joke> findRecentJokes(int limit);
    boolean update(Joke joke);
    boolean updateApprovalStatus(int jokeId, String approvalStatus, int approvedBy);
    boolean delete(int jokeId);
    int getJokeRating(int jokeId);
}

