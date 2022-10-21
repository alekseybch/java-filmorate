package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
public class UserService extends AbstractService<User> {
    private final UserStorage userStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        super(userStorage);
        this.userStorage = userStorage;
    }

    public void addFriend(int userId, int friendId) {
        User user = getById(userId);
        getById(friendId); // check friend
        user.addFriend(friendId);
        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        User user = getById(userId);
        getById(friendId);
        user.deleteFriend(friendId);
        userStorage.deleteFriend(userId, friendId);
    }

    public Collection<User> getFriends(int userId) {
        return userStorage.getFriends(userId);
    }

    public Collection<User> commonFriends(int userId, int otherId) {
        return userStorage.commonFriends(userId, otherId);
    }
}