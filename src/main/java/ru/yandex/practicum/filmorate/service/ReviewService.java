package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;

@Service
public class ReviewService extends AbstractService<Review> {
    private final ReviewStorage reviewStorage;

    public ReviewService(@Qualifier("reviewDbStorage") ReviewStorage reviewStorage) {
        super(reviewStorage);
        this.reviewStorage = reviewStorage;
    }

    public List<Review> getReviewsForFilm(Integer filmId, Integer count) {
        if (count <= 0) {
            throw new IllegalArgumentException(String.format("Must be positive count = %d", count));
        }
        if (filmId == null) {
            return reviewStorage.readAllWithLimit(count);
        } else {
            return reviewStorage.readReviewsForFilm(filmId, count);
        }
    }

    public void addLike(Integer reviewId, Integer userId, boolean isLike) {
        if (isLike) {
            reviewStorage.createLike(reviewId, userId);
        } else {
            reviewStorage.createDislike(reviewId, userId);
        }
    }

    public void deleteLike(Integer reviewId, Integer userId, boolean isLike) {
        if (isLike) {
            reviewStorage.deleteLike(reviewId, userId);
        } else {
            reviewStorage.deleteDislike(reviewId, userId);
        }
    }
}
