package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class FilmService {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    private int generateId() {
        return ++id;
    }

    public void addFilm(Film film) {
        int id = generateId();
        film.setId(id);

        films.put(id, film);
    }

    public Collection<Film> getAllFilms() {
        return films.values();
    }

    public void updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            throw new NotFoundException("фильм не найден.");
        }
    }
}
