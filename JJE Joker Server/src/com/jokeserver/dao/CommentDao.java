package com.jokeserver.dao;

import com.jokeserver.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentDao {
    int create(Comment comment);
    Optional<Comment> findById(int commentId);
    List<Comment> findByJokeId(int jokeId);
    List<Comment> findByUserId(int userId);
    boolean update(Comment comment);
    boolean delete(int commentId);
}
