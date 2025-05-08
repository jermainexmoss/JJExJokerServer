package com.jokeserver.service.impl;

import com.jokeserver.dao.*;
import com.jokeserver.dao.impl.*;
import com.jokeserver.model.*;
import com.jokeserver.server.JokeServer.Response;
import com.jokeserver.service.JokeService;
import com.jokeserver.util.TransactionManager;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class JokeServiceImpl implements JokeService {
    private static final Logger LOGGER = Logger.getLogger(JokeServiceImpl.class.getName());
    private final UserDao userDao;
    private final JokeDao jokeDao;
    private final VoteDao voteDao;
    private final JokeOfTheDayDao jotdDao;
    private final CategoryDao categoryDao;
    private final JokeCategoryDao jokeCategoryDao;
    private final UserActionDao actionDao;
    private final CommentDao commentDao;
    private final ModerationRequestDao moderationRequestDao;
    private final ReportDao reportDao;

    public JokeServiceImpl() {
        userDao = new UserDaoImpl();
        jokeDao = new JokeDaoImpl();
        voteDao = new VoteDaoImpl();
        jotdDao = new JokeOfTheDayDaoImpl();
        categoryDao = new CategoryDaoImpl();
        jokeCategoryDao = new JokeCategoryDaoImpl();
        actionDao = new UserActionDaoImpl();
        commentDao = new CommentDaoImpl();
        moderationRequestDao = new ModerationRequestDaoImpl();
        reportDao = new ReportDaoImpl();
    }

    @Override
    public Response createUser(User user) {
        try (TransactionManager tx = new TransactionManager()) {
            if (userDao.findByUsername(user.getUsername()).isPresent()) {
                return new Response("ERROR", "Username already exists");
            }
            int userId = userDao.create(user);
            tx.commit();
            logAction(userId, "CREATE_USER", "User created: " + user.getUsername());
            return userId != -1 ? new Response("SUCCESS", userId) : new Response("ERROR", "Failed to create user");
        } catch (Exception e) {
            LOGGER.severe("Error creating user: " + e.getMessage());
            return new Response("ERROR", e.getMessage());
        }
    }

    @Override
    public Response getUser(int userId) {
        try {
            Optional<User> user = userDao.findById(userId);
            return user.isPresent() ?
                    new Response("SUCCESS", user.get()) :
                    new Response("ERROR", "User not found");
        } catch (Exception e) {
            LOGGER.severe("Error getting user: " + e.getMessage());
            return new Response("ERROR", e.getMessage());
        }
    }

    @Override
    public Response submitJoke(Joke joke) {
        try (TransactionManager tx = new TransactionManager()) {
            Optional<User> user = userDao.findById(joke.getUserId());
            if (user.isEmpty() || !"creator".equals(user.get().getAccountType())) {
                return new Response("ERROR", "Only creators can submit jokes");
            }
            int jokeId = jokeDao.create(joke);
            tx.commit();
            logAction(joke.getUserId(), "SUBMIT_JOKE", "Joke submitted: " + jokeId);
            return jokeId != -1 ? new Response("SUCCESS", jokeId) : new Response("ERROR", "Failed to submit joke");
        } catch (Exception e) {
            LOGGER.severe("Error submitting joke: " + e.getMessage());
            return new Response("ERROR", e.getMessage());
        }
    }

    @Override
    public Response getJoke(int jokeId) {
        try {
            Optional<Joke> joke = jokeDao.findById(jokeId);
            return joke.isPresent() ?
                    new Response("SUCCESS", joke.get()) :
                    new Response("ERROR", "Joke not found");
        } catch (Exception e) {
            LOGGER.severe("Error getting joke: " + e.getMessage());
            return new Response("ERROR", e.getMessage());
        }
    }

    @Override
    public Response voteJoke(Vote vote) {
        try (TransactionManager tx = new TransactionManager()) {
            Optional<User> user = userDao.findById(vote.getUserId());
            if (user.isEmpty() || !"regular".equals(user.get().getAccountType())) {
                return new Response("ERROR", "Only regular users can vote");
            }
            boolean success = voteDao.findByJokeAndUser(vote.getJokeId(), vote.getUserId())
                    .map(v -> voteDao.update(vote))
                    .orElseGet(() -> voteDao.create(vote) != -1);
            tx.commit();
            logAction(vote.getUserId(), "VOTE_JOKE", "Voted on joke: " + vote.getJokeId());
            return success ? new Response("SUCCESS", "Vote recorded") : new Response("ERROR", "Failed to record vote");
        } catch (Exception e) {
            LOGGER.severe("Error voting on joke: " + e.getMessage());
            return new Response("ERROR", e.getMessage());
        }
    }

    @Override
    public Response getTopJokes(int limit) {
        try {
            List<Joke> topJokes = jokeDao.findTopRatedJokes(limit);
            return new Response("SUCCESS", topJokes);
        } catch (Exception e) {
            LOGGER.severe("Error getting top jokes: " + e.getMessage());
            return new Response("ERROR", e.getMessage());
        }
    }

    @Override
    public Response createComment(Comment comment) {
        try (TransactionManager tx = new TransactionManager()) {
            Optional<User> user = userDao.findById(comment.getUserId());
            if (user.isEmpty() || !"regular".equals(user.get().getAccountType())) {
                return new Response("ERROR", "Only regular users can comment");
            }
            int commentId = commentDao.create(comment);
            tx.commit();
            logAction(comment.getUserId(), "CREATE_COMMENT", "Comment added to joke: " + comment.getJokeId());
            return commentId != -1 ? new Response("SUCCESS", commentId) : new Response("ERROR", "Failed to create comment");
        } catch (Exception e) {
            LOGGER.severe("Error creating comment: " + e.getMessage());
            return new Response("ERROR", e.getMessage());
        }
    }

    @Override
    public Response getComments(int jokeId) {
        try {
            List<Comment> comments = commentDao.findByJokeId(jokeId);
            return new Response("SUCCESS", comments);
        } catch (Exception e) {
            LOGGER.severe("Error getting comments: " + e.getMessage());
            return new Response("ERROR", e.getMessage());
        }
    }

    @Override
    public Response createCategory(Category category) {
        try (TransactionManager tx = new TransactionManager()) {
            int categoryId = categoryDao.create(category);
            tx.commit();
            return categoryId != -1 ? new Response("SUCCESS", categoryId) : new Response("ERROR", "Failed to create category");
        } catch (Exception e) {
            LOGGER.severe("Error creating category: " + e.getMessage());
            return new Response("ERROR", e.getMessage());
        }
    }

    @Override
    public Response addJokeToCategory(int jokeId, int categoryId) {
        try (TransactionManager tx = new TransactionManager()) {
            boolean success = jokeCategoryDao.addJokeToCategory(jokeId, categoryId);
            tx.commit();
            return success ?
                    new Response("SUCCESS", "Joke added to category") :
                    new Response("ERROR", "Failed to add joke to category");
        } catch (Exception e) {
            LOGGER.severe("Error adding joke to category: " + e.getMessage());
            return new Response("ERROR", e.getMessage());
        }
    }

    @Override
    public Response createModerationRequest(ModerationRequest request) {
        try (TransactionManager tx = new TransactionManager()) {
            Optional<User> user = userDao.findById(request.getUserId());
            if (user.isEmpty() || !("moderator".equals(user.get().getAccountType()) || "admin".equals(user.get().getAccountType()))) {
                return new Response("ERROR", "Only moderators or admins can create moderation requests");
            }
            int requestId = moderationRequestDao.create(request);
            tx.commit();
            logAction(request.getUserId(), "CREATE_MODERATION_REQUEST", "Moderation request created: " + requestId);
            return requestId != -1 ? new Response("SUCCESS", requestId) : new Response("ERROR", "Failed to create moderation request");
        } catch (Exception e) {
            LOGGER.severe("Error creating moderation request: " + e.getMessage());
            return new Response("ERROR", e.getMessage());
        }
    }

    @Override
    public Response createReport(Report report) {
        try (TransactionManager tx = new TransactionManager()) {
            Optional<User> user = userDao.findById(report.getUserId());
            if (user.isEmpty() || !"regular".equals(user.get().getAccountType())) {
                return new Response("ERROR", "Only regular users can create reports");
            }
            int reportId = reportDao.create(report);
            tx.commit();
            logAction(report.getUserId(), "CREATE_REPORT", "Report created for joke: " + report.getJokeId());
            return reportId != -1 ? new Response("SUCCESS", reportId) : new Response("ERROR", "Failed to create report");
        } catch (Exception e) {
            LOGGER.severe("Error creating report: " + e.get