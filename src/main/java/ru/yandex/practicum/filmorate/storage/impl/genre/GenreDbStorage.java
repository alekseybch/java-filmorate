package ru.yandex.practicum.filmorate.storage.impl.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.DataStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Repository("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements DataStorage<Genre> {
    private static final String SQL_READ_GENRE_BY_ID = "select * from GENRES where GENRE_ID = ?";
    private static final String SQL_READ_ALL_GENRES = "select * from GENRES";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Genre> readAll() {
        return jdbcTemplate.query(SQL_READ_ALL_GENRES, this::mapRowToGenre);
    }

    @Override
    public Genre readById(Integer id) {
        List<Genre> genres = jdbcTemplate.query(SQL_READ_GENRE_BY_ID, this::mapRowToGenre, id);
        if (genres.size() != 1) {
            throw new NotFoundException(String.format("Genre with id = %d not found.", id));
        }
        return genres.get(0);
    }

    @Override
    public void create(Genre genre) {
    }

    @Override
    public void update(Genre genre) {
    }

    @Override
    public void delete(Integer id) {
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("genre_name"))
                .build();
    }
}