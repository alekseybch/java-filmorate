package ru.yandex.practicum.filmorate.storage.impl.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.DataStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Repository("mpaDbStorage")
@RequiredArgsConstructor
public class MpaDbStorage implements DataStorage<Mpa> {
    private static final String SQL_GET_MPA_BY_ID = "SELECT mpa_id, mpa_name, mpa_description FROM mpa_ratings WHERE mpa_id = ?";
    private static final String SQL_GET_ALL_MPA = "SELECT mpa_id, mpa_name, mpa_description FROM mpa_ratings";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Mpa> getAll() {
        return jdbcTemplate.query(SQL_GET_ALL_MPA, this::mapRowToMpa);
    }

    @Override
    public Mpa getById(int id) {
        List<Mpa> mpaList = jdbcTemplate.query(SQL_GET_MPA_BY_ID, this::mapRowToMpa, id);
        if (mpaList.size() != 1) {
            throw new NotFoundException(String.format("Mpa with id = %d not found.", id));
        }
        return mpaList.get(0);
    }

    @Override
    public void add(Mpa mpa) {
    }

    @Override
    public void update(Mpa mpa) {
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("mpa_id"))
                .name(resultSet.getString("mpa_name"))
                .description(resultSet.getString("mpa_description"))
                .build();
    }
}