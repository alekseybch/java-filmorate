package ru.yandex.practicum.filmorate.storage.impl.mpa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.DataStorage;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
public class MpaDbStorageTest {
    @Autowired
    @Qualifier("mpaDbStorage")
    private DataStorage<Mpa> mpaStorage;

    @Test
    public void whenFindMpaById_thenGetMpaById1() {
        //when
        final Mpa mpa = mpaStorage.readById(1);

        //then
        assertThat(mpa)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "G")
                .hasFieldOrPropertyWithValue("description", "у фильма нет возрастных ограничений");
    }

    @Test
    public void whenFindAllMpa_thenGetAllMpa() {
        //when
        final Collection<Mpa> mpa = mpaStorage.readAll();

        //then
        assertNotNull(mpa, "Mpa are not returned.");
        assertEquals(5, mpa.size(), "Incorrect number of mpa.");
    }
}