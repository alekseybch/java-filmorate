package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.DataStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final DataStorage<Film> filmStorage;
    private final DataStorage<User> userStorage;

    public void add(Film film) {
        filmStorage.add(film);
    }

    public void update(Film film) {
        filmStorage.update(film);
    }

    public Film getById(int id) {
        return filmStorage.getById(id);
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public void addLike(int filmId, int userId) {
        userStorage.getById(userId); //проверить наличие пользователя
        filmStorage.getById(filmId).addLike(userId);
    }

    public void deleteLike(int filmId, int userId) {
        userStorage.getById(userId); //проверить наличие пользователя
        filmStorage.getById(filmId).deleteLike(userId);
    }

    public Collection<Film> getTopFilms(int count) {
        if (count <= 0) throw new IllegalArgumentException(String.format("Must be positive. count = %d", count));
        return filmStorage.getAll().stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}