package ru.yandex.practicum.filmorate.storage.likeList;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.LikeList;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;

@Slf4j
@Repository
public class LikeListDbStorage extends BaseStorage implements LikeListStorage {

    public LikeListDbStorage(JdbcTemplate jdbc, RowMapper<LikeList> mapper) {
        super(jdbc, mapper, LikeList.class);
    }

    private static final String PUT_LIKE_QUERY = "INSERT INTO like_list(film_id, user_id)VALUES(?,?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM like_list WHERE film_id = ? AND user_id = ?";
    private static final String GET_LIKE_BY_ID_QUERY = "SELECT * FROM like_list WHERE film_id = ? AND user_id = ?";
    private static final String GET_LIKELIST_BY_FILM_ID = "SELECT * FROM like_list WHERE film_id = ?";

    @Override
    public void putLike(long userId, long filmId) {
        log.debug("Хранилище. putLike userId {}, filmId {}", userId, filmId);
        update(PUT_LIKE_QUERY, filmId, userId);
    }

    @Override
    public boolean containsLike(long userId, long filmId) {
        log.debug("Хранилище. containsLike userId {}, filmId {}", userId, filmId);
        return findOne(GET_LIKE_BY_ID_QUERY, filmId, userId).isPresent();
    }

    public void deleteLike(long filmId, long userId) {
        log.debug("Хранилище. deleteLike filmId {}, userId {}", filmId, userId);
        update(DELETE_LIKE_QUERY, filmId, userId);
    }

    public List<LikeList> getLikeListsByFilmId(long filmId) {
        log.debug("Хранилище. getLikeListsByFilmId filmId {}", filmId);
//        System.out.println("LikeList для film id = " + filmId);
//        System.out.println(findMany(GET_LIKELIST_BY_FILM_ID, filmId));
        return findMany(GET_LIKELIST_BY_FILM_ID, filmId);
    }
}
