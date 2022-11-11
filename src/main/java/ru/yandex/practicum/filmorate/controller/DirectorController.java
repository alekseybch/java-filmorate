package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> findAllDirectors() {
        log.info("/directors GET request received.");
        return directorService.getAll();
    }

    @GetMapping("{id}")
    public Director findDirectorById(@PathVariable("id") Integer id) {
        log.info("/directors/{id} GET - request to receive a director has been received id = {}.", id);
        return directorService.getById(id);
    }

    @PostMapping
    public Director addDirector(@RequestBody @Valid Director director) {
        log.info("/directors POST request received {}.", director);
        directorService.add(director);
        log.info("Director is created - {}.", director);
        return director;
    }

    @PutMapping
    public Director updateDirector(@RequestBody @Valid Director director) {
        log.info("/directors PUT request received {}.", director);
        directorService.update(director);
        log.info("Director is updated {}", director);
        return director;
    }

    @DeleteMapping("{id}")
    public void deleteDirector(@PathVariable("id") Integer id) {
        log.info("/directors DELETE - request to delete a director received id = {}.", id);
        directorService.delete(id);
        log.info("Director id = {} deleted.", id);
    }
}