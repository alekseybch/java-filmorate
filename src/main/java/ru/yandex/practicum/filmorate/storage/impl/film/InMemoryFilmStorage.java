package ru.yandex.practicum.filmorate.storage.impl.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DataStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements DataStorage<Film> {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    private int generateId() {
        return ++id;
    }

    @Override
    public void add(Film film) {
        film.setId(generateId());
        films.put(id, film);
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Film getById(int id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException(String.format("Film with id = %d not found.", id));
        }
        return films.get(id);
    }

    @Override
    public void update(Film film) {
        getById(film.getId());
        for (Integer userId: films.get(film.getId()).getLikes()) {
            film.addLike(userId);
        }
        films.put(film.getId(), film);
    }
}