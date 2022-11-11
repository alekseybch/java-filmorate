package ru.yandex.practicum.filmorate.storage.impl.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
public class UserDbStorageTest {
    private static final LocalDate BIRTHDAY = LocalDate.of(1980, 2, 23);
    @Autowired
    @Qualifier("userDbStorage")
    private UserStorage userStorage;

    User user = User.builder()
            .email("test@test.com")
            .login("login")
            .name("testname")
            .birthday(BIRTHDAY)
            .build();

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenNewUser_whenFindUserById_thenGetUserById1() {
        //given
        userStorage.create(user);

        //when
        final User testUser = userStorage.readById(1);

        //then
        assertThat(testUser)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("email", "test@test.com")
                .hasFieldOrPropertyWithValue("login", "login")
                .hasFieldOrPropertyWithValue("name", "testname")
                .hasFieldOrPropertyWithValue("birthday", BIRTHDAY);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenUpdatedUser_whenUpdateUserById_thenGetUpdatedUserById1() {
        //given
        final User updatedUser = User.builder()
                .id(1)
                .email("updated@test.com")
                .login("login")
                .name("Vasya")
                .birthday(BIRTHDAY)
                .build();

        userStorage.create(user);

        //when
        userStorage.update(updatedUser);
        final User testUser = userStorage.readById(1);

        //then
        assertThat(testUser)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("email", "updated@test.com")
                .hasFieldOrPropertyWithValue("login", "login")
                .hasFieldOrPropertyWithValue("name", "Vasya")
                .hasFieldOrPropertyWithValue("birthday", BIRTHDAY);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenNewUserId1AndNewUserId2_whenFindAllUsers_thenGetAllUsers() {
        //given
        final User testUser = User.builder()
                .email("mail@mail.com")
                .login("friend")
                .name("friendname")
                .birthday(BIRTHDAY.minusYears(5))
                .build();

        userStorage.create(user);
        userStorage.create(testUser);

        //when
        final Collection<User> users = userStorage.readAll();

        //then
        assertNotNull(users, "Users are not returned.");
        assertEquals(2, users.size(), "Incorrect number of users.");
    }
}