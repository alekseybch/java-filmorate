package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

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
        userStorage.saveFriends(user);
    }

    public void deleteFriend(int userId, int friendId) {
        User user = getById(userId);
        getById(friendId);
        user.deleteFriend(friendId);
        userStorage.saveFriends(user);
    }

    public Collection<User> getFriends(int userId) {
        return userStorage.getById(userId).getFriends().stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }

    public Collection<User> commonFriends(int userId, int otherId) {
        Set<Integer> otherUser = userStorage.getById(otherId).getFriends();
        return userStorage.getById(userId).getFriends().stream()
                .filter(otherUser::contains)
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }
}