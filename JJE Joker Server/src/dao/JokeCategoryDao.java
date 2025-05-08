package dao;

import java.util.List;

public interface JokeCategoryDao {
    boolean addJokeToCategory(int jokeId, int categoryId);
    boolean removeJokeFromCategory(int jokeId, int categoryId);
    List<Integer> findCategoryIdsByJokeId(int jokeId);
    List<Integer> findJokeIdsByCategoryId(int categoryId);
}
