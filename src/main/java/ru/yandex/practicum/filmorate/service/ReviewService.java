package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
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
    private final EventService eventService;

    public ReviewService(ReviewStorage reviewStorage,
                         UserStorage userStorage,
                         FilmStorage filmStorage,
                         EventService eventService) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.eventService = eventService;
    }

    /**
     * Добавление нового отзыва
     */
    public Review addReview(Review review) {
        log.debug("Добавление отзыва: {}", review);

        validateUserAndFilm(review);

        if (review.getUseful() == null) {
            review.setUseful(0);
        }

        Review createdReview = reviewStorage.addReview(review);

        // Добавляем событие после сохранения
        Event event = Event.builder()
                .userId(createdReview.getUserId())
                .entityId(createdReview.getReviewId())
                .eventType(EventType.REVIEW)
                .operation(Operation.ADD)
                .build();

        log.debug("Добавление события в ленту: {}", event);
        eventService.addEvent(event);

        return createdReview;  // тут возвращаем результат reviewStorage.addReview(review)
    }


    /**
     * Обновление существующего отзыва
     */
    public Review updateReview(Review review) {
        log.debug("Обновление отзыва: {}", review);

        if (review.getReviewId() == null) {
            throw new ValidationException("ID отзыва должен быть указан");
        }
        if (review.getContent() == null || review.getContent().trim().isEmpty()) {
            throw new ValidationException("Содержание отзыва не может быть пустым");
        }
        if (review.getIsPositive() == null) {
            throw new ValidationException("Тип отзыва должен быть указан");
        }
        Review updatedReview = reviewStorage.updateReview(review);
        // Создаем и добавляем событие
        Event event = Event.builder()
                .userId(updatedReview.getUserId())
                .entityId(updatedReview.getReviewId())
                .eventType(EventType.REVIEW)
                .operation(Operation.UPDATE)
                .build();

        log.debug("Добавление события в ленту после обновления отзыва: {}", event);
        eventService.addEvent(event);

        return updatedReview;
    }

    /**
     * Удаление отзыва по ID
     */
    public void deleteReview(long id) {
        Review review = getReviewById(id);
        reviewStorage.getReviewById(id).orElseThrow(() -> {
            throw new EntityNotFoundException("Отзыв с ID " + id + " не найден", id);
        });

        reviewStorage.deleteReview(id);
        Event event = Event.builder()
                .userId(review.getUserId())
                .entityId(review.getReviewId())
                .eventType(EventType.REVIEW)
                .operation(Operation.REMOVE)
                .build();

        log.info("Создаём событие удаления отзыва с operation = {}", event.getOperation());
        eventService.addEvent(event);
    }


    /**
     * Получение отзыва по ID
     */
    public Review getReviewById(long id) {
        return reviewStorage.getReviewById(id)
                .orElseThrow(() -> new EntityNotFoundException("Отзыв с ID " + id + " не найден", id));
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
                throw new EntityNotFoundException("Фильм с ID " + filmId + " не найден", filmId);
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
            throw new EntityNotFoundException("Оценка отзыва не найдена", reviewId);
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
    private void validateUserAndFilm(Review review) {

        if (review.getUserId() == null) {
            throw new ValidationException("ID пользователя не может быть null");
        }
        if (review.getFilmId() == null) {
            throw new ValidationException("ID фильма не может быть null");
        }

        if (!userStorage.containsUserById(review.getUserId())) {
            throw new EntityNotFoundException("Пользователь с ID " + review.getUserId() + " не найден", review.getUserId());
        }
        if (!filmStorage.containsFilmById(review.getFilmId())) {
            throw new EntityNotFoundException("Фильм с ID " + review.getFilmId() + " не найден", review.getFilmId());
        }

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
    }

    /**
     * Валидация отзыва и пользователя
     */
    private void validateReviewAndUser(long reviewId, long userId) {
        reviewStorage.getReviewById(reviewId).orElseThrow(() -> {
            throw new EntityNotFoundException("Отзыв с ID " + reviewId + " не найден", reviewId);
        });
        if (!userStorage.containsUserById(userId)) {
            throw new EntityNotFoundException("Пользователь с ID " + userId + " не найден", userId);
        }
    }
}