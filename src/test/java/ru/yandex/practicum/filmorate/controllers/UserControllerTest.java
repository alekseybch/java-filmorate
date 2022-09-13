package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final LocalDate BIRTHDAY = LocalDate.of(1980, 2, 23);
    private static final LocalDate BAD_BIRTHDAY = LocalDate.now().plusDays(1);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void givenNewUser_whenCreated_thenAddsUser() throws Exception {
        //given
        User user = User.builder()
                .email("test@test.com")
                .login("login")
                .name("testname")
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
                .andExpect(jsonPath("$.name").value("testname"))
                .andExpect(jsonPath("$.birthday").value(BIRTHDAY.toString()));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void givenNewUserWithNullName_whenCreated_thenAddsUserWithNameEqualsLogin() throws Exception {
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

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void givenUpdatedUser_whenUpdate_thenUpdateUser() throws Exception {
        //given
        User user = User.builder()
                .email("test@test.com")
                .login("login")
                .name("testname")
                .birthday(BIRTHDAY)
                .build();
        User updatedUser = User.builder()
                .id(1)
                .email("updated@test.com")
                .login("login")
                .name("Vasya")
                .birthday(BIRTHDAY)
                .build();

        userService.addUser(user);
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

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void givenUpdatedUserWithFailId_whenUpdate_thenBadRequest404() throws Exception {
        //given
        User user = User.builder()
                .email("test@test.com")
                .login("login")
                .name("testname")
                .birthday(BIRTHDAY)
                .build();
        User updatedUser = User.builder()
                .id(2)
                .email("updated@test.com")
                .login("login")
                .name("Vasya")
                .birthday(BIRTHDAY)
                .build();

        userService.addUser(user);
        String body = objectMapper.writeValueAsString(updatedUser);

        //when
        this.mockMvc.perform(
                        put("/users").content(body).contentType(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void givenNewUser_whenGetUsers_thenGetAllUsers() throws Exception {
        //given
        User user = User.builder()
                .email("test@test.com")
                .login("login")
                .name("testname")
                .birthday(BIRTHDAY)
                .build();

        userService.addUser(user);

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

}