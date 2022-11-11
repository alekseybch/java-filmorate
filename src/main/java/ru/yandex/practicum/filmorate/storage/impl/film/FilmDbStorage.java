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
    private static final String SQL_CREATE_FILM = "insert into FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, " +
            "RATING, MPA_ID) values (?, ?, ?, ?, ?, ?)";
    private static final String SQL_CREATE_LIKE = "insert into MOVIES_LIKES (FILM_ID, USER_ID) values (?, ?)";
    private static final String SQL_CREATE_GENRE = "insert into MOVIES_GENRES (FILM_ID, GENRE_ID) values (?, ?)";
    private static final String SQL_CREATE_DIRECTOR = "insert into MOVIES_DIRECTORS (FILM_ID, DIRECTOR_ID) values (?, ?)";
    private static final String SQL_UPDATE_FILM = "update FILMS set FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, " +
            "DURATION = ?, RATING = ?, MPA_ID = ? where FILM_ID = ?";
    private static final String SQL_DELETE_FILM = "delete from FILMS where FILM_ID = ?";
    private static final String SQL_DELETE_LIKE = "delete from MOVIES_LIKES where FILM_ID = ? and USER_ID = ?";
    private static final String SQL_DELETE_GENRES = "delete from MOVIES_GENRES where FILM_ID = ?";
    private static final String SQL_DELETE_DIRECTORS = "delete from MOVIES_DIRECTORS where FILM_ID = ?";
    private static final String SQL_READ_FILM_BY_ID = "select f.*, m.* from FILMS f " +
            "left join MPA_RATINGS m on f.MPA_ID = m.MPA_ID where f.FILM_ID = ?";
    private static final String SQL_READ_ALL_FILMS = "select f.*, m.* from FILMS f " +
            "left join mpa_ratings m on f.MPA_ID = m.MPA_ID";
    private static final String SQL_READ_TOP_FILMS = "select f.*, m.* from FILMS f " +
            "left join MPA_RATINGS m on f.MPA_ID = m.MPA_ID " +
            "left join MOVIES_LIKES ml on f.FILM_ID = ml.FILM_ID " +
            "group by f.FILM_ID order by COUNT(ml.USER_ID) desc limit ?";
    private static final String SQL_READ_FILMS_SORTED_BY_YEAR = "select f.*, m.* from FILMS f " +
            "left join MPA_RATINGS m on f.MPA_ID = m.MPA_ID " +
            "left join MOVIES_DIRECTORS md on f.FILM_ID = md.FILM_ID " +
            "left join DIRECTORS d on md.DIRECTOR_ID = d.DIRECTOR_ID where d.DIRECTOR_ID = ? " +
            "order by EXTRACT(year from CAST(RELEASE_DATE as date))";
    private static final String SQL_READ_FILMS_SORTED_BY_LIKES = "SELECT f.*, m.* FROM films AS f " +
            "left join MPA_RATINGS m on f.MPA_ID = m.MPA_ID " +
            "left join MOVIES_DIRECTORS md on f.FILM_ID = md.FILM_ID " +
            "left join DIRECTORS d on md.DIRECTOR_ID = d.DIRECTOR_ID " +
            "left join MOVIES_LIKES ml on f.FILM_ID = ml.FILM_ID " +
            "where d.director_id = ? " +
            "group by f.FILM_ID " +
            "order by COUNT(ml.USER_ID) desc ";
    private static final String SQL_READ_ALL_LIKES = "select USER_ID from MOVIES_LIKES where FILM_ID = ?";
    private static final String SQL_READ_FILM_GENRES = "select mg.GENRE_ID, g.GENRE_NAME from MOVIES_GENRES mg " +
            "left join GENRES g on mg.GENRE_ID = g.GENRE_ID where mg.FILM_ID = ?";
    private static final String SQL_READ_FILM_DIRECTORS = "select md.DIRECTOR_ID, d.DIRECTOR_NAME " +
            "from MOVIES_DIRECTORS md " +
            "left join DIRECTORS d on md.DIRECTOR_ID = d.DIRECTOR_ID where md.FILM_ID = ?";

    private final JdbcTemplate jdbcTemplate;
    private final DataStorage<Director> directorStorage;

    @Override
    public Collection<Film> readAll() {
        return jdbcTemplate.query(SQL_READ_ALL_FILMS, this::mapRowToFilm);
    }

    @Override
    public Film readById(Integer id) {
        List<Film> films = jdbcTemplate.query(SQL_READ_FILM_BY_ID, this::mapRowToFilm, id);
        if (films.size() != 1) {
            throw new NotFoundException(String.format("Film with id = %d not found.", id));
        }
        return films.get(0);
    }

    @Override
    public void create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(SQL_CREATE_FILM, new String[]{"film_id"});
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
        readById(film.getId());

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
    public void delete(Integer id) {
        readById(id);
        jdbcTemplate.update(SQL_DELETE_FILM, id);
    }

    @Override
    public void createLike(Integer filmId, Integer userId) {
        jdbcTemplate.update(SQL_CREATE_LIKE, filmId, userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        jdbcTemplate.update(SQL_DELETE_LIKE, filmId, userId);
    }

    @Override
    public Collection<Film> readTopFilms(Integer count) {
        return jdbcTemplate.query(SQL_READ_TOP_FILMS, this::mapRowToFilm, count);
    }

    @Override
    public Collection<Film> readDirectorFilmsSortedByYear(Integer directorId, String sortBy) {
        directorStorage.readById(directorId);
        return jdbcTemplate.query(SQL_READ_FILMS_SORTED_BY_YEAR, this::mapRowToFilm, directorId);
    }

    @Override
    public Collection<Film> readDirectorFilmsSortedByLikes(Integer directorId, String sortBy) {
        directorStorage.readById(directorId);
        return jdbcTemplate.query(SQL_READ_FILMS_SORTED_BY_LIKES, this::mapRowToFilm, directorId);
    }

    private void saveGenres(Film film) {
        jdbcTemplate.update(SQL_DELETE_GENRES, film.getId());
        List<Genre> genres = new ArrayList<>(film.getGenres());
        jdbcTemplate.batchUpdate(SQL_CREATE_GENRE, new BatchPreparedStatementSetter() {
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
        jdbcTemplate.batchUpdate(SQL_CREATE_DIRECTOR, new BatchPreparedStatementSetter() {
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
        SqlRowSet likes = jdbcTemplate.queryForRowSet(SQL_READ_ALL_LIKES, film.getId());
        while (likes.next()) {
            film.addLike(likes.getInt("user_id"));
        }
    }

    private void getGenres(Film film) {
        List<Genre> genres = jdbcTemplate.query(SQL_READ_FILM_GENRES, this::mapRowToGenre, film.getId());
        for (Genre genre : genres) {
            film.addGenre(genre);
        }
    }

    private void getDirectors(Film film) {
        List<Director> directors = jdbcTemplate.query(SQL_READ_FILM_DIRECTORS, this::mapRowToDirector, film.getId());
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