package ru.yandex.practicum.filmorate.storage.impl.director;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DataStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Repository("directorDbStorage")
@RequiredArgsConstructor
public class directorDbStorage implements DataStorage<Director> {
    private static final String SQL_ADD_DIRECTOR = "INSERT INTO directors (director_name) VALUES (?)";
    private static final String SQL_UPDATE_DIRECTOR = "UPDATE directors SET director_name = ? WHERE director_id = ?";
    private static final String SQL_DELETE_DIRECTOR = "DELETE FROM directors WHERE director_id = ?";
    private static final String SQL_GET_DIRECTOR_BY_ID = "SELECT * FROM directors WHERE director_id = ?";
    private static final String SQL_GET_ALL_DIRECTORS = "SELECT * FROM directors";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Director> getAll() {
        return jdbcTemplate.query(SQL_GET_ALL_DIRECTORS, this::mapRowToDirector);
    }

    @Override
    public Director getById(int id) {
        List<Director> directors = jdbcTemplate.query(SQL_GET_DIRECTOR_BY_ID, this::mapRowToDirector, id);
        if (directors.size() != 1) {
            throw new NotFoundException(String.format("Director with id = %d not found.", id));
        }
        return directors.get(0);
    }

    @Override
    public void add(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(SQL_ADD_DIRECTOR, new String[]{"director_id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);

        director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
    }

    @Override
    public void update(Director director) {
        getById(director.getId());

        jdbcTemplate.update(SQL_UPDATE_DIRECTOR,
                director.getName(),
                director.getId());
    }

    @Override
    public void delete(int id) {
        getById(id);
        jdbcTemplate.update(SQL_DELETE_DIRECTOR, id);
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getInt("director_id"))
                .name(resultSet.getString("director_name"))
                .build();
    }
}