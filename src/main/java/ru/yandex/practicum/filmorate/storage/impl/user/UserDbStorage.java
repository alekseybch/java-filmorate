package ru.yandex.practicum.filmorate.storage.impl.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Repository("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private static final String SQL_ADD_USER = "INSERT INTO users (email, login, user_name, birthday) " +
            "VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE_USER = "UPDATE users SET email = ?, login = ?, user_name = ?, birthday = ? " +
            "WHERE user_id = ?";
    private static final String SQL_DELETE_USER = "DELETE FROM users WHERE user_id = ?";
    private static final String SQL_GET_USER_BY_ID = "SELECT * FROM users WHERE user_id = ?";
    private static final String SQL_GET_ALL_USERS = "SELECT * FROM users";
    private static final String SQL_GET_ALL_FRIENDS = "SELECT u.* FROM friendships AS f LEFT JOIN users AS u " +
            "ON f.friend_id = u.user_id WHERE f.user_id = ?";
    private static final String SQL_GET_ALL_FRIENDS_ID = "SELECT friend_id FROM friendships WHERE user_id = ?";
    private static final String SQL_ADD_FRIEND = "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)";
    private static final String SQL_DELETE_FRIEND = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
    private static final String SQL_GET_COMMON_FRIENDS = "SELECT u.* FROM friendships AS f LEFT JOIN users AS u " +
            "ON f.friend_id = u.user_id WHERE f.user_id = ? INTERSECT SELECT u.* FROM friendships AS f " +
            "LEFT JOIN users AS u ON f.friend_id = u.user_id WHERE f.user_id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<User> getAll() {
        return jdbcTemplate.query(SQL_GET_ALL_USERS, this::mapRowToUser);
    }

    @Override
    public User getById(int id) {
        List<User> users = jdbcTemplate.query(SQL_GET_USER_BY_ID, this::mapRowToUser, id);
        if (users.size() != 1) {
            throw new NotFoundException(String.format("User with id = %d not found.", id));
        }
        return users.get(0);
    }

    @Override
    public void add(User user) {
        user.checkName();
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(SQL_ADD_USER, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
    }

    @Override
    public void update(User user) {
        getById(user.getId());
        user.checkName();

        jdbcTemplate.update(SQL_UPDATE_USER,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
    }

    @Override
    public void delete(int id) {
        getById(id);
        jdbcTemplate.update(SQL_DELETE_USER, id);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        jdbcTemplate.update(SQL_ADD_FRIEND, userId, friendId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        jdbcTemplate.update(SQL_DELETE_FRIEND, userId, friendId);
    }

    @Override
    public Collection<User> getFriends(int id) {
        getById(id);
        return jdbcTemplate.query(SQL_GET_ALL_FRIENDS, this::mapRowToUser, id);
    }

    @Override
    public Collection<User> commonFriends(int userId, int otherId) {
        getById(userId);
        getById(otherId);
        return jdbcTemplate.query(SQL_GET_COMMON_FRIENDS, this::mapRowToUser, userId, otherId);
    }

    private void getFriendsId(User user) {
        SqlRowSet friends = jdbcTemplate.queryForRowSet(SQL_GET_ALL_FRIENDS_ID, user.getId());
        while (friends.next()) {
            user.addFriend(friends.getInt("friend_id"));
        }
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = User.builder()
                .id(resultSet.getInt("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("user_name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
        getFriendsId(user);
        return user;
    }
}