package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage extends DataStorage<Review> {
    List<Review> readReviewsForFilm(Integer filmId, Integer count);

    List<Review> readAllWithLimit(Integer count);

    void createLike(Integer reviewId, Integer userId);

    void createDislike(Integer reviewId, Integer userId);

    void deleteLike(Integer reviewId, Integer userId);

    void deleteDislike(Integer reviewId, Integer userId);
}