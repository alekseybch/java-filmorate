package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping
    public List<Review> getReviewsForFilm(@RequestParam(required = false) Integer filmId,
                                          @RequestParam(defaultValue = "10") Integer count) {
        log.info("/reviews/ GET - request to receive a reviews for film has been received.");
        List<Review> reviews = reviewService.getReviewsForFilm(filmId, count);
        if (!reviews.isEmpty()) {
            log.info("Number of reviews {}, most useful review id = {}.", reviews.size(), reviews.get(0).getReviewId());
        }
        return reviews;
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable("id") Integer id) {
        log.info("/reviews/{id} GET - request to receive a review has been received id = {}.", id);
        return reviewService.getById(id);
    }

    @PostMapping
    public Review addReview(@RequestBody @Valid Review review) {
        log.info("/reviews POST request received {}.", review);
        reviewService.add(review);
        log.info("Review is created - {}.", review);
        return review;
    }

    @PutMapping
    public Review updateReview(@RequestBody @Valid Review review) {
        log.info("/reviews PUT request received {}.", review);
        reviewService.update(review);
        log.info("Review is updated {}.", review);
        return review;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Integer reviewId,
                        @PathVariable Integer userId) {
        log.info("/reviews/{id}/like/{userId} PUT - request to add " +
                "a like to review id = {} from user id = {}.", reviewId, userId);
        reviewService.addLike(reviewId, userId, true);
        log.info("Review id = {} added like from user id = {}.", reviewId, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable("id") Integer reviewId,
                           @PathVariable Integer userId) {
        log.info("/reviews/{id}/dislike/{userId} PUT - request to add " +
                "a dislike to review id = {} from user id = {}.", reviewId, userId);
        reviewService.addLike(reviewId, userId, false);
        log.info("Review id = {} added dislike from user id = {}.", reviewId, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable("id") Integer reviewId) {
        log.info("/reviews DELETE - request to delete a review received id = {}.", reviewId);
        reviewService.delete(reviewId);
        log.info("Review id = {} deleted", reviewId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Integer reviewId,
                           @PathVariable Integer userId) {
        log.info("/reviews/{id}/like/{userId} DELETE - request to delete " +
                "a like to review id = {} from user id = {}.", reviewId, userId);
        reviewService.deleteLike(reviewId, userId, true);
        log.info("Review id = {} delete like from user id = {}.", reviewId, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable("id") Integer reviewId,
                              @PathVariable Integer userId) {
        log.info("/reviews/{id}/dislike/{userId} DELETE - request to delete " +
                "a dislike to review id = {} from user id = {}.", reviewId, userId);
        reviewService.deleteLike(reviewId, userId, false);
        log.info("Review id = {} delete dislike from user id = {}.", reviewId, userId);
    }
}