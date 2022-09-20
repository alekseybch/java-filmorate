package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public void addLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId); //проверить наличие пользователя
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        film.getLikes().add(userId);
    }

    public void deleteLike(int filmId, int userId) {
        Set<Integer> userLikes = filmStorage.getFilmById(filmId).getLikes();
        userStorage.getUserById(userId); //проверить наличие пользователя
        if (userLikes == null || !userLikes.contains(userId)) {
            throw new NotFoundException(String.format("Like with id = %d not found", userId));
        }
        userLikes.remove(userId);
    }

    public Collection<Film> getTopFilms(int count) {
        if (count <= 0) throw new IllegalArgumentException(String.format("Must be positive count = %d", count));
        return filmStorage.getAllFilms().stream()
                .sorted((p0, p1) -> compare(p0.getLikes(), p1.getLikes()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compare(Set<Integer> p0, Set<Integer> p1) {
        if (p0 == null) {
            return 1;
        } else if (p1 == null) {
            return -1;
        }
        return p1.size() - p0.size();
    }
}