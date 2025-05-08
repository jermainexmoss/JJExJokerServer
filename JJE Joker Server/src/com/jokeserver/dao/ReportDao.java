package com.jokeserver.dao;

import com.jokeserver.model.Report;

import java.util.List;
import java.util.Optional;

public interface ReportDao {
    int create(Report report);
    Optional<Report> findById(int reportId);
    List<Report> findByJokeId(int jokeId);
    List<Report> findByUserId(int userId);
    boolean update(Report report);
    boolean delete(int reportId);
}

