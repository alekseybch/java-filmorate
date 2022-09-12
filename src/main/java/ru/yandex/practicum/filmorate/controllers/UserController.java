package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        log.info("/users получен GET запрос");
        return userService.getAllUsers();
    }

    @PostMapping
    public User create(@RequestBody @Valid @NotNull User user) {
        log.info("/users получен POST запрос {}", user);
        userService.addUser(user);
        log.info("Пользователь добавлен {}", user);
        return user;
    }

    @PutMapping
    public User put(@RequestBody @Valid @NotNull User user) {
        log.info("/users получен PUT запрос {}", user);
        userService.updateUser(user);
        log.info("Пользователь обновлен {}", user);
        return user;
    }
}