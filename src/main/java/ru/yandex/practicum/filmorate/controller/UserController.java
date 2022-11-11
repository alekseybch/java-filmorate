package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> findAllUsers() {
        log.info("/users GET - request received.");
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable Integer id) {
        log.info("/users/{id} GET - request to receive a user has been received id = {}.", id);
        return userService.getById(id);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> findFriends(@PathVariable Integer id) {
        log.info("/users/{id}/friends GET - request to receive a friends has been received id = {}.", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> commonFriends(@PathVariable Integer id,
                                          @PathVariable Integer otherId) {
        log.info("/users/{id}/friends/common/{otherId} GET - request for common friends " +
                "has been received id = {}, otherId = {}.", id, otherId);
        return userService.commonFriends(id, otherId);
    }

    @PostMapping
    public User addUser(@RequestBody @Valid User user) {
        log.info("/users POST - request received {}.", user);
        userService.add(user);
        log.info("User id = {} is created {}.", user.getId(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        log.info("/users PUT - request received {}.", user);
        userService.update(user);
        log.info("User id = {} is updated {}.", user.getId(), user);
        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Integer id,
                          @PathVariable Integer friendId) {
        log.info("/users/{id}/friends/{friendId} PUT - request to add " +
                "a friend received id = {}, friendId = {}.", id, friendId);
        userService.addFriend(id, friendId);
        log.info("User id = {} added friend id = {}.", id, friendId);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        log.info("/users/{id} DELETE - request to delete id = {}.", id);
        userService.delete(id);
        log.info("User id = {} deleted.", id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id,
                             @PathVariable Integer friendId) {
        log.info("/users/{id}/friends/{friendId} DELETE - request to delete " +
                "a friend received id = {}, friendId = {}.", id, friendId);
        userService.deleteFriend(id, friendId);
        log.info("User id = {} delete friend id = {}.", id, friendId);
    }
}