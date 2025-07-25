package ru.yandex.practicum.filmorate.storage.likeList;

import ru.yandex.practicum.filmorate.model.LikeList;

import java.util.List;

public interface LikeListStorage {

    void putLike(long userId, long filmId);

    void deleteLike(long userId, long filmId);

    boolean containsLike(long userId, long filmId);

    List<LikeList> getLikeListsByFilmId(long filmId);
}
