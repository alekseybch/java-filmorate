package ru.yandex.practicum.filmorate.storage.impl.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    private int generateId() {
        return ++id;
    }

    @Override
    public void add(User user) {
        int id = generateId();
        user.setId(id);
        user.checkName();
        users.put(id, user);
    }

    @Override
    public User getById(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("User with id = %d not found.", id));
        }
        return users.get(id);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public void update(User user) {
        getById(user.getId());
        user.checkName();
        for (Integer friendId: users.get(user.getId()).getFriends()) {
            user.addFriend(friendId);
        }
        users.put(user.getId(), user);
    }

    @Override
    public void saveFriends(User user) {
        users.put(user.getId(), user);
    }
}