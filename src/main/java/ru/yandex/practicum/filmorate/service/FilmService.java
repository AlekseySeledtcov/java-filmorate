package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundFilmException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundUserByIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeListComparator likeListComparator;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage, LikeListComparator likeListComparator) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeListComparator = likeListComparator;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        if (!filmStorage.containsFilm(film.getId())) {
            throw new NotFoundFilmException("Не найден фильм в методе updateFilm по id ", film.getId());
        }
        Film oldFilm = filmStorage.getFilm(film.getId());
        log.info("Обновление фильма с названием {} id {}", oldFilm.getName(), film.getId());
        if (film.getName() != null) {
            oldFilm.setName(film.getName());
        }
        if (film.getDescription() != null) {
            oldFilm.setDescription(film.getDescription());
        }
        if (film.getReleaseDate() != null) {
            oldFilm.setReleaseDate(film.getReleaseDate());
        }
        if (film.getDuration() != null) {
            oldFilm.setDuration(film.getDuration());
        }
        return oldFilm;
    }

    public List<Film> getFilmsList() {
        return filmStorage.getFilmsList();
    }

    public Film putLikeToFilm(long id, long userId) {
        if (!filmStorage.containsFilm(id)) {
            throw new NotFoundFilmException("Не найден фильм в методе putLikeToFilm по id ", id);
        }
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundUserByIdException("Не найден пользователь в методе putLikeToFilm по userId ", userId);
        }
        Film film = filmStorage.getFilm(id);
        film.updateFilmLikeList(userId);
        return filmStorage.updateFilm(film);
    }

    public Film deleteLiketoFilm(long id, long userId) {
        if (!filmStorage.containsFilm(id)) {
            throw new NotFoundFilmException("Не найден фильм в методе deleteLiketoFilm по id ", id);
        }
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundUserByIdException("Не найден пользователь в методе deleteLiketoFilm по userId ", userId);
        }
        Film film = filmStorage.getFilm(id);
        film.getLikeLIst().remove(userId);
        return filmStorage.updateFilm(film);
    }

    public List<Film> getPopularFilmsList(Long count) {
        return filmStorage.getFilmsList().stream().limit(count).sorted(likeListComparator.reversed()).toList();
    }
}
