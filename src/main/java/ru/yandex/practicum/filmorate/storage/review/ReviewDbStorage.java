package ru.yandex.practicum.filmorate.storage.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class ReviewDbStorage extends BaseStorage<Review> implements ReviewStorage {

    public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper, Review.class);
    }

    private static final String INSERT_QUERY = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM reviews WHERE review_id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM reviews WHERE review_id = ?";
    private static final String FIND_BY_FILM_ID_QUERY = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
    private static final String ADD_LIKE_QUERY = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, true)";
    private static final String ADD_DISLIKE_QUERY = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, false)";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";
    private static final String UPDATE_USEFUL_QUERY = "UPDATE reviews SET useful = useful + ? WHERE review_id = ?";
    private static final String HAS_USER_RATED_QUERY = "SELECT COUNT(*) FROM review_likes WHERE review_id = ? AND user_id = ?";
    private static final String IS_LIKE_QUERY = "SELECT is_like FROM review_likes WHERE review_id = ? AND user_id = ?";
    private static final String GET_REVIEW_LIKES_QUERY = "SELECT user_id FROM review_likes WHERE review_id = ? AND is_like = true";
    private static final String GET_REVIEW_DISLIKES_QUERY = "SELECT user_id FROM review_likes WHERE review_id = ? AND is_like = false";

    @Override
    public Review addReview(Review review) {
        log.debug("Добавление нового отзыва для фильма ID: {}", review.getFilmId());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            ps.setInt(5, review.getUseful() != null ? review.getUseful() : 0);
            return ps;
        }, keyHolder);

        review.setReviewId(keyHolder.getKey().longValue());

        // Загружаем лайки и дизлайки для нового отзыва
        loadLikesAndDislikes(review);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        log.debug("Обновление отзыва ID: {}", review.getReviewId());
        update(UPDATE_QUERY, review.getContent(), review.getIsPositive(), review.getReviewId());

        // Загружаем обновленные лайки и дизлайки
        Review updatedReview = getReviewById(review.getReviewId()).orElse(review);
        loadLikesAndDislikes(updatedReview);
        return updatedReview;
    }

    @Override
    public void deleteReview(long id) {
        log.debug("Удаление отзыва ID: {}", id);
        update(DELETE_QUERY, id);
    }

    @Override
    public Optional<Review> getReviewById(long id) {
        log.debug("Получение отзыва по ID: {}", id);
        Optional<Review> review = findOne(FIND_BY_ID_QUERY, id);
        review.ifPresent(this::loadLikesAndDislikes);
        return review;
    }

    @Override
    public List<Review> getReviewsByFilmId(Long filmId, int count) {
        log.debug("Получение {} отзывов для фильма ID: {}", count, filmId);
        List<Review> reviews = findMany(FIND_BY_FILM_ID_QUERY, filmId, count);
        reviews.forEach(this::loadLikesAndDislikes);
        return reviews;
    }

    @Override
    public List<Review> getAllReviews(int count) {
        log.debug("Получение всех отзывов (limit: {})", count);
        List<Review> reviews = findMany(FIND_ALL_QUERY, count);
        reviews.forEach(this::loadLikesAndDislikes);
        return reviews;
    }

    @Override
    public void addLike(long reviewId, long userId) {
        log.debug("Добавление лайка отзыву ID: {} от пользователя ID: {}", reviewId, userId);
        update(ADD_LIKE_QUERY, reviewId, userId);
        update(UPDATE_USEFUL_QUERY, 1, reviewId);
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        log.debug("Добавление дизлайка отзыву ID: {} от пользователя ID: {}", reviewId, userId);
        update(ADD_DISLIKE_QUERY, reviewId, userId);
        update(UPDATE_USEFUL_QUERY, -1, reviewId);
    }

    @Override
    public void removeLike(long reviewId, long userId) {
        log.debug("Удаление лайка отзыву ID: {} от пользователя ID: {}", reviewId, userId);
        update(REMOVE_LIKE_QUERY, reviewId, userId);
        update(UPDATE_USEFUL_QUERY, -1, reviewId);
    }

    @Override
    public void removeDislike(long reviewId, long userId) {
        log.debug("Удаление дизлайка отзыву ID: {} от пользователя ID: {}", reviewId, userId);
        update(REMOVE_LIKE_QUERY, reviewId, userId);
        update(UPDATE_USEFUL_QUERY, 1, reviewId);
    }

    @Override
    public boolean hasUserRatedReview(long reviewId, long userId) {
        log.debug("Проверка, оценивал ли пользователь ID: {} отзыв ID: {}", userId, reviewId);
        Long count = jdbc.queryForObject(HAS_USER_RATED_QUERY, Long.class, reviewId, userId);
        return count != null && count > 0;
    }

    @Override
    public boolean isLike(long reviewId, long userId) {
        log.debug("Проверка типа оценки отзыва ID: {} пользователем ID: {}", reviewId, userId);
        try {
            Boolean isLike = jdbc.queryForObject(IS_LIKE_QUERY, Boolean.class, reviewId, userId);
            return isLike != null && isLike;
        } catch (Exception e) {
            log.warn("Ошибка при проверке типа оценки: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Загружает лайки и дизлайки для отзыва
     */
    private void loadLikesAndDislikes(Review review) {
        if (review == null) return;

        // Загружаем лайки
        List<Long> likes = jdbc.query(GET_REVIEW_LIKES_QUERY,
                (rs, rowNum) -> rs.getLong("user_id"), review.getReviewId());
        review.getLikes().addAll(likes);

        // Загружаем дизлайки
        List<Long> dislikes = jdbc.query(GET_REVIEW_DISLIKES_QUERY,
                (rs, rowNum) -> rs.getLong("user_id"), review.getReviewId());
        review.getDislikes().addAll(dislikes);
    }
}