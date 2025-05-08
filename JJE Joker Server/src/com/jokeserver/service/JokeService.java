package com.jokeserver.service;

import com.jokeserver.server.JokeServer.Response;
import model.*;

import java.util.List;

public interface JokeService {
    Response createUser(User user);
    Response getUser(int userId);
    Response submitJoke(Joke joke);
    Response getJoke(int jokeId);
    Response voteJoke(Vote vote);
    Response getTopJokes(int limit);
    Response createComment(Comment comment);
    Response getComments(int jokeId);
    Response createCategory(Category category);
    Response addJokeToCategory(int jokeId, int categoryId);
    Response createModerationRequest(ModerationRequest request);
    Response createReport(Report report);
    Response selectJokeOfTheDay();
}
