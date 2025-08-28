package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
    private final GenreService genreService;
    private final DirectorService directorService;
    private final ReviewStorage reviewStorage;
    private final JdbcTemplate jdbc;
    private final EventService eventService;

    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       @Qualifier("UserDbStorage") UserStorage userStorage,
                       MpaStorage mpaStorage,
                       GenreStorage genreStorage,
                       LikeStorage likeStorage,
                       GenreService genreService,
                       DirectorService directorService,
                       ReviewStorage reviewStorage,
                       JdbcTemplate jdbc,
                       EventService eventService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.likeStorage = likeStorage;
        this.genreService = genreService;
        this.directorService = directorService;
        this.reviewStorage = reviewStorage;
        this.jdbc = jdbc;
        this.eventService = eventService;
    }

    public Film addFilm(Film film) {
        film = filmStorage.addFilm(film);

        if (!film.getGenres().isEmpty()) {
            genreService.putGenre(film.getGenres(), film.getId());
        }
        if (!film.getDirectors().isEmpty()) {
            directorService.putDirectorsToFilm(film.getDirectors(), film.getId());
        }
        return film;
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film updateFilm(Film film) {

        if (!filmStorage.containsFilmById(film.getId())) {
            throw new NotFoundFilmException("Не найден фильм в методе updateFilm по id ", film.getId());
        }

        if (!film.getGenres().isEmpty()) {
            genreService.deleteGenre(film.getId());
            genreService.putGenre(film.getGenres(), film.getId());
        }

        if (!film.getDirectors().isEmpty()) {
            directorService.deleteDirectorsFromFilm(film.getId());
            directorService.putDirectorsToFilm(film.getDirectors(), film.getId());
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
        eventService.addEvent(Event.builder()
                .userId(userId)
                .entityId(filmId)
                .eventType(EventType.LIKE)
                .operation(Operation.ADD)
                .build());
        log.debug("Событие добавлено: userId={}, entityId={}, eventType={}, operation={}",
                userId, filmId, EventType.LIKE, Operation.ADD);

        Film film = filmStorage.getFilm(filmId).orElseThrow(() -> {
            log.warn("FilmService, putLikeToFilm, фильм с id {} не найден", filmId);
            throw new NotFoundFilmException(String.format("Фильм с id %d не найден", filmId), filmId);
        });

        return addData(film);
    }

    public Film deleteLikeToFilm(long filmId, long userId) {
        if (!filmStorage.containsFilmById(filmId)) {
            throw new NotFoundFilmException("Не найден фильм в методе deleteLiketoFilm по filmId ", filmId);
        }
        if (!userStorage.containsUserById(userId)) {
            throw new NotFoundUserByIdException("Не найден пользователь в методе deleteLiketoFilm по userId ", userId);
        }
        likeStorage.deleteLike(filmId, userId);
        log.debug("Удалён лайк от пользователя с id {} к фильму с id {}", userId, filmId);
        Event event = Event.builder()
                .userId(userId)
                .entityId(filmId)
                .eventType(EventType.LIKE)
                .operation(Operation.REMOVE)
                .build();
        log.debug("Добавление события в ленту: {}", event);
        eventService.addEvent(event);
        Film film = filmStorage.getFilm(filmId).orElseThrow(() -> {
            log.warn("FilmService, deleteLikeToFilm, фильм с id {} не найден", filmId);
            throw new NotFoundFilmException(String.format("Фильм с id %d не найден", filmId), filmId);
        });
        return addData(film);
    }

    public List<Film> getPopularFilmList(int count) {
        log.debug("Получение {} популярных фильмов", count);
        List<Film> films = filmStorage.getPopularFilmList(count);
        for (Film film : films) {
            film.setLikesCount(filmStorage.getLikeListsByFilmId(film.getId()));
        }
        return films.stream()
                .sorted(Comparator.comparing(Film::getLikesCount).reversed())
                .toList();
    }

    public Film getFilmWithGenreById(long id) {
        Film film = filmStorage.getFilm(id).orElseThrow(() -> {
            log.warn("FilmService, getFilmWithGenreById, фильм с id {} не найден", id);
            throw new NotFoundFilmException(String.format("Фильм с id %d не найден", id), id);
        });
        return addData(film);
    }

    public List<Film> getFilmsByDirectorSorted(long directorId, String sortedBy) {
        List<Film> films = filmStorage.getFilmsByDirectorSorted(directorId, sortedBy);
        films.forEach(this::addData);
        return films;
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        log.debug("FilmService. Получение общих фильмов для userId={} и friendId={}", userId, friendId);

        if (!userStorage.containsUserById(userId)) {
            throw new NotFoundUserByIdException("Пользователь не найден", userId);
        }
        if (!userStorage.containsUserById(friendId)) {
            throw new NotFoundUserByIdException("Друг не найден", friendId);
        }

        List<Film> commonFilms = filmStorage.getCommonFilms(userId, friendId);

        commonFilms.forEach(this::addData);

        commonFilms.sort(Comparator.comparingLong(Film::getLikesCount).reversed());

        return commonFilms;
    }

    public List<Film> getFilmsSearch(String query, String by) {

        List<Film> films = new ArrayList<>();
        String[] byArr = by.replaceAll("\\s+", "").split(",");

        for (String s : byArr) {
            if (s.equals("director")) {
                films.addAll(filmStorage.getFilmsSearchByDirector("%" + query.toLowerCase() + "%"));
            }
            if (s.equals("title")) {
                films.addAll(filmStorage.getFilmsSearchByTitle("%" + query.toLowerCase() + "%"));
            }
        }

        if (!films.isEmpty()) {
            films.forEach(this::addData);
        }

        return films.stream()
                .distinct()
                .sorted(Comparator.comparing(Film::getLikesCount))
                .toList();
    }

    public void deleteFilm(long id) {
        log.debug("Сервис. deleteFilm Удаление фильма с id {}", id);

        if (!filmStorage.containsFilmById(id)) {
            throw new NotFoundFilmException("Не найден фильм для удаления по id ", id);
        }

        likeStorage.deleteAllLikesForFilm(id);
        genreService.deleteGenre(id);
        directorService.deleteDirectorsFromFilm(id);
        reviewStorage.deleteReviewsByFilmId(id);

        boolean wasDeleted = filmStorage.deleteFilm(id);
        if (!wasDeleted) {
            throw new NotFoundFilmException("Не удалось удалить фильм по id ", id);
        }
        log.debug("Фильм с id {} успешно удален: {}", id, wasDeleted);
    }

    private Film addData(Film film) {
        film.setGenres(genreService.getGenresByFilmId(film.getId()));
        film.setDirectors(directorService.getDirectorsByFilmId(film.getId()));
        film.setLikesCount(filmStorage.getLikeListsByFilmId(film.getId()));
        return film;
    }
}


