package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> findAll() {
        log.info("/directors GET request received");
        return directorService.getAll();
    }

    @GetMapping("{id}")
    public Director findDirector(@PathVariable("id") int id) {
        log.info("/directors/{id} GET - request to receive a director has been received id = {}", id);
        return directorService.getById(id);
    }

    @PostMapping
    public Director createDirector(@RequestBody @Valid @NotNull Director director) {
        log.info("/directors POST request received {}", director);
        directorService.add(director);
        log.info("Director is created - {}", director);
        return director;
    }

    @PutMapping
    public Director updateDirector(@RequestBody @Valid @NotNull Director director) {
        log.info("/directors PUT request received {}", director);
        directorService.update(director);
        log.info("Director is updated {}", director);
        return director;
    }

    @DeleteMapping("{id}")
    public void deleteDirector(@PathVariable("id") int id) {
        log.info("/directors Delete - request to delete a director received id = {}", id);
        directorService.delete(id);
        log.info("Director id = {} deleted", id);
    }
}