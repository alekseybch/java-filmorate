package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        log.info("/users GET - request received");
        return userStorage.getAllUsers();
    }

    @GetMapping("/{id}")
    public User findUser(@PathVariable int id) {
        log.info("/users/{id} GET - request to receive a user has been received id = {}", id);
        return userStorage.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> findFriends(@PathVariable int id) {
        log.info("/users/{id}/friends GET - request to receive a friends has been received id = {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> commonFriends(@PathVariable int id,
                                        @PathVariable int otherId) {
        log.info("/users/{id}/friends/common/{otherId} GET - request for common friends " +
                "has been received id = {}, otherId = {}", id, otherId);
        return userService.commonFriends(id, otherId);
    }

    @PostMapping
    public User create(@RequestBody @Valid @NotNull User user) {
        log.info("/users POST - request received {}", user);
        userStorage.addUser(user);
        log.info("User id = {} is created {}", user.getId(), user);
        return user;
    }

    @PutMapping
    public User put(@RequestBody @Valid @NotNull User user) {
        log.info("/users PUT - request received {}", user);
        userStorage.updateUser(user);
        log.info("User id = {} is uprated {}", user.getId(), user);
        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id,
                          @PathVariable int friendId) {
        log.info("/users/{id}/friends/{friendId} PUT - request to add " +
                "a friend received id = {}, friendId = {}", id, friendId);
        userService.addFriend(id, friendId);
        log.info("User id = {} added friend id = {}", id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id,
                             @PathVariable int friendId) {
        log.info("/users/{id}/friends/{friendId} DELETE - request to delete " +
                "a friend received id = {}, friendId = {}", id, friendId);
        userService.deleteFriend(id, friendId);
        log.info("User id = {} delete friend id = {}", id, friendId);
    }
}