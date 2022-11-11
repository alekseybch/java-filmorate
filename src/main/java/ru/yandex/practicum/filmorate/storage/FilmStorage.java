package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage extends DataStorage<Film> {
    void createLike(Integer filmId, Integer userId);

    void deleteLike(Integer filmId, Integer userId);

    Collection<Film> readTopFilms(Integer count);

    Collection<Film> readDirectorFilmsSortedByYear(Integer directorId, String sortBy);

    Collection<Film> readDirectorFilmsSortedByLikes(Integer directorId, String sortBy);
}