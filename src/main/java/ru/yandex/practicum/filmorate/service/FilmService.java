package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.SortRequestException;
import ru.yandex.practicum.filmorate.exception.YearValidException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Service
public class FilmService extends AbstractService<Film>{
    private final static LocalDate FIRST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
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

    public Collection<Film> getTopFilms(Integer count, Integer genreId, Integer year) {
        if (count <= 0) {
            throw new IllegalArgumentException(String.format("Must be positive count = %d", count));
        }
        if (year != null && year < FIRST_RELEASE_DATE.getYear()) {
            throw new YearValidException("Invalid year. First release year 1895.");
        }
        if (genreId != null && year != null) {
            return filmStorage.readTopFilmsByGenreAndYear(count, genreId, year);
        } else if (genreId != null) {
            return filmStorage.readTopFilmsByGenre(count, genreId);
        } else if (year != null) {
            return filmStorage.readTopFilmsByYear(count, year);
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