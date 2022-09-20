package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("/films GET request received");
        return filmStorage.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film findFilm(@PathVariable int id) {
        log.info("/films/{id} GET - request to receive a film has been received id = {}", id);
        return filmStorage.getFilmById(id);
    }

    @GetMapping("/popular")
    public Collection<Film> findTopFilm(@RequestParam(defaultValue = "10", required = false) int count) {
        log.info("/films/{id} GET - request to receive a top {} films has been received", count);
        return filmService.getTopFilms(count);
    }

    @PostMapping
    public Film create(@RequestBody @Valid @NotNull Film film) {
        log.info("/films POST request received {}", film);
        filmStorage.addFilm(film);
        log.info("Film is created - {}", film);
        return film;
    }

    @PutMapping
    public Film put(@RequestBody @Valid @NotNull Film film) {
        log.info("/films PUT request received {}", film);
        filmStorage.updateFilm(film);
        log.info("Film is updated {}", film);
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id,
                        @PathVariable int userId) {
        log.info("/films/{id}/like/{userId} PUT - request to add " +
                "a like received id = {}, userId = {}", id, userId);
        filmService.addLike(id, userId);
        log.info("Film id = {} has a like added from user id = {}", id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id,
                           @PathVariable int userId) {
        log.info("/films/{id}/like/{userId} Delete - request to delete " +
                "a like received id = {}, userId = {}", id, userId);
        filmService.deleteLike(id, userId);
        log.info("Film id = {} has a like deleted from user id = {}", id, userId);
    }
}