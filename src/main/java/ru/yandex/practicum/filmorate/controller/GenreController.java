package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public Collection<Genre> findAllGenres() {
        log.info("/genres GET request received.");
        return genreService.getAll();
    }

    @GetMapping("/{id}")
    public Genre findGenreById(@PathVariable Integer id) {
        log.info("/genres/{id} GET - request to receive a genre has been received id = {}.", id);
        return genreService.getById(id);
    }
}