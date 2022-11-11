package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("/films GET request received.");
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        log.info("/films/{id} GET - request to receive a film has been received id = {}.", id);
        return filmService.getById(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getTopFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        log.info("/films/{id} GET - request to receive a top {} films has been received.", count);
        return filmService.getTopFilms(count);
    }

    @GetMapping("director/{directorId}")
    public Collection<Film> getDirectorFilms(@PathVariable Integer directorId,
                                             @RequestParam String sortBy) {
        return filmService.getSortedDirectorFilms(directorId, sortBy);
    }

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        log.info("/films POST request received {}.", film);
        filmService.add(film);
        log.info("Film is created - {}.", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        log.info("/films PUT request received {}.", film);
        filmService.update(film);
        log.info("Film is updated {}.", film);
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id,
                        @PathVariable Integer userId) {
        log.info("/films/{id}/like/{userId} PUT - request to add " +
                "a like received id = {}, userId = {}.", id, userId);
        filmService.addLike(id, userId);
        log.info("Film id = {} has a like added from user id = {}.", id, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable Integer id) {
        log.info("/films DELETE - request to delete a film received id = {}.", id);
        filmService.delete(id);
        log.info("Film id = {} deleted.", id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id,
                           @PathVariable Integer userId) {
        log.info("/films/{id}/like/{userId} DELETE - request to delete " +
                "a like received id = {}, userId = {}.", id, userId);
        filmService.deleteLike(id, userId);
        log.info("Film id = {} has a like deleted from user id = {}.", id, userId);
    }
}