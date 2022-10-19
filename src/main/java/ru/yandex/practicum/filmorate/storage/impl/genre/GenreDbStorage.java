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
    private static final String SQL_GET_GENRE_BY_ID = "SELECT genre_id, genre_name FROM genres WHERE genre_id = ?";
    private static final String SQL_GET_ALL_GENRES = "SELECT genre_id, genre_name FROM genres";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Genre> getAll() {
        return jdbcTemplate.query(SQL_GET_ALL_GENRES, this::mapRowToGenre);
    }

    @Override
    public Genre getById(int id) {
        List<Genre> genres = jdbcTemplate.query(SQL_GET_GENRE_BY_ID, this::mapRowToGenre, id);
        if (genres.size() != 1) {
            throw new NotFoundException(String.format("Genre with id = %d not found.", id));
        }
        return genres.get(0);
    }

    @Override
    public void add(Genre genre) {
    }

    @Override
    public void update(Genre genre) {
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("genre_name"))
                .build();
    }
}