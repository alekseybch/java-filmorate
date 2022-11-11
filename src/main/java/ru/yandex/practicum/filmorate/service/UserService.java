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

    public void addFriend(Integer userId, Integer friendId) {
        User user = getById(userId);
        getById(friendId); // check friend
        user.addFriend(friendId);
        userStorage.createFriend(userId, friendId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        User user = getById(userId);
        getById(friendId);
        user.deleteFriend(friendId);
        userStorage.deleteFriend(userId, friendId);
    }

    public Collection<User> getFriends(Integer userId) {
        return userStorage.readFriends(userId);
    }

    public Collection<User> commonFriends(Integer userId, Integer otherId) {
        return userStorage.mutualFriends(userId, otherId);
    }
}