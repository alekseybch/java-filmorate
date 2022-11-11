package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.SortRequestException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
public class FilmService extends AbstractService<Film>{
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        super(filmStorage);
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Integer filmId, Integer userId) {
        userStorage.readById(userId);
        Film film = getById(filmId);
        film.addLike(userId);
        filmStorage.createLike(filmId, userId);
        update(film);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        userStorage.readById(userId);
        Film film = getById(filmId);
        film.deleteLike(userId);
        filmStorage.deleteLike(filmId, userId);
        update(film);
    }

    public Collection<Film> getTopFilms(Integer count) {
        if (count <= 0) {
            throw new IllegalArgumentException(String.format("Must be positive count = %d", count));
        }
        return filmStorage.readTopFilms(count);
    }

    public Collection<Film> getSortedDirectorFilms(Integer directorId, String sortBy) {
        switch (sortBy) {
            case "year":
                return filmStorage.readDirectorFilmsSortedByYear(directorId, sortBy);
            case "likes":
                return filmStorage.readDirectorFilmsSortedByLikes(directorId, sortBy);
            default:
                throw new SortRequestException(String.format("Sort must be 'year' or 'likes' sortBy = %s", sortBy));
        }
    }
}