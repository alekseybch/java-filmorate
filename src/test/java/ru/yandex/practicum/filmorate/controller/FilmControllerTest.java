package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class FilmControllerTest {
    private static final LocalDate FIRST_RELEASE_DATE= LocalDate.of(1895, 12, 28);
    private static final LocalDate RELEASE_DATE= LocalDate.of(2015, 5, 12);
    private static final LocalDate BIRTHDAY = LocalDate.of(1980, 2, 23);
    private final List<User> users = new ArrayList<>(
            List.of(User.builder()
                            .email("test@test.com")
                            .login("login")
                            .name("testname")
                            .birthday(BIRTHDAY)
                            .build(),
                    User.builder()
                            .email("mail@mail.com")
                            .login("vasya")
                            .name("vasyaname")
                            .birthday(BIRTHDAY.minusYears(5))
                            .build()));
    private final List<Film> films = new ArrayList<>(
            List.of(Film.builder()
                            .name("kakabanga film")
                            .description("testdescription")
                            .releaseDate(RELEASE_DATE)
                            .duration(100)
                            .mpa(Mpa.builder()
                                    .id(2)
                                    .build())
                            .build(),
                    Film.builder()
                            .name("kavabanga test film")
                            .description("kavabanga")
                            .releaseDate(RELEASE_DATE.minusYears(2))
                            .duration(150)
                            .mpa(Mpa.builder()
                                    .id(4)
                                    .build())
                            .build()));

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    @Qualifier("filmDbStorage")
    private FilmStorage filmStorage;
    @Autowired
    private FilmService filmService;
    @Autowired
    @Qualifier("userDbStorage")
    private UserStorage userStorage;
    @Autowired
    private MockMvc mockMvc;

    public User getUser(int id) {
        return users.get(id);
    }

    public Film getFilm(int id) {
        return films.get(id);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenNewFilm_whenCreated_thenAddsFilmId1() throws Exception {
        //given
        String body = objectMapper.writeValueAsString(getFilm(0));

        //when
        this.mockMvc.perform(
                        post("/films").content(body).contentType(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("kakabanga film"))
                .andExpect(jsonPath("$.description").value("testdescription"))
                .andExpect(jsonPath("$.releaseDate").value(RELEASE_DATE.toString()))
                .andExpect(jsonPath("$.duration").value(100));
    }

    @Test
    void givenNewFilmWithNullName_whenCreated_thenBadRequest400() throws Exception {
        //given
        Film film = Film.builder()
                .name(null)
                .description("testdescription")
                .releaseDate(RELEASE_DATE)
                .duration(100)
                .build();

        String body = objectMapper.writeValueAsString(film);

        //when
        this.mockMvc.perform(
                        post("/films").content(body).contentType(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    void givenNewFilmWith201CharsetDescription_whenCreated_thenBadRequest400() throws Exception {
        //given
        Film film = Film.builder()
                .name("kakabanga film")
                .description(RandomString.make(201))
                .releaseDate(RELEASE_DATE)
                .duration(100)
                .build();

        String body = objectMapper.writeValueAsString(film);

        //when
        this.mockMvc.perform(
                        post("/films").content(body).contentType(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    void givenNewFilmWithFailReleaseDate_whenCreated_thenBadRequest400() throws Exception {
        //given
        Film film = Film.builder()
                .name("kakabanga film")
                .description("testdescription")
                .releaseDate(FIRST_RELEASE_DATE.minusDays(1))
                .duration(100)
                .build();

        String body = objectMapper.writeValueAsString(film);

        //when
        this.mockMvc.perform(
                        post("/films").content(body).contentType(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    void givenNewFilmWithFailDuration_whenCreated_thenBadRequest400() throws Exception {
        //given
        Film film = Film.builder()
                .name("kakabanga film")
                .description("testdescription")
                .releaseDate(RELEASE_DATE)
                .duration(-1)
                .build();

        String body = objectMapper.writeValueAsString(film);

        //when
        this.mockMvc.perform(
                        post("/films").content(body).contentType(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenUpdatedFilm_whenUpdate_thenUpdateFilmId1() throws Exception {
        //given
        Film updatedFilm = Film.builder()
                .id(1)
                .name("kakabanga film")
                .description("kavabanga")
                .releaseDate(RELEASE_DATE)
                .duration(300)
                .mpa(Mpa.builder()
                        .id(3)
                        .build())
                .build();

        filmStorage.create(getFilm(0));
        String body = objectMapper.writeValueAsString(updatedFilm);

        //when
        this.mockMvc.perform(
                        put("/films").content(body).contentType(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("kakabanga film"))
                .andExpect(jsonPath("$.description").value("kavabanga"))
                .andExpect(jsonPath("$.releaseDate").value(RELEASE_DATE.toString()))
                .andExpect(jsonPath("$.duration").value(300));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenUpdatedFilmWithFailId_whenUpdate_thenNotFoundRequest404() throws Exception {
        //given
        Film updatedFilm = Film.builder()
                .id(2)
                .name("kakabanga film")
                .description("kavabanga")
                .releaseDate(RELEASE_DATE)
                .duration(300)
                .mpa(Mpa.builder()
                        .id(4)
                        .build())
                .build();

        filmStorage.create(getFilm(0));
        String body = objectMapper.writeValueAsString(updatedFilm);

        //when
        this.mockMvc.perform(
                        put("/films").content(body).contentType(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenFilmWithId1_whenRequestGetFilms_thenGetAllFilm() throws Exception {
        //given
        filmStorage.create(getFilm(0));

        //when
        this.mockMvc.perform(
                        get("/films"))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("kakabanga film"))
                .andExpect(jsonPath("$[0].description").value("testdescription"))
                .andExpect(jsonPath("$[0].releaseDate").value(RELEASE_DATE.toString()))
                .andExpect(jsonPath("$[0].duration").value(100));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenFilmWithId1_whenRequestGetFilmId1_thenGetFilmId1() throws Exception {
        //given
        filmStorage.create(getFilm(0));

        //when
        this.mockMvc.perform(
                        get("/films/1"))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("kakabanga film"))
                .andExpect(jsonPath("$.description").value("testdescription"))
                .andExpect(jsonPath("$.releaseDate").value(RELEASE_DATE.toString()))
                .andExpect(jsonPath("$.duration").value(100));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenFilmId1_whenRequestGetFilmWithFailId_thenNotFoundRequest404() throws Exception {
        //given
        filmStorage.create(getFilm(0));

        //when
        this.mockMvc.perform(
                        get("/films/-1"))

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenFilmId1AndUserId1_whenAddLikeToFilmId1FromUserId1_thenAddedLikeOnFilmId1() throws Exception {
        //given
        filmStorage.create(getFilm(0));
        userStorage.create(getUser(0));

        //when
        this.mockMvc.perform(
                        put("/films/1/like/1"))

                //then
                .andExpect(status().isOk());

        final Set<Integer> likes = filmStorage.readById(1).getLikes();
        assertNotNull(likes, "Likes are not returned.");
        assertEquals(1, likes.size(), "Incorrect number of likes.");
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenFilmId1With1LikeFromUserId1_whenDeleteLikeFromUserId1_thenDeletedLikeFilmId1() throws Exception {
        //given
        filmStorage.create(getFilm(0));
        userStorage.create(getUser(0));
        filmService.addLike(1, 1);

        Set<Integer> likes = filmStorage.readById(1).getLikes();
        assertNotNull(likes, "Likes are not returned.");
        assertEquals(1, likes.size(), "Incorrect number of likes.");

        //when
        this.mockMvc.perform(
                        delete("/films/1/like/1"))

                //then
                .andExpect(status().isOk());

        likes = filmStorage.readById(1).getLikes();
        assertNotNull(likes, "Likes are not returned.");
        assertEquals(0, likes.size(), "Incorrect number of likes.");
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenFilmId1With1NoLikeFromUserId1_whenDeleteLikeFromUserFailId_thenNotFoundRequest404() throws Exception {
        //given
        filmStorage.create(getFilm(0));
        userStorage.create(getUser(0));

        //when
        this.mockMvc.perform(
                        delete("/films/1/like/1"))

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenFilmId1With1LikeAndFilmId2With2Likes_whenRequestGetTop2Films_thenGetTop2Films() throws Exception {
        //given
        filmStorage.create(getFilm(0));
        filmStorage.create(getFilm(1));
        userStorage.create(getUser(0));
        userStorage.create(getUser(1));
        filmService.addLike(1, 1);
        filmService.addLike(2, 1);
        filmService.addLike(2, 2);

        final Collection<Film> likes = filmService.getTopFilms(2, null, null);
        assertNotNull(likes, "Films are not returned.");
        assertEquals(2, likes.size(), "Incorrect number of films.");

        //when
        this.mockMvc.perform(
                        get("/films//popular?count=2"))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("kavabanga test film"))
                .andExpect(jsonPath("$[0].description").value("kavabanga"))
                .andExpect(jsonPath("$[0].releaseDate").value(RELEASE_DATE.minusYears(2).toString()))
                .andExpect(jsonPath("$[0].duration").value(150));
    }
}