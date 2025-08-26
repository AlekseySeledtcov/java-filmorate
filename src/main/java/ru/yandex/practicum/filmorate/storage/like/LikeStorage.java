package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;

public interface LikeStorage {

    void putLike(long userId, long filmId);

    void deleteLike(long userId, long filmId);

    boolean containsLike(long userId, long filmId);

    List<Like> getLikeListsByFilmId(long filmId);

    void deleteAllLikesForFilm(long filmId);

    void deleteAllLikesForUser(long userId);
}
