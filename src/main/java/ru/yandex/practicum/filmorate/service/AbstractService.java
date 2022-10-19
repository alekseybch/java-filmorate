package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.storage.DataStorage;

import java.util.Collection;

public abstract class AbstractService<T> {
    DataStorage<T> storage;

    public AbstractService(DataStorage<T> storage) {
        this.storage = storage;
    }

    public void add(T obj) {
        storage.add(obj);
    }

    public void update(T obj) {
        storage.update(obj);
    }

    public T getById(int id) {
        return storage.getById(id);
    }

    public Collection<T> getAll() {
        return storage.getAll();
    }
}