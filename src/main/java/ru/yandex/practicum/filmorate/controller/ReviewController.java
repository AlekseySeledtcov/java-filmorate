package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    /**
     * Добавление нового отзыва
     */
    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        log.debug("POST /reviews - добавление нового отзыва");
        return reviewService.addReviewWithEvent(review);
    }

    /**
     * Обновление существующего отзыва
     */
    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        log.debug("PUT /reviews - обновление отзыва ID: {}", review.getReviewId());
        return reviewService.updateReviewWithEvent(review);
    }

    /**
     * Удаление отзыва по ID
     */
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable long id) {
        log.debug("DELETE /reviews/{} - удаление отзыва", id);
        reviewService.deleteReviewWithEvent(id);
    }

    /**
     * Получение отзыва по ID
     */
    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable long id) {
        log.debug("GET /reviews/{} - получение отзыва по ID", id);
        return reviewService.getReviewById(id);
    }

    /**
     * Получение отзывов по фильму
     */
    @GetMapping
    public List<Review> getReviews(
            @RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10") int count) {
        log.debug("GET /reviews?filmId={}&count={} - получение отзывов", filmId, count);
        return reviewService.getReviews(filmId, count);
    }

    /**
     * Добавление лайка отзыву
     */
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        log.debug("PUT /reviews/{}/like/{} - добавление лайка", id, userId);
        reviewService.addLike(id, userId);
    }

    /**
     * Добавление дизлайка отзыву
     */
    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable long id, @PathVariable long userId) {
        log.debug("PUT /reviews/{}/dislike/{} - добавление дизлайка", id, userId);
        reviewService.addDislike(id, userId);
    }

    /**
     * Удаление лайка
     */
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        log.debug("DELETE /reviews/{}/like/{} - удаление лайка", id, userId);
        reviewService.removeReaction(id, userId);
    }

    /**
     * Удаление дизлайка
     */
    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable long id, @PathVariable long userId) {
        log.debug("DELETE /reviews/{}/dislike/{} - удаление дизлайка", id, userId);
        reviewService.removeReaction(id, userId);
    }
}