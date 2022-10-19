package ru.yandex.practicum.filmorate.storage.impl.film;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
public class FilmDbStorageTest {
    private static final LocalDate RELEASE_DATE= LocalDate.of(2015, 5, 12);
    @Autowired
    @Qualifier("filmDbStorage")
    private FilmStorage filmStorage;

    Film film = Film.builder()
            .name("kakabanga film")
            .description("testdescription")
            .releaseDate(RELEASE_DATE)
            .duration(100)
            .mpa(Mpa.builder()
                    .id(2)
                    .build())
            .build();

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenNewFilm_whenFindFilmById_thenGetFilmById1() {
        //given
        filmStorage.add(film);

        //when
        final Film testFilm = filmStorage.getById(1);

        //then
        assertThat(testFilm)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "kakabanga film")
                .hasFieldOrPropertyWithValue("description", "testdescription")
                .hasFieldOrPropertyWithValue("releaseDate", RELEASE_DATE)
                .hasFieldOrPropertyWithValue("duration", 100);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenUpdatedFilm_whenUpdateFilmById_thenGetUpdatedFilmById1() {
        //given
        final Film updatedFilm = Film.builder()
                .id(1)
                .name("kakabanga film")
                .description("kavabanga")
                .releaseDate(RELEASE_DATE)
                .duration(300)
                .mpa(Mpa.builder()
                        .id(2)
                        .build())
                .build();

        filmStorage.add(film);

        //when
        filmStorage.update(updatedFilm);
        final Film testFilm = filmStorage.getById(1);

        //then
        assertThat(testFilm)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "kakabanga film")
                .hasFieldOrPropertyWithValue("description", "kavabanga")
                .hasFieldOrPropertyWithValue("releaseDate", RELEASE_DATE)
                .hasFieldOrPropertyWithValue("duration", 300);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenNewFilmId1AndNewFilmId2_whenFindAllFilms_thenGetAllFilms() {
        //given
        final Film testFilm = Film.builder()
                .name("kavabanga test film")
                .description("kavabanga")
                .releaseDate(RELEASE_DATE.minusYears(2))
                .duration(150)
                .mpa(Mpa.builder()
                        .id(4)
                        .build())
                .build();

        filmStorage.add(film);
        filmStorage.add(testFilm);

        //when
        final Collection<Film> films = filmStorage.getAll();

        //then
        assertNotNull(films, "Films are not returned.");
        assertEquals(2, films.size(), "Incorrect number of films.");
    }
}