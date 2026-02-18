package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Review addReview(Review review);

    Review updateReview(Review review);

    void deleteReview(long id);

    Optional<Review> getReviewById(long id);

    List<Review> getReviewsByFilmId(Long filmId, int count);

    List<Review> getAllReviews(int count);

    void addLike(long reviewId, long userId);

    void addDislike(long reviewId, long userId);

    void removeLike(long reviewId, long userId);

    void removeDislike(long reviewId, long userId);

    boolean hasUserRatedReview(long reviewId, long userId);

    boolean isLike(long reviewId, long userId);

    void deleteReviewsByUserId(long userId);

    void deleteReviewsByFilmId(long filmId);

    void deleteReviewRatingsByUserId(long userId);
}