package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.DataStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final LocalDate BIRTHDAY = LocalDate.of(1980, 2, 23);
    private static final LocalDate BAD_BIRTHDAY = LocalDate.now().plusDays(1);
    private final List<User> users = new ArrayList<>(
            List.of(User.builder()
                            .email("test@test.com")
                            .login("login")
                            .name("testname")
                            .birthday(BIRTHDAY)
                            .build(),
                    User.builder()
                            .email("mail@mail.com")
                            .login("friend")
                            .name("friendname")
                            .birthday(BIRTHDAY.minusYears(5))
                            .build(),
                    User.builder()
                            .email("mail@yandex.ru")
                            .login("friend2")
                            .name("friendname2")
                            .birthday(BIRTHDAY.minusYears(10))
                            .build()));

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DataStorage<User> userStorage;
    @Autowired
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenNewUser_whenCreated_thenAddedUserId1() throws Exception {
        //given
        String body = objectMapper.writeValueAsString(users.get(0));

        //when
        this.mockMvc.perform(
                        post("/users").content(body).contentType(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.name").value("testname"))
                .andExpect(jsonPath("$.birthday").value(BIRTHDAY.toString()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenNewUserWithNullName_whenCreated_thenAddedUserWithNameEqualsLogin() throws Exception {
        //given
        User user = User.builder()
                .email("test@test.com")
                .login("login")
                .name(null)
                .birthday(BIRTHDAY)
                .build();

        String body = objectMapper.writeValueAsString(user);

        //when
        this.mockMvc.perform(
                        post("/users").content(body).contentType(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.name").value("login"))
                .andExpect(jsonPath("$.birthday").value(BIRTHDAY.toString()));
    }

    @Test
    void givenNewUserWithSpaceName_whenCreated_thenBadRequest400() throws Exception {
        //given
        User user = User.builder()
                .email("test@test.com")
                .login("log in")
                .name("testname")
                .birthday(BIRTHDAY)
                .build();

        String body = objectMapper.writeValueAsString(user);

        //when
        this.mockMvc.perform(
                        post("/users").content(body).contentType(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    void givenNewUserWithFailEmail_whenCreated_thenBadRequest400() throws Exception {
        //given
        User user = User.builder()
                .email("testtest.com")
                .login("login")
                .name("testname")
                .birthday(BIRTHDAY)
                .build();

        String body = objectMapper.writeValueAsString(user);

        //when
        this.mockMvc.perform(
                        post("/users").content(body).contentType(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    void givenNewUserWithFailBirthday_whenCreated_thenBadRequest400() throws Exception {
        //given
        User user = User.builder()
                .email("test@test.com")
                .login("login")
                .name("testname")
                .birthday(BAD_BIRTHDAY)
                .build();

        String body = objectMapper.writeValueAsString(user);

        //when
        this.mockMvc.perform(
                        post("/users").content(body).contentType(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenUpdatedUser_whenUpdate_thenUpdatedUserId1() throws Exception {
        //given
        User updatedUser = User.builder()
                .id(1)
                .email("updated@test.com")
                .login("login")
                .name("Vasya")
                .birthday(BIRTHDAY)
                .build();

        userStorage.add(users.get(0));
        String body = objectMapper.writeValueAsString(updatedUser);

        //when
        this.mockMvc.perform(
                        put("/users").content(body).contentType(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("updated@test.com"))
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.name").value("Vasya"))
                .andExpect(jsonPath("$.birthday").value(BIRTHDAY.toString()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenUpdatedUserWithFailId_whenUpdate_thenNotFoundRequest404() throws Exception {
        //given
        User updatedUser = User.builder()
                .id(2)
                .email("updated@test.com")
                .login("login")
                .name("Vasya")
                .birthday(BIRTHDAY)
                .build();

        userStorage.add(users.get(0));
        String body = objectMapper.writeValueAsString(updatedUser);

        //when
        this.mockMvc.perform(
                        put("/users").content(body).contentType(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenUserId1_whenRequestGetAllUsers_thenGetAllUsers() throws Exception {
        //given
        userStorage.add(users.get(0));

        //when
        this.mockMvc.perform(
                        get("/users"))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("test@test.com"))
                .andExpect(jsonPath("$[0].login").value("login"))
                .andExpect(jsonPath("$[0].name").value("testname"))
                .andExpect(jsonPath("$[0].birthday").value(BIRTHDAY.toString()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenUserId1_whenRequestGetUserId1_thenGetUserById1() throws Exception {
        //given
        userStorage.add(users.get(0));

        //when
        this.mockMvc.perform(
                        get("/users/1"))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.name").value("testname"))
                .andExpect(jsonPath("$.birthday").value(BIRTHDAY.toString()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenUserId1_whenRequestGetUserFailId_thenNotFoundRequest404() throws Exception {
        //given
        userStorage.add(users.get(0));

        //when
        this.mockMvc.perform(
                        get("/users/-1"))

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenUserId1AndUserId2_whenAddFriend_thenAddedFriendId2ToUserId1AndAddedFriendId1ToUserId2() throws Exception {
        //given
        userStorage.add(users.get(0));
        userStorage.add(users.get(1));

        //when
        this.mockMvc.perform(
                        put("/users/1/friends/2"))

                //then
                .andExpect(status().isOk());

        final Set<Integer> friends = userStorage.getById(1).getFriends();
        assertNotNull(friends, "Friends are not returned.");
        assertEquals(1, friends.size(), "Incorrect number of friends.");

        final Set<Integer> friendFriends = userStorage.getById(2).getFriends();
        assertNotNull(friendFriends, "Friends are not returned.");
        assertEquals(1, friendFriends.size(), "Incorrect number of friends.");
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenUserId1_whenAddFriendWithFailId_thenNotFoundRequest404() throws Exception {
        //given
        userStorage.add(users.get(0));

        //when
        this.mockMvc.perform(
                        put("/users/1/friends/-1"))

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenUserId1WithFriendId3AndUserId2WithFriendId3_whenRequestGetCommonFriendUserId1AndUserId2_thenGetCommonFriend() throws Exception {
        //given
        userStorage.add(users.get(0));
        userStorage.add(users.get(1));
        userStorage.add(users.get(2));
        userService.addFriend(1, 3);
        userService.addFriend(2, 3);

        final Set<Integer> friends = userStorage.getById(1).getFriends();
        assertNotNull(friends, "Friends are not returned.");
        assertEquals(1, friends.size(), "Incorrect number of friends.");

        final Set<Integer> friendFriends = userStorage.getById(2).getFriends();
        assertNotNull(friendFriends, "Friends are not returned.");
        assertEquals(1, friendFriends.size(), "Incorrect number of friends.");

        //when
        this.mockMvc.perform(
                        get("/users/1/friends/common/2"))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].email").value("mail@yandex.ru"))
                .andExpect(jsonPath("$[0].login").value("friend2"))
                .andExpect(jsonPath("$[0].name").value("friendname2"))
                .andExpect(jsonPath("$[0].birthday").value(BIRTHDAY.minusYears(10).toString()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenUserId1WithFriendId2_whenDeleteFriend_thenDeletedFriend() throws Exception {
        //given
        userStorage.add(users.get(0));
        userStorage.add(users.get(1));
        userService.addFriend(1, 2);

        final Set<Integer> friends = userStorage.getById(1).getFriends();
        assertNotNull(friends, "Friends are not returned.");
        assertEquals(1, friends.size(), "Incorrect number of friends.");

        final Set<Integer> friendFriends = userStorage.getById(2).getFriends();
        assertNotNull(friendFriends, "Friends are not returned.");
        assertEquals(1, friendFriends.size(), "Incorrect number of friends.");

        //when
        this.mockMvc.perform(
                        delete("/users/1/friends/2"))

                //then
                .andExpect(status().isOk());

        assertNotNull(friends, "Friends are not returned.");
        assertEquals(0, friends.size(), "Incorrect number of friends.");

        assertNotNull(friendFriends, "Friends are not returned.");
        assertEquals(0, friendFriends.size(), "Incorrect number of friends.");
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenUserId1AndUserId2NotFriends_whenDeleteFriendWithFailId_thenNotFoundRequest404() throws Exception {
        //given
        userStorage.add(users.get(0));
        userStorage.add(users.get(1));

        //when
        this.mockMvc.perform(
                        delete("/users/1/friends/2"))

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));
    }
}