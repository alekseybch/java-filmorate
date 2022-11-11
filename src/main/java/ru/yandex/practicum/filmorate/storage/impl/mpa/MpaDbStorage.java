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
    private static final String SQL_READ_MPA_BY_ID = "select * from MPA_RATINGS where MPA_ID = ?";
    private static final String SQL_READ_ALL_MPA = "select * from MPA_RATINGS";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Mpa> readAll() {
        return jdbcTemplate.query(SQL_READ_ALL_MPA, this::mapRowToMpa);
    }

    @Override
    public Mpa readById(Integer id) {
        List<Mpa> mpaList = jdbcTemplate.query(SQL_READ_MPA_BY_ID, this::mapRowToMpa, id);
        if (mpaList.size() != 1) {
            throw new NotFoundException(String.format("Mpa with id = %d not found.", id));
        }
        return mpaList.get(0);
    }

    @Override
    public void create(Mpa mpa) {
    }

    @Override
    public void update(Mpa mpa) {
    }

    @Override
    public void delete(Integer id) {
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("mpa_id"))
                .name(resultSet.getString("mpa_name"))
                .description(resultSet.getString("mpa_description"))
                .build();
    }
}