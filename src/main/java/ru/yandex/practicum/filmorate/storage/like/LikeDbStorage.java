package ru.yandex.practicum.filmorate.storage.like;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;

@Slf4j
@Repository
public class LikeDbStorage extends BaseStorage<Like> implements LikeStorage {

    public LikeDbStorage(JdbcTemplate jdbc, RowMapper<Like> mapper) {
        super(jdbc, mapper, Like.class);
    }

    private static final String PUT_LIKE_QUERY = "INSERT INTO like_list(film_id, user_id)VALUES(?,?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM like_list WHERE film_id = ? AND user_id = ?";
    private static final String GET_LIKE_BY_ID_QUERY = "SELECT * FROM like_list WHERE film_id = ? AND user_id = ?";
    private static final String GET_LIKELIST_BY_FILM_ID = "SELECT * FROM like_list WHERE film_id = ?";
    private static final String DELETE_ALL_LIKES_FOR_FILM_QUERY = "DELETE FROM like_list WHERE film_id = ?";
    private static final String DELETE_ALL_LIKES_FOR_USER_QUERY = "DELETE FROM like_list WHERE user_id = ?";

    @Override
    public void putLike(long userId, long filmId) {
        log.debug("putLike userId {}, filmId {}", userId, filmId);
        jdbc.update(PUT_LIKE_QUERY, filmId, userId);
    }

    @Override
    public boolean containsLike(long userId, long filmId) {
        log.debug("containsLike userId {}, filmId {}", userId, filmId);
        return findOne(GET_LIKE_BY_ID_QUERY, filmId, userId).isPresent();
    }

    public void deleteLike(long filmId, long userId) {
        log.debug("deleteLike filmId {}, userId {}", filmId, userId);
        jdbc.update(DELETE_LIKE_QUERY, filmId, userId);
    }

    public List<Like> getLikeListsByFilmId(long filmId) {
        log.debug("getLikeListsByFilmId filmId {}", filmId);
        return findMany(GET_LIKELIST_BY_FILM_ID, filmId);
    }

    @Override
    public void deleteAllLikesForFilm(long filmId) {
        log.debug("deleteAllLikesForFilm filmId {}", filmId);
        // ЗАМЕНА: используем jdbc.update вместо BaseStorage.update
        jdbc.update(DELETE_ALL_LIKES_FOR_FILM_QUERY, filmId);
    }

    @Override
    public void deleteAllLikesForUser(long userId) {
        log.debug("deleteAllLikesForUser userId {}", userId);
        // ЗАМЕНА: используем jdbc.update вместо BaseStorage.update
        jdbc.update(DELETE_ALL_LIKES_FOR_USER_QUERY, userId);
    }
}
