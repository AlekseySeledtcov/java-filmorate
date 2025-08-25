package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundFilmException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundReactionException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundReviewException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundUserByIdException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public ReviewService(ReviewStorage reviewStorage,
                         @Qualifier("UserDbStorage") UserStorage userStorage,
                         @Qualifier("FilmDbStorage") FilmStorage filmStorage) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    /**
     * Добавление нового отзыва
     */
    public Review addReview(Review review) {
        log.debug("Добавление отзыва: {}", review);

        if (review.getContent() == null || review.getContent().trim().isEmpty()) {
            throw new ValidationException("Содержание отзыва не может быть пустым");
        }
        if (review.getIsPositive() == null) {
            throw new ValidationException("Тип отзыва должен быть указан");
        }
        if (review.getUserId() == null) {
            throw new ValidationException("ID пользователя должен быть указан");
        }
        if (review.getFilmId() == null) {
            throw new ValidationException("ID фильма должен быть указан");
        }

        validateUserAndFilm(review.getUserId(), review.getFilmId());

        if (review.getUseful() == null) {
            review.setUseful(0);
        }

        return reviewStorage.addReview(review);
    }

    /**
     * Обновление существующего отзыва
     */
    public Review updateReview(Review review) {
        log.debug("Обновление отзыва: {}", review);

        if (review.getReviewId() == null) {
            throw new ValidationException("ID отзыва должен быть указан");
        }

        Review existingReview = getReviewById(review.getReviewId());

        if (!existingReview.getUserId().equals(review.getUserId())) {
            throw new ValidationException("Пользователь может редактировать только свои отзывы");
        }

        if (review.getContent() == null || review.getContent().trim().isEmpty()) {
            throw new ValidationException("Содержание отзыва не может быть пустым");
        }
        if (review.getIsPositive() == null) {
            throw new ValidationException("Тип отзыва должен быть указан");
        }

        return reviewStorage.updateReview(review);
    }

    /**
     * Удаление отзыва по ID
     */
    public void deleteReview(long id) {
        if (!reviewStorage.getReviewById(id).isPresent()) {
            throw new NotFoundReviewException("Отзыв с ID " + id + " не найден", id);
        }
        reviewStorage.deleteReview(id);
    }

    /**
     * Получение отзыва по ID
     */
    public Review getReviewById(long id) {
        return reviewStorage.getReviewById(id)
                .orElseThrow(() -> new NotFoundReviewException("Отзыв с ID " + id + " не найден", id));
    }

    /**
     * Получение отзывов по фильму с ограничением количества
     */
    public List<Review> getReviews(Long filmId, int count) {
        if (count <= 0) {
            count = 10; // значение по умолчанию
        }

        if (filmId == null) {
            return reviewStorage.getAllReviews(count);
        } else {
            if (!filmStorage.containsFilmById(filmId)) {
                throw new NotFoundFilmException("Фильм с ID " + filmId + " не найден", filmId);
            }
            return reviewStorage.getReviewsByFilmId(filmId, count);
        }
    }

    /**
     * Добавление лайка отзыву
     */
    public void addLike(long reviewId, long userId) {
        validateReviewAndUser(reviewId, userId);

        reviewStorage.addLike(reviewId, userId);
    }

    /**
     * Добавление дизлайка отзыву
     */
    public void addDislike(long reviewId, long userId) {
        validateReviewAndUser(reviewId, userId);

        reviewStorage.addDislike(reviewId, userId);
    }

    /**
     * Удаление лайка/дизлайка
     */
    public void removeReaction(long reviewId, long userId) {
        validateReviewAndUser(reviewId, userId);

        if (!reviewStorage.hasUserRatedReview(reviewId, userId)) {
            throw new NotFoundReactionException("Оценка отзыва не найдена", reviewId, userId);
        }

        if (reviewStorage.isLike(reviewId, userId)) {
            reviewStorage.removeLike(reviewId, userId);
        } else {
            reviewStorage.removeDislike(reviewId, userId);
        }
    }

    /**
     * Валидация пользователя и фильма
     */
    private void validateUserAndFilm(Long userId, Long filmId) {
        if (userId == null) {
            throw new ValidationException("ID пользователя не может быть null");
        }
        if (filmId == null) {
            throw new ValidationException("ID фильма не может быть null");
        }

        if (!userStorage.containsUserById(userId)) {
            throw new NotFoundUserByIdException("Пользователь с ID " + userId + " не найден", userId);
        }
        if (!filmStorage.containsFilmById(filmId)) {
            throw new NotFoundFilmException("Фильм с ID " + filmId + " не найден", filmId);
        }
    }

    /**
     * Валидация отзыва и пользователя
     */
    private void validateReviewAndUser(long reviewId, long userId) {
        if (!reviewStorage.getReviewById(reviewId).isPresent()) {
            throw new NotFoundReviewException("Отзыв с ID " + reviewId + " не найден", reviewId);
        }
        if (!userStorage.containsUserById(userId)) {
            throw new NotFoundUserByIdException("Пользователь с ID " + userId + " не найден", userId);
        }
    }
}