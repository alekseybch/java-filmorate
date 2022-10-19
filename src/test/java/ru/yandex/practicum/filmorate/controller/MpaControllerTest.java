package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class MpaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenRequestGetMpaRatings_thenGetAllMpaRatings() throws Exception {
        //when
        this.mockMvc.perform(
                        get("/mpa"))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[4].id").value(5))
                .andExpect(jsonPath("$[4].name").value("NC-17"))
                .andExpect(jsonPath("$[4].description").value("лицам до 18 лет просмотр запрещён"));
    }

    @Test
    void whenRequestGetMpaId1_thenGetMpaId1() throws Exception {
        //when
        this.mockMvc.perform(
                        get("/mpa/1"))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("G"))
                .andExpect(jsonPath("$.description").value("у фильма нет возрастных ограничений"));
    }

    @Test
    void whenRequestGetMpaWithFailId_thenNotFoundRequest404() throws Exception {
        //when
        this.mockMvc.perform(
                        get("/mpa/-1"))

                //then
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));
    }

}