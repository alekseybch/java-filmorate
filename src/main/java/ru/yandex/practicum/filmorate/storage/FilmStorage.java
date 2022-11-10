package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage extends DataStorage<Film> {
    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    Collection<Film> getTopFilms(int count);

    Collection<Film> getSortedDirectorFilmsByYear(int directorId, String sortBy);

    Collection<Film> getSortedDirectorFilmsByLikes(int directorId, String sortBy);
}