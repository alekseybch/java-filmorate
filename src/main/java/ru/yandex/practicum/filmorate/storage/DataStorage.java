package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;

public interface DataStorage<T> {
    Collection<T> readAll();

    T readById(Integer id);

    void create(T obj);

    void update(T obj);

    void delete(Integer id);
}