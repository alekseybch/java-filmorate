package ru.yandex.practicum.filmorate.storage.impl.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.DataStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements DataStorage<User> {

    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    private int generateId() {
        return ++id;
    }

    @Override
    public void add(User user) {
        int id = generateId();
        user.setId(id);
        userCheckName(user);
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
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException(String.format("User with id = %d not found.", user.getId()));
        }
        userCheckName(user);
        for (Integer friendId: users.get(user.getId()).getFriends()) {
            user.addFriend(friendId);
        }
        users.put(user.getId(), user);
    }

    private void userCheckName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
