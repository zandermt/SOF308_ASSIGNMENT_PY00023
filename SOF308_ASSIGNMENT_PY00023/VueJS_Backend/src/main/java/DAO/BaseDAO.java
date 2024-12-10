package DAO;

import java.util.List;

public interface BaseDAO<T, K> {
    List<T> findAll();
    T findById(K id);
    void create(T item);
    void update(T item);
    void deleteById(K id);
}
