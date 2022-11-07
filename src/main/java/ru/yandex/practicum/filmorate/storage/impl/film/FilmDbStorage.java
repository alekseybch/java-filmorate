package ru.yandex.practicum.filmorate.storage.impl.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.DataStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
    private static final String SQL_DELETE_FILM = "DELETE FROM films WHERE film_id = ?";
    private static final String SQL_GET_FILM_BY_ID = "SELECT f.*, m.* FROM films AS f " +
            "LEFT JOIN mpa_ratings AS m ON f.mpa_id = m.mpa_id WHERE f.film_id = ?";
    private static final String SQL_GET_ALL_FILMS = "SELECT f.*, m.* FROM films AS f " +
            "LEFT JOIN mpa_ratings AS m ON f.mpa_id = m.mpa_id";
    private static final String SQL_GET_TOP_FILMS = "SELECT f.*, m.* FROM films AS f LEFT JOIN mpa_ratings AS m " +
            "ON f.mpa_id = m.mpa_id LEFT JOIN movies_likes AS ml ON f.film_id = ml.film_id " +
            "GROUP BY f.film_id ORDER BY COUNT(ml.user_id) DESC LIMIT ?";
    private static final String SQL_GET_SORTED_FILMS_BY_YEAR = "SELECT f.*, m.* FROM films AS f " +
            "LEFT JOIN mpa_ratings AS m ON f.mpa_id = m.mpa_id " +
            "LEFT JOIN movies_directors AS md ON f.film_id = md.film_id " +
            "LEFT JOIN directors AS d ON md.director_id = d.director_id WHERE d.director_id = ? " +
            "ORDER BY EXTRACT(YEAR FROM CAST(release_date AS date))";
    private static final String SQL_GET_SORTED_FILMS_BY_LIKES = "SELECT f.*, m.* FROM films AS f " +
            "LEFT JOIN mpa_ratings AS m ON f.mpa_id = m.mpa_id " +
            "LEFT JOIN movies_directors AS md ON f.film_id = md.film_id " +
            "LEFT JOIN directors AS d ON md.director_id = d.director_id " +
            "LEFT JOIN movies_likes AS ml ON f.film_id = ml.film_id " +
            "WHERE d.director_id = ? GROUP BY f.film_id ORDER BY COUNT(ml.user_id) DESC";
    private static final String SQL_ADD_LIKE = "INSERT INTO movies_likes (film_id, user_id) VALUES (?, ?)";
    private static final String SQL_GET_ALL_LIKES = "SELECT user_id FROM movies_likes WHERE film_id = ?";
    private static final String SQL_DELETE_LIKE = "DELETE FROM movies_likes WHERE film_id = ? AND user_id = ?";
    private static final String SQL_ADD_GENRE = "INSERT INTO movies_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String SQL_ADD_DIRECTOR = "INSERT INTO movies_directors (film_id, director_id) VALUES (?, ?)";
    private static final String SQL_GET_FILM_GENRES = "SELECT mg.genre_id, g.genre_name FROM movies_genres AS mg " +
            "LEFT JOIN genres AS g ON mg.genre_id = g.genre_id WHERE mg.film_id = ?";
    private static final String SQL_GET_FILM_DIRECTORS = "SELECT md.director_id, d.director_name FROM movies_directors AS md " +
            "LEFT JOIN directors AS d ON md.director_id = d.director_id WHERE md.film_id = ?";
    private static final String SQL_DELETE_GENRES = "DELETE FROM movies_genres WHERE film_id = ?";
    private static final String SQL_DELETE_DIRECTORS = "DELETE FROM movies_directors WHERE film_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final DataStorage<Director> directorStorage;

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
        saveDirectors(film);
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
        saveDirectors(film);
    }

    @Override
    public void delete(int id) {
        getById(id);
        jdbcTemplate.update(SQL_DELETE_FILM, id);
    }

    @Override
    public void addLike(int filmId, int userId) {
        jdbcTemplate.update(SQL_ADD_LIKE, filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        jdbcTemplate.update(SQL_DELETE_LIKE, filmId, userId);
    }

    @Override
    public Collection<Film> getTopFilms(int count) {
        return jdbcTemplate.query(SQL_GET_TOP_FILMS, this::mapRowToFilm, count);
    }

    @Override
    public Collection<Film> getSortedDirectorFilms(int directorId, String sortBy) {
        directorStorage.getById(directorId);
        Collection<Film> films = null;
        switch (sortBy) {
            case "year":
                films = jdbcTemplate.query(SQL_GET_SORTED_FILMS_BY_YEAR, this::mapRowToFilm, directorId);
                break;
            case "likes":
                films = jdbcTemplate.query(SQL_GET_SORTED_FILMS_BY_LIKES, this::mapRowToFilm, directorId);
        }
        return films;
    }

    private void saveGenres(Film film) {
        jdbcTemplate.update(SQL_DELETE_GENRES, film.getId());
        List<Genre> genres = new ArrayList<>(film.getGenres());
        jdbcTemplate.batchUpdate(SQL_ADD_GENRE, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i)
                    throws SQLException {
                ps.setInt(1, film.getId());
                ps.setInt(2, genres.get(i).getId());
            }
            public int getBatchSize() {
                return genres.size();
            }
        });
    }

    private void saveDirectors(Film film) {
        jdbcTemplate.update(SQL_DELETE_DIRECTORS, film.getId());
        List<Director> directors = new ArrayList<>(film.getDirectors());
        jdbcTemplate.batchUpdate(SQL_ADD_DIRECTOR, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i)
                    throws SQLException {
                ps.setInt(1, film.getId());
                ps.setInt(2, directors.get(i).getId());
            }
            public int getBatchSize() {
                return directors.size();
            }
        });
    }

    private void getLikes(Film film) {
        SqlRowSet likes = jdbcTemplate.queryForRowSet(SQL_GET_ALL_LIKES, film.getId());
        while (likes.next()) {
            film.addLike(likes.getInt("user_id"));
        }
    }

    private void getGenres(Film film) {
        List<Genre> genres = jdbcTemplate.query(SQL_GET_FILM_GENRES, this::mapRowToGenre, film.getId());
        for (Genre genre : genres) {
            film.addGenre(genre);
        }
    }

    private void getDirectors(Film film) {
        List<Director> directors = jdbcTemplate.query(SQL_GET_FILM_DIRECTORS, this::mapRowToDirector, film.getId());
        for (Director director : directors) {
            film.addDirector(director);
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
        getDirectors(film);
        return film;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("genre_name"))
                .build();
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getInt("director_id"))
                .name(resultSet.getString("director_name"))
                .build();
    }
}