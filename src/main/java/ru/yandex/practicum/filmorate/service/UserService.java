package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    private int generateId() {
        return ++id;
    }

    public void addUser(User user) {
        int id = generateId();
        user.setId(id);
        userCheckName(user);
        users.put(id, user);
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public void updateUser(User user) {
        if (users.containsKey(user.getId())) {
            userCheckName(user);
            users.put(user.getId(), user);
        } else {
            throw new NotFoundException("пользователь не найден.");
        }
    }

    private void userCheckName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
