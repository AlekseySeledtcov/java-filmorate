package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    @Autowired
    @Qualifier("FilmDbStorage")
    private final FilmStorage filmStorage;
    @Qualifier("UserDbStorage")
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final LikeStorage likeStorage;

    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       @Qualifier("UserDbStorage") UserStorage userStorage,
                       MpaStorage mpaStorage,
                       GenreStorage genreStorage,
                       LikeStorage likeStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.likeStorage = likeStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film updateFilm(Film film) {

        if (!filmStorage.containsFilmById(film.getId())) {
            throw new NotFoundFilmException("Не найден фильм в методе updateFilm по id ", film.getId());
        }

        return filmStorage.updateFilm(film);
    }

    public Film putLikeToFilm(long filmId, long userId) {
        if (!filmStorage.containsFilmById(filmId)) {
            throw new NotFoundFilmException("Не найден фильм в методе putLikeToFilm по filmId ", filmId);
        }
        if (!userStorage.containsUserById(userId)) {
            throw new NotFoundUserByIdException("Не найден пользователь в методе putLikeToFilm по userId ", userId);
        }
        if (likeStorage.containsLike(userId, filmId)) {
            throw new DuplicatedDataException("Этим пользователем лайк уже поставлен");
        }

        likeStorage.putLike(userId, filmId);

        return filmStorage.getFilm(filmId);
    }

    public Film deleteLiketoFilm(long filmId, long userId) {
        if (!filmStorage.containsFilmById(filmId)) {
            throw new NotFoundFilmException("Не найден фильм в методе deleteLiketoFilm по filmId ", filmId);
        }
        if (!userStorage.containsUserById(userId)) {
            throw new NotFoundUserByIdException("Не найден пользователь в методе deleteLiketoFilm по userId ", userId);
        }
        likeStorage.deleteLike(filmId, userId);
        return filmStorage.getFilm(filmId);
    }

    public List<Film> getPopularFilmList(int count) {
        return filmStorage.getPopularFilmList(count).stream()
                .collect(Collectors.toList());
    }

    public Film getFilmWithGenreById(long genreId) {
        return filmStorage.getFilm(genreId);
    }
}


