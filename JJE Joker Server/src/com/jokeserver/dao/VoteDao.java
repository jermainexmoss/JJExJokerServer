package com.jokeserver.dao;

import com.jokeserver.model.Vote;

import java.util.List;
import java.util.Optional;

public interface VoteDao {
    int create(Vote vote);
    Optional<Vote> findById(int voteId);
    Optional<Vote> findByJokeAndUser(int jokeId, int userId);
    List<Vote> findByJokeId(int jokeId);
    List<Vote> findByUserId(int userId);
    boolean update(Vote vote);
    boolean delete(int voteId);
    boolean deleteByJokeAndUser(int jokeId, int userId);
    int getJokeRating(int jokeId);
}

