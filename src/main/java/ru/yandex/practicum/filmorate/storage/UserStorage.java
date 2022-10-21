package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage extends DataStorage<User> {
    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    Collection<User> getFriends(int id);

    Collection<User> commonFriends(int userId, int otherId);
}