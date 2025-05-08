package com.jokeserver.dao;

import com.jokeserver.model.UserAction;

import java.util.List;

public interface UserActionDao {
    int create(UserAction action);
    List<UserAction> findByUserId(int userId);
    List<UserAction> findByActionType(String actionType);
    List<UserAction> findAll();
}
