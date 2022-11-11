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
public class DirectorDbStorage implements DataStorage<Director> {
    private static final String SQL_CREATE_DIRECTOR = "insert into DIRECTORS (DIRECTOR_NAME) values (?)";
    private static final String SQL_UPDATE_DIRECTOR = "update DIRECTORS set DIRECTOR_NAME = ? where DIRECTOR_ID = ?";
    private static final String SQL_DELETE_DIRECTOR = "delete from DIRECTORS where DIRECTOR_ID = ?";
    private static final String SQL_READ_DIRECTOR_BY_ID = "select * from DIRECTORS where DIRECTOR_ID = ?";
    private static final String SQL_READ_ALL_DIRECTORS = "select * from DIRECTORS";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Director> readAll() {
        return jdbcTemplate.query(SQL_READ_ALL_DIRECTORS, this::mapRowToDirector);
    }

    @Override
    public Director readById(Integer id) {
        List<Director> directors = jdbcTemplate.query(SQL_READ_DIRECTOR_BY_ID, this::mapRowToDirector, id);
        if (directors.size() != 1) {
            throw new NotFoundException(String.format("Director with id = %d not found.", id));
        }
        return directors.get(0);
    }

    @Override
    public void create(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(SQL_CREATE_DIRECTOR, new String[]{"director_id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);

        director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
    }

    @Override
    public void update(Director director) {
        readById(director.getId());

        jdbcTemplate.update(SQL_UPDATE_DIRECTOR,
                director.getName(),
                director.getId());
    }

    @Override
    public void delete(Integer id) {
        readById(id);
        jdbcTemplate.update(SQL_DELETE_DIRECTOR, id);
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getInt("director_id"))
                .name(resultSet.getString("director_name"))
                .build();
    }
}