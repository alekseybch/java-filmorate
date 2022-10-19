package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage extends DataStorage<Film> {
    void saveLikes(Film film);
    void saveGenres(Film film);
}