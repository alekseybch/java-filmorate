package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest {
    private static final LocalDate FIRST_RELEASE_DATE= LocalDate.of(1895, 12, 28);
    private static final LocalDate RELEASE_DATE= LocalDate.of(2015, 5, 12);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FilmService filmService;
    @Autowired
    private MockMvc mockMvc;

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void givenNewFilm_whenCreated_thenAddsFilm() throws Exception {
        //given
        Film film = Film.builder()
                .name("kakabanga film")
                .description("testdescription")
                .releaseDate(RELEASE_DATE)
                .duration(100)
                .build();

        String body = objectMapper.writeValueAsString(film);

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

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void givenUpdatedFilm_whenUpdate_thenUpdateFilm() throws Exception {
        //given
        Film film = Film.builder()
                .name("kakabanga film")
                .description("testdescription")
                .releaseDate(RELEASE_DATE)
                .duration(100)
                .build();

        Film updatedFilm = Film.builder()
                .id(1)
                .name("kakabanga film")
                .description("kavabanga")
                .releaseDate(RELEASE_DATE)
                .duration(300)
                .build();

        filmService.addFilm(film);
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

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void givenUpdatedFilmWithFailId_whenUpdate_thenBadRequest404() throws Exception {
        //given
        Film film = Film.builder()
                .name("kakabanga film")
                .description("testdescription")
                .releaseDate(RELEASE_DATE)
                .duration(100)
                .build();

        Film updatedFilm = Film.builder()
                .id(2)
                .name("kakabanga film")
                .description("kavabanga")
                .releaseDate(RELEASE_DATE)
                .duration(300)
                .build();

        filmService.addFilm(film);
        String body = objectMapper.writeValueAsString(updatedFilm);

        //when
        this.mockMvc.perform(
                        put("/films").content(body).contentType(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void givenNewFilm_whenGetFilms_thenGetAllFilm() throws Exception {
        //given
        Film film = Film.builder()
                .name("kakabanga film")
                .description("testdescription")
                .releaseDate(RELEASE_DATE)
                .duration(100)
                .build();

        filmService.addFilm(film);

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

}