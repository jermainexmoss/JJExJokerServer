package com.jokeserver.model;

import java.time.LocalDateTime;

public class Joke {
    private int jokeId;
    private int userId;
    private String jokeText;
    private LocalDateTime submissionDate;
    private String approvalStatus; // 'pending', 'approved', 'rejected'
    private Integer approvedBy; // Can be null
    private LocalDateTime approvalDate; // Can be null
    private int rating; // Calculated field, not stored in DB

    // Constructors
    public Joke() {}

    public Joke(int jokeId, int userId, String jokeText, LocalDateTime submissionDate,
                String approvalStatus, Integer approvedBy, LocalDateTime approvalDate) {
        this.jokeId = jokeId;
        this.userId = userId;
        this.jokeText = jokeText;
        this.submissionDate = submissionDate;
        this.approvalStatus = approvalStatus;
        this.approvedBy = approvedBy;
        this.approvalDate = approvalDate;
    }

    // Getters and Setters
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

    public String getJokeText() {
        return jokeText;
    }

    public void setJokeText(String jokeText) {
        this.jokeText = jokeText;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Integer getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Integer approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Joke{" +
                "jokeId=" + jokeId +
                ", userId=" + userId +
                ", jokeText='" + jokeText + '\'' +
                ", submissionDate=" + submissionDate +
                ", approvalStatus='" + approvalStatus + '\'' +
                ", rating=" + rating +
                '}';
    }
}
