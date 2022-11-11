package ru.yandex.practicum.filmorate.storage.impl.review;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Repository("reviewDbStorage")
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private static final String SQL_CREATE_REVIEW = "insert into REVIEWS (CONTENT, IS_POSITIVE, USER_ID, FILM_ID) " +
            "values (?, ?, ?, ?)";
    private static final String SQL_CREATE_REVIEWS_LIKE = "insert into REVIEWS_LIKES (REVIEW_ID, USER_ID, IS_LIKE) " +
            "values (?, ?, ?)";
    private static final String SQL_UPDATE_REVIEW = "update REVIEWS set CONTENT = ?, IS_POSITIVE = ? where REVIEW_ID = ?";
    private static final String SQL_UPDATE_REVIEWS_LIKE = "update REVIEWS_LIKES set IS_LIKE = ? where REVIEW_ID = ? and USER_ID = ?";
    private static final String SQL_DELETE_REVIEW = "delete from REVIEWS where REVIEW_ID = ?";
    private static final String SQL_DELETE_REVIEWS_LIKE = "delete from REVIEWS_LIKES where REVIEW_ID = ? and USER_ID = ?";
    private static final String SQL_DELETE_REVIEWS_DISLIKE = "delete from REVIEWS_LIKES where REVIEW_ID = ? and USER_ID = ?";
    private static final String SQL_READ_REVIEW_BY_ID = "select *, " +
            "(SUM(case when rl.IS_LIKE = true then 1 else 0 end ) - " +
            "SUM(case when RL.IS_LIKE = false then 1 else 0 end )) USEFUL " +
            "from REVIEWS r " +
            "left join REVIEWS_LIKES rl on r.REVIEW_ID = rl.REVIEW_ID " +
            "where r.REVIEW_ID = ? " +
            "group by r.REVIEW_ID";
    private static final String SQL_READ_REVIEWS_FOR_FILMS = "select r.REVIEW_ID, r.CONTENT, r.IS_POSITIVE, r.USER_ID, r.FILM_ID, " +
            "(SUM(case when rl.IS_LIKE = true then 1 else 0 end) - " +
            "SUM(case when rl.IS_LIKE = false then 1 else 0 end)) USEFUL " +
            "from REVIEWS r " +
            "left join REVIEWS_LIKES rl on r.REVIEW_ID = rl.REVIEW_ID " +
            "where r.FILM_ID = ? " +
            "group by r.REVIEW_ID " +
            "order by USEFUL desc " +
            "limit ?";
    private static final String SQL_READ_ALL_REVIEWS_WITH_LIMIT = "select r.REVIEW_ID, r.CONTENT, r.IS_POSITIVE, r.USER_ID, r.FILM_ID, " +
            "(SUM(case when rl.IS_LIKE = true then 1 else 0 end) - " +
            "SUM(case when rl.IS_LIKE = false then 1 else 0 end)) USEFUL " +
            "from REVIEWS r " +
            "left join REVIEWS_LIKES rl on r.REVIEW_ID = rl.REVIEW_ID " +
            "group by R.REVIEW_ID " +
            "order by USEFUL desc " +
            "limit ?";

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Review> readAll() {
        return null;
    }

    @Override
    public Review readById(Integer id) {
        List<Review> reviews = jdbcTemplate.query(SQL_READ_REVIEW_BY_ID, this::mapRowToReview, id);
        if (reviews.size() != 1) {
            throw new NotFoundException(String.format("Review with id = %d not found.", id));
        }
        return reviews.get(0);
    }

    @Override
    public void create(Review review) {
        userStorage.readById(review.getUserId());
        filmStorage.readById(review.getFilmId());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(SQL_CREATE_REVIEW, new String[]{"review_id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setInt(3, review.getUserId());
            stmt.setInt(4, review.getFilmId());
            return stmt;
        }, keyHolder);

        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).intValue());
    }

    @Override
    public void update(Review review) {
        readById(review.getReviewId());

        jdbcTemplate.update(SQL_UPDATE_REVIEW,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
    }

    @Override
    public void delete(Integer id) {
        readById(id);

        jdbcTemplate.update(SQL_DELETE_REVIEW, id);
    }

    @Override
    public List<Review> readReviewsForFilm(Integer filmId, Integer count) {
        filmStorage.readById(filmId);

        return jdbcTemplate.query(SQL_READ_REVIEWS_FOR_FILMS, this::mapRowToReview, filmId, count);
    }

    @Override
    public List<Review> readAllWithLimit(Integer count) {
        return jdbcTemplate.query(SQL_READ_ALL_REVIEWS_WITH_LIMIT, this::mapRowToReview, count);
    }

    @Override
    public void createLike(Integer reviewId, Integer userId) {
        userStorage.readById(userId);
        Review review = readById(reviewId);

        if (review != null) {
            jdbcTemplate.update(SQL_CREATE_REVIEWS_LIKE, reviewId, userId, true);
        } else {
            jdbcTemplate.update(SQL_UPDATE_REVIEWS_LIKE, reviewId, userId, true);
        }
    }

    @Override
    public void createDislike(Integer reviewId, Integer userId) {
        userStorage.readById(userId);
        Review review = readById(reviewId);

        if (review != null) {
            jdbcTemplate.update(SQL_CREATE_REVIEWS_LIKE, reviewId, userId, false);
        } else {
            jdbcTemplate.update(SQL_UPDATE_REVIEWS_LIKE, reviewId, userId, false);
        }
    }

    @Override
    public void deleteLike(Integer reviewId, Integer userId) {
        userStorage.readById(userId);
        Review review = readById(reviewId);
        if (!review.getIsPositive()) {
            throw new NotFoundException(
                    String.format("Like to review id = %d from user id = %d not found.", reviewId, userId));
        }

        jdbcTemplate.update(SQL_DELETE_REVIEWS_LIKE, reviewId, userId);
    }

    @Override
    public void deleteDislike(Integer reviewId, Integer userId) {
        userStorage.readById(userId);
        Review review = readById(reviewId);
        if (review.getIsPositive()) {
            throw new NotFoundException(
                    String.format("Dislike to review id = %d from user id = %d not found.", reviewId, userId));
        }

        jdbcTemplate.update(SQL_DELETE_REVIEWS_DISLIKE, reviewId, userId);
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getInt("review_id"))
                .content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .userId(resultSet.getInt("user_id"))
                .filmId(resultSet.getInt("film_id"))
                .useful(resultSet.getInt("useful"))
                .build();
    }
}