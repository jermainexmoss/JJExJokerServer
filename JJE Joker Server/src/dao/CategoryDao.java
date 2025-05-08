package dao;
import model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryDao {
    int create(Category category);
    Optional<Category> findById(int categoryId);
    Optional<Category> findByName(String categoryName);
    List<Category> findAll();
    boolean update(Category category);
    boolean delete(int categoryId);
}
