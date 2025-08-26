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
    private static final String SET_USEFUL_QUERY = "UPDATE reviews SET useful = ? WHERE review_id = ?";
    private static final String HAS_USER_RATED_QUERY = "SELECT COUNT(*) FROM review_likes WHERE review_id = ? AND user_id = ?";
    private static final String IS_LIKE_QUERY = "SELECT is_like FROM review_likes WHERE review_id = ? AND user_id = ?";
    private static final String GET_LIKES_COUNT_QUERY = "SELECT COUNT(*) FROM review_likes WHERE review_id = ? AND is_like = true";
    private static final String GET_DISLIKES_COUNT_QUERY = "SELECT COUNT(*) FROM review_likes WHERE review_id = ? AND is_like = false";
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
        jdbc.update(UPDATE_QUERY, review.getContent(), review.getIsPositive(), review.getReviewId());
        return review;
    }

    @Override
    public void deleteReview(long id) {
        log.debug("Удаление отзыва ID: {}", id);
        jdbc.update(DELETE_QUERY, id);
    }

    @Override
    public Optional<Review> getReviewById(long id) {
        log.debug("Получение отзыва по ID: {}", id);
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public List<Review> getReviewsByFilmId(Long filmId, int count) {
        log.debug("Получение {} отзывов для фильма ID: {}", count, filmId);
        return findMany(FIND_BY_FILM_ID_QUERY, filmId, count);
    }

    @Override
    public List<Review> getAllReviews(int count) {
        log.debug("Получение всех отзывов (limit: {})", count);
        return findMany(FIND_ALL_QUERY, count);
    }

    @Override
    public void addLike(long reviewId, long userId) {
        log.debug("Добавление лайка отзыву ID: {} от пользователя ID: {}", reviewId, userId);

        if (hasUserDisliked(reviewId, userId)) {
            // Меняем дизлайк на лайк (удаляем дизлайк, добавляем лайк)
            removeUserRating(reviewId, userId);
            jdbc.update(ADD_LIKE_QUERY, reviewId, userId);
            log.debug("Изменен дизлайк на лайк пользователя ID: {}", userId);
        } else if (!hasUserLiked(reviewId, userId)) {
            // Добавляем лайк (если его еще нет)
            jdbc.update(ADD_LIKE_QUERY, reviewId, userId);
            log.debug("Добавлен лайк от пользователя ID: {}", userId);
        } else {
            // Лайк уже стоит - ничего не делаем
            log.debug("Пользователь ID: {} уже поставил лайк", userId);
            return;
        }

        updateUsefulField(reviewId);
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        log.debug("Добавление дизлайка отзыву ID: {} от пользователя ID: {}", reviewId, userId);

        if (hasUserLiked(reviewId, userId)) {
            // Меняем лайк на дизлайк (удаляем лайк, добавляем дизлайк)
            removeUserRating(reviewId, userId);
            jdbc.update(ADD_DISLIKE_QUERY, reviewId, userId);
            log.debug("Изменен лайк на дизлайк пользователя ID: {}", userId);
        } else if (!hasUserDisliked(reviewId, userId)) {
            // Добавляем дизлайк (если его еще нет)
            jdbc.update(ADD_DISLIKE_QUERY, reviewId, userId);
            log.debug("Добавлен дизлайк от пользователя ID: {}", userId);
        } else {
            // Дизлайк уже стоит - ничего не делаем
            log.debug("Пользователь ID: {} уже поставил дизлайк", userId);
            return;
        }

        updateUsefulField(reviewId);
    }

    @Override
    public void removeLike(long reviewId, long userId) {
        log.debug("Удаление лайка отзыву ID: {} от пользователя ID: {}", reviewId, userId);
        removeUserRating(reviewId, userId);
        updateUsefulField(reviewId);
    }

    @Override
    public void removeDislike(long reviewId, long userId) {
        log.debug("Удаление дизлайка отзыву ID: {} от пользователя ID: {}", reviewId, userId);
        removeUserRating(reviewId, userId);
        updateUsefulField(reviewId);
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

    // Вспомогательные методы для реализации переключения оценок
    private boolean hasUserLiked(long reviewId, long userId) {
        String sql = "SELECT COUNT(*) FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = true";
        Integer count = jdbc.queryForObject(sql, Integer.class, reviewId, userId);
        return count != null && count > 0;
    }

    private boolean hasUserDisliked(long reviewId, long userId) {
        String sql = "SELECT COUNT(*) FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = false";
        Integer count = jdbc.queryForObject(sql, Integer.class, reviewId, userId);
        return count != null && count > 0;
    }

    private void removeUserRating(long reviewId, long userId) {
        jdbc.update(REMOVE_LIKE_QUERY, reviewId, userId);
    }

    private void updateUsefulField(long reviewId) {
        int likesCount = getLikesCount(reviewId);
        int dislikesCount = getDislikesCount(reviewId);
        int useful = likesCount - dislikesCount;

        jdbc.update(SET_USEFUL_QUERY, useful, reviewId);

        log.debug("Обновлено поле useful для отзыва ID: {} - likes: {}, dislikes: {}, useful: {}",
                 reviewId, likesCount, dislikesCount, useful);
    }

    private int getLikesCount(long reviewId) {
        Integer count = jdbc.queryForObject(GET_LIKES_COUNT_QUERY, Integer.class, reviewId);
        return count != null ? count : 0;
    }

    private int getDislikesCount(long reviewId) {
        Integer count = jdbc.queryForObject(GET_DISLIKES_COUNT_QUERY, Integer.class, reviewId);
        return count != null ? count : 0;
    }

    @Override
    public void deleteReviewsByUserId(long userId) {
        log.debug("Удаление отзывов пользователя ID: {}", userId);
        String deleteReviewLikesQuery = "DELETE FROM review_likes WHERE review_id IN " +
                "(SELECT review_id FROM reviews WHERE user_id = ?)";
        jdbc.update(deleteReviewLikesQuery, userId);
        String deleteReviewsQuery = "DELETE FROM reviews WHERE user_id = ?";
        jdbc.update(deleteReviewsQuery, userId);
    }

    @Override
    public void deleteReviewsByFilmId(long filmId) {
        log.debug("Удаление отзывов фильма ID: {}", filmId);

        String deleteReviewLikesQuery = "DELETE FROM review_likes WHERE review_id IN " +
                "(SELECT review_id FROM reviews WHERE film_id = ?)";
        jdbc.update(deleteReviewLikesQuery, filmId);
        String deleteReviewsQuery = "DELETE FROM reviews WHERE film_id = ?";
        jdbc.update(deleteReviewsQuery, filmId);
    }

    @Override
    public void deleteReviewRatingsByUserId(long userId) {
        log.debug("Удаление оценок отзывов пользователя ID: {}", userId);
        String deleteRatingsQuery = "DELETE FROM review_likes WHERE user_id = ?";
        jdbc.update(deleteRatingsQuery, userId);
    }
}