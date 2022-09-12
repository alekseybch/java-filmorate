package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("/films получен GET запрос");
        return filmService.getAllFilms();
    }

    @PostMapping
    public Film create(@RequestBody @Valid @NotNull Film film) {
        log.info("/films получен POST запрос {}", film);
        filmService.addFilm(film);
        log.info("Фильм добавлен {}", film);
        return film;
    }

    @PutMapping
    public Film put(@RequestBody @Valid @NotNull Film film) {
        log.info("/films получен PUT запрос {}", film);
        filmService.updateFilm(film);
        log.info("Фильм обновлен {}", film);
        return film;
    }
}