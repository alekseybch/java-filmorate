package ru.yandex.practicum.filmorate.storage.impl.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Repository("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private static final String SQL_ADD_FILM = "INSERT INTO films (film_name, description, release_date, duration, " +
            "rating, mpa_id) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE_FILM = "UPDATE films SET film_name = ?, description = ?, release_date = ?, " +
            "duration = ?, rating = ?, mpa_id = ? WHERE film_id = ?";
    private static final String SQL_GET_FILM_BY_ID = "SELECT f.film_id, f.film_name, f.description, f.release_date, " +
            "f.duration, f.rating, f.mpa_id, m.mpa_name, m.mpa_description FROM films AS f " +
            "LEFT JOIN mpa_ratings AS m ON f.mpa_id = m.mpa_id WHERE f.film_id = ?";
    private static final String SQL_GET_ALL_FILMS = "SELECT f.film_id, f.film_name, f.description, f.release_date, " +
            "f.duration, f.rating, f.mpa_id, m.mpa_name, m.mpa_description FROM films AS f " +
            "LEFT JOIN mpa_ratings AS m ON f.mpa_id = m.mpa_id";
    private static final String SQL_ADD_LIKE = "INSERT INTO movies_likes (film_id, user_id) VALUES (?, ?)";
    private static final String SQL_GET_ALL_LIKES = "SELECT user_id FROM movies_likes WHERE film_id = ?";
    private static final String SQL_DELETE_LIKES = "DELETE FROM movies_likes WHERE film_id = ?";
    private static final String SQL_ADD_GENRE = "INSERT INTO movies_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String SQL_GET_ALL_GENRES = "SELECT genre_id FROM movies_genres WHERE film_id = ?";
    private static final String SQL_DELETE_GENRES = "DELETE FROM movies_genres WHERE film_id = ?";
    private static final String SQL_GET_GENRE_BY_ID = "SELECT genre_id, genre_name FROM genres WHERE genre_id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Film> getAll() {
        return jdbcTemplate.query(SQL_GET_ALL_FILMS, this::mapRowToFilm);
    }

    @Override
    public Film getById(int id) {
        List<Film> films = jdbcTemplate.query(SQL_GET_FILM_BY_ID, this::mapRowToFilm, id);
        if (films.size() != 1) {
            throw new NotFoundException(String.format("Film with id = %d not found.", id));
        }
        return films.get(0);
    }

    @Override
    public void add(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(SQL_ADD_FILM, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getRate());
            stmt.setInt(6, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        saveGenres(film);
    }

    @Override
    public void update(Film film) {
        getById(film.getId());

        jdbcTemplate.update(SQL_UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());

        saveGenres(film);
        /*
            без очистки жанров, невозможно пройти тест постмана из-за неверного порядка
         */
        film.deleteAllGenres();
        getGenres(film);
    }

    @Override
    public void saveLikes(Film film) {
        update(film); // update rate
        jdbcTemplate.update(SQL_DELETE_LIKES, film.getId());
        for (Integer like: film.getLikes()) {
            jdbcTemplate.update(SQL_ADD_LIKE, film.getId(), like);
        }
    }

    @Override
    public void saveGenres(Film film) {
        jdbcTemplate.update(SQL_DELETE_GENRES, film.getId());
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(SQL_ADD_GENRE, film.getId(), genre.getId());
        }
    }

    private void getLikes(Film film) {
        SqlRowSet likes = jdbcTemplate.queryForRowSet(SQL_GET_ALL_LIKES, film.getId());
        while (likes.next()) {
            film.addLike(likes.getInt("user_id"));
        }
    }

    private void getGenres(Film film) {
        SqlRowSet genres = jdbcTemplate.queryForRowSet(SQL_GET_ALL_GENRES, film.getId());
        while (genres.next()) {
            List<Genre> genreList = jdbcTemplate.query(SQL_GET_GENRE_BY_ID, this::mapRowToGenre, genres.getInt("genre_id"));
            if (genreList.size() != 1) {
                throw new NotFoundException(String.format("Genre with id = %d not found.", genres.getInt("genre_id")));
            }
            film.addGenre(genreList.get(0));
        }
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getInt("film_id"))
                .name(resultSet.getString("film_name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .rate(resultSet.getInt("rating"))
                .mpa(Mpa.builder()
                        .id(resultSet.getInt("mpa_id"))
                        .name(resultSet.getString("mpa_name"))
                        .description(resultSet.getString("mpa_description"))
                        .build())
                .build();
        getLikes(film);
        getGenres(film);
        return film;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("genre_name"))
                .build();
    }
}