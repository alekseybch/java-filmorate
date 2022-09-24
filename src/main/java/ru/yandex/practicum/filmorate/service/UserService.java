package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.DataStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final DataStorage<User> userStorage;

    public void add(User user) {
        userStorage.add(user);
    }

    public void update(User user) {
        userStorage.update(user);
    }

    public User getById(int id) {
        return userStorage.getById(id);
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public void addFriend(int userId, int friendId) {
        User user = userStorage.getById(userId);
        User userFriend = userStorage.getById(friendId);
        user.addFriend(friendId);
        userFriend.addFriend(userId);
    }

    public void deleteFriend(int userId, int friendId) {
        User user = userStorage.getById(userId);
        User userFriend = userStorage.getById(friendId);
        user.deleteFriend(friendId);
        userFriend.deleteFriend(userId);
    }

    public Collection<User> getFriends(int userId) {
        return userStorage.getById(userId).getFriends().stream()
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }

    public Collection<User> commonFriends(int userId, int otherId) {
        return userStorage.getById(userId).getFriends().stream()
                .filter(userStorage.getById(otherId).getFriends()::contains)
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }
}