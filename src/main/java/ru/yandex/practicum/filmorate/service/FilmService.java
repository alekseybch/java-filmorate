package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadSortRequestException;
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

    public void addLike(int filmId, int userId) {
        userStorage.getById(userId); // check user
        Film film = getById(filmId);
        film.addLike(userId);
        filmStorage.addLike(filmId, userId);
        update(film);
    }

    public void deleteLike(int filmId, int userId) {
        userStorage.getById(userId); // check user
        Film film = getById(filmId);
        film.deleteLike(userId);
        filmStorage.deleteLike(filmId, userId);
        update(film);
    }

    public Collection<Film> getTopFilms(int count) {
        if (count <= 0) throw new IllegalArgumentException(String.format("Must be positive count = %d", count));
        return filmStorage.getTopFilms(count);
    }

    public Collection<Film> getSortedDirectorFilms(int directorId, String sortBy) {
        if (!(sortBy.equals("year") || sortBy.equals("likes")))
            throw new BadSortRequestException(String.format("Sort must be 'year' or 'likes' sortBy = %s", sortBy));
        return filmStorage.getSortedDirectorFilms(directorId, sortBy);
    }
}