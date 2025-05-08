package com.jokeserver.controller;

import com.jokeserver.server.JokeServer.Request;
import com.jokeserver.server.JokeServer.Response;
import com.jokeserver.service.JokeService;
import java.util.logging.Logger;

public class MasterController {
    private static final Logger LOGGER = Logger.getLogger(MasterController.class.getName());
    private final JokeService jokeService;

    public MasterController(JokeService jokeService) {
        this.jokeService = jokeService;
    }

    public Response handleRequest(Request request) {
        String command = request.getCommand();
        Object payload = request.getPayload();
        LOGGER.info("Processing command: " + command);

        try {
            switch (command) {
                case "CREATE_USER":
                    return jokeService.createUser((com.jokeserver.model.User) payload);
                case "GET_USER":
                    return jokeService.getUser((Integer) payload);
                case "SUBMIT_JOKE":
                    return jokeService.submitJoke((com.jokeserver.model.Joke) payload);
                case "GET_JOKE":
                    return jokeService.getJoke((Integer) payload);
                case "VOTE_JOKE":
                    return jokeService.voteJoke((com.jokeserver.model.Vote) payload);
                case "GET_TOP_JOKES":
                    return jokeService.getTopJokes((Integer) payload);
                case "CREATE_COMMENT":
                    return jokeService.createComment((com.jokeserver.model.Comment) payload);
                case "GET_COMMENTS":
                    return jokeService.getComments((Integer) payload);
                case "CREATE_CATEGORY":
                    return jokeService.createCategory((com.jokeserver.model.Category) payload);
                case "ADD_JOKE_TO_CATEGORY":
                    int[] ids = (int[]) payload;
                    return jokeService.addJokeToCategory(ids[0], ids[1]);
                case "CREATE_MODERATION_REQUEST":
                    return jokeService.createModerationRequest((com.jokeserver.model.ModerationRequest) payload);
                case "CREATE_REPORT":
                    return jokeService.createReport((com.jokeserver.model.Report) payload);
                case "SELECT_JOKE_OF_THE_DAY":
                    return jokeService.selectJokeOfTheDay();
                default:
                    return new Response("ERROR", "Unknown command: " + command);
            }
        } catch (Exception e) {
            LOGGER.severe("Error processing command: " + command + " - " + e.getMessage());
            return new Response("ERROR", "Server error: " + e.getMessage());
        }
    }
}