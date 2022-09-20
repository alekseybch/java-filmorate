package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public void addFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User userFriend = userStorage.getUserById(friendId);
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        if (userFriend.getFriends() == null) {
            userFriend.setFriends(new HashSet<>());
        }
        user.getFriends().add(friendId);
        userFriend.getFriends().add(userId);
    }

    public void deleteFriend(int userId, int friendId) {
        Set<Integer> userFriends = userStorage.getUserById(userId).getFriends();
        Set<Integer> friendFriends = userStorage.getUserById(friendId).getFriends();
        if (userFriends == null || !userFriends.contains(friendId)) {
            throw new NotFoundException(String.format("Friend with id = %d not found", friendId));
        }
        if (friendFriends == null || !friendFriends.contains(userId)) {
            throw new NotFoundException(String.format("Friend with id = %d not found", userId));
        }
        userFriends.remove(friendId);
        friendFriends.remove(userId);
    }

    public Collection<User> getFriends(int userId) {
        Collection<User> friends = new ArrayList<>();
        Set<Integer> userFriends = userStorage.getUserById(userId).getFriends();
        if (userFriends != null && !userFriends.isEmpty()) {
            for (int id : userFriends) {
                friends.add(userStorage.getUserById(id));
            }
        }
        return friends;
    }

    public Collection<User> commonFriends(int userId, int otherId) {
        Map<Integer, Integer> duplicateFriends = new HashMap<>();
        List<User> commonFriends = new ArrayList<>();
        Set<Integer> userFriends = userStorage.getUserById(userId).getFriends();
        Set<Integer> otherFriends = userStorage.getUserById(otherId).getFriends();

        if (userFriends != null && otherFriends != null &&
                !userFriends.isEmpty() && !otherFriends.isEmpty()) {
            for (int id : userFriends) {
                duplicateFriends.put(id, 1);
            }
            for (int id : otherFriends) {
                if (!duplicateFriends.containsKey(id)) {
                    duplicateFriends.put(id, 1);
                } else {
                    duplicateFriends.put(id, duplicateFriends.get(id) + 1);
                }
            }

            List<Integer> duplicates = duplicateFriends.entrySet().stream()
                    .filter(o -> o.getValue() > 1)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            if (!duplicates.isEmpty()) {
                for (int id : duplicates) {
                    commonFriends.add(userStorage.getUserById(id));
                }
            }
        }
        return commonFriends;
    }
}