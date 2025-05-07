package com.jokeserver.model;

import java.time.LocalDateTime;

public class Vote {
    private int voteId;
    private int jokeId;
    private int userId;
    private int voteValue; // 1 for upvote, -1 for downvote
    private LocalDateTime voteDate;

    // Constructors
    public Vote() {}

    public Vote(int voteId, int jokeId, int userId, int voteValue, LocalDateTime voteDate) {
        this.voteId = voteId;
        this.jokeId = jokeId;
        this.userId = userId;
        this.voteValue = voteValue;
        this.voteDate = voteDate;
    }

    // Getters and Setters
    public int getVoteId() {
        return voteId;
    }

    public void setVoteId(int voteId) {
        this.voteId = voteId;
    }

    public int getJokeId() {
        return jokeId;
    }

    public void setJokeId(int jokeId) {
        this.jokeId = jokeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getVoteValue() {
        return voteValue;
    }

    public void setVoteValue(int voteValue) {
        this.voteValue = voteValue;
    }

    public LocalDateTime getVoteDate() {
        return voteDate;
    }

    public void setVoteDate(LocalDateTime voteDate) {
        this.voteDate = voteDate;
    }

    @Override
    public String toString() {
        return "Vote{" +
                "voteId=" + voteId +
                ", jokeId=" + jokeId +
                ", userId=" + userId +
                ", voteValue=" + voteValue +
                ", voteDate=" + voteDate +
                '}';
    }
}

