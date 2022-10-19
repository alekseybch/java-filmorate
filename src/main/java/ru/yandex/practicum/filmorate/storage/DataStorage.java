package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;

public interface DataStorage<T> {
    Collection<T> getAll();

    T getById(int id);

    void add(T obj);

    void update(T obj);
}