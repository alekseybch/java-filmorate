package ru.yandex.practicum.filmorate.storage.impl.genre;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.DataStorage;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
public class GenreDbStorageTest {
    @Autowired
    @Qualifier("genreDbStorage")
    private DataStorage<Genre> genreStorage;

    @Test
    public void whenFindGenreById_thenGetGenreById1() {
        //when
        final Genre genre = genreStorage.getById(1);

        //then
        assertThat(genre)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    public void whenFindAllGenres_thenGetAllGenres() {
        //when
        final Collection<Genre> genres = genreStorage.getAll();

        //then
        assertNotNull(genres, "Genres are not returned.");
        assertEquals(6, genres.size(), "Incorrect number of genres.");
    }
}