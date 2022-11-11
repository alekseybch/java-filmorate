package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage extends DataStorage<User> {
    void createFriend(Integer userId, Integer friendId);

    void deleteFriend(Integer userId, Integer friendId);

    Collection<User> readFriends(Integer id);

    Collection<User> mutualFriends(Integer userId, Integer otherId);
}