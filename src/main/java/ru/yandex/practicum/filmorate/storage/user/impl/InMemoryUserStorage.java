package ru.yandex.practicum.filmorate.storage.user.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    private int generateId() {
        return ++id;
    }

    @Override
    public void addUser(User user) {
        int id = generateId();
        user.setId(id);
        userCheckName(user);
        users.put(id, user);
    }

    @Override
    public User getUserById(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("User with id = %d not found.", id));
        }
        return users.get(id);
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public void updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException(String.format("User with id = %d not found.", user.getId()));
        }
        userCheckName(user);
        user.setFriends(users.get(user.getId()).getFriends());
        users.put(user.getId(), user);
    }

    private void userCheckName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
