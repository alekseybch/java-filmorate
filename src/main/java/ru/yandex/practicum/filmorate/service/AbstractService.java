package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.storage.DataStorage;

import java.util.Collection;

public abstract class AbstractService<T> {
    private final DataStorage<T> storage;

    public AbstractService(DataStorage<T> storage) {
        this.storage = storage;
    }

    public void add(T obj) {
        storage.create(obj);
    }

    public void delete(Integer id) {storage.delete(id);}

    public void update(T obj) {
        storage.update(obj);
    }

    public T getById(Integer id) {
        return storage.readById(id);
    }

    public Collection<T> getAll() {
        return storage.readAll();
    }
}