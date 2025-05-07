package com.jokeserver.model;

import java.time.LocalDate;

public class JokeOfTheDay {
    private int jotdId;
    private int jokeId;
    private LocalDate date;


    public JokeOfTheDay() {}

    public JokeOfTheDay(int jotdId, int jokeId, LocalDate date) {
        this.jotdId = jotdId;
        this.jokeId = jokeId;
        this.date = date;
    }


    public int getJotdId() {
        return jotdId;
    }

    public void setJotdId(int jotdId) {
        this.jotdId = jotdId;
    }

    public int getJokeId() {
        return jokeId;
    }

    public void setJokeId(int jokeId) {
        this.jokeId = jokeId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "JokeOfTheDay{" +
                "jotdId=" + jotdId +
                ", jokeId=" + jokeId +
                ", date=" + date +
                '}';
    }
}