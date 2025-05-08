package dao;

import model.JokeOfTheDay;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JokeOfTheDayDao {
    int create(JokeOfTheDay jotd);
    Optional<JokeOfTheDay> findById(int jotdId);
    Optional<JokeOfTheDay> findByDate(LocalDate date);
    List<JokeOfTheDay> findAll();
    boolean update(JokeOfTheDay jotd);
    boolean delete(int jotdId);
    boolean selectJokeOfTheDay();
}
