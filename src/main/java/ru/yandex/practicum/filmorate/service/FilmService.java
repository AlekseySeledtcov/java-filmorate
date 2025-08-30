package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
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
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;
    private final GenreService genreService;
    private final DirectorService directorService;
    private final ReviewStorage reviewStorage;
    private final EventService eventService;

    public FilmService(FilmStorage filmStorage,
                       UserStorage userStorage,
                       MpaStorage mpaStorage,
                       GenreStorage genreStorage,
                       LikeStorage likeStorage,
                       GenreService genreService,
                       DirectorService directorService,
                       ReviewStorage reviewStorage,
                       EventService eventService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeStorage = likeStorage;
        this.genreService = genreService;
        this.directorService = directorService;
        this.reviewStorage = reviewStorage;
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
        List<Film> films = filmStorage.getFilms();
        films.forEach(this::addData);
        return films;
    }

    public Film updateFilm(Film film) {

        film.setGenres(genreValidator(film.getGenres()));

        if (!filmStorage.containsFilmById(film.getId())) {
            throw new EntityNotFoundException("Не найден фильм в методе updateFilm по id ", film.getId());
        }

        if (!film.getGenres().isEmpty()) {
            genreService.deleteGenre(film.getId());
            genreService.putGenre(film.getGenres(), film.getId());
        } else {
            genreService.deleteGenre(film.getId());
        }

        if (!film.getDirectors().isEmpty()) {
            directorService.deleteDirectorsFromFilm(film.getId());
            directorService.putDirectorsToFilm(film.getDirectors(), film.getId());
        } else {
            directorService.deleteDirectorsFromFilm(film.getId());
        }
        return filmStorage.updateFilm(film);
    }

    public Film putLikeToFilm(long filmId, long userId) {
        if (!filmStorage.containsFilmById(filmId)) {
            throw new EntityNotFoundException("Не найден фильм в методе putLikeToFilm по filmId ", filmId);
        }
        if (!userStorage.containsUserById(userId)) {
            throw new EntityNotFoundException("Не найден пользователь в методе putLikeToFilm по userId ", userId);
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
            log.warn("putLikeToFilm, фильм с id {} не найден", filmId);
            return new EntityNotFoundException(String.format("Фильм с id %d не найден", filmId), filmId);
        });

        return addData(film);
    }

    public Film deleteLikeToFilm(long filmId, long userId) {
        if (!filmStorage.containsFilmById(filmId)) {
            throw new EntityNotFoundException("Не найден фильм в методе deleteLiketoFilm по filmId ", filmId);
        }
        if (!userStorage.containsUserById(userId)) {
            throw new EntityNotFoundException("Не найден пользователь в методе deleteLiketoFilm по userId ", userId);
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
            log.warn("deleteLikeToFilm, фильм с id {} не найден", filmId);
            return new EntityNotFoundException(String.format("Фильм с id %d не найден", filmId), filmId);
        });
        return addData(film);
    }

    public List<Film> getPopularFilms(int limit, Integer genreId, Integer year) {
        log.debug("getPopularFilms. limit = {}, genreId = {}, year={}", limit, genreId, year);

        List<Film> films = filmStorage.getPopularFilms(year);
        films.forEach(this::addData);
        return films.stream()
                .distinct()
                .filter(film -> genreId == null ||
                        (film.getGenres() != null &&
                                film.getGenres().stream().anyMatch(genre -> genre.getId().equals(genreId))))
                .sorted(Comparator.comparing(Film::getLikesCount, Comparator.reverseOrder()))
                .limit(limit)
                .toList();
    }

    public Film getFilmWithGenreById(long id) {
        Film film = filmStorage.getFilm(id).orElseThrow(() -> {
            log.warn("getFilmWithGenreById, фильм с id {} не найден", id);
            return new EntityNotFoundException(String.format("Фильм с id %d не найден", id), id);
        });
        return addData(film);
    }

    public List<Film> getFilmsByDirectorSorted(long directorId, String sortedBy) {
        List<Film> films = filmStorage.getFilmsByDirectorSorted(directorId, sortedBy);
        if (films == null || films.isEmpty()) {
            throw new EntityNotFoundException(String.format("Фильм с id директора %d не найден", directorId), directorId);
        }
        films.forEach(this::addData);
        return films;
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        log.debug("Получение общих фильмов для userId={} и friendId={}", userId, friendId);

        if (!userStorage.containsUserById(userId)) {
            throw new EntityNotFoundException("Пользователь не найден", userId);
        }
        if (!userStorage.containsUserById(friendId)) {
            throw new EntityNotFoundException("Друг не найден", friendId);
        }

        List<Film> commonFilms = filmStorage.getCommonFilms(userId, friendId);

        commonFilms.forEach(this::addData);

        commonFilms.sort(Comparator.comparingLong(Film::getLikesCount).reversed());

        return commonFilms;
    }

    public List<Film> getFilmsSearch(String query, String by) {

        List<Film> films = new ArrayList<>();
        String[] byArr = by.replaceAll("\\s+", "").split(",");

        for (String string : byArr) {
            if (string.equals("director")) {
                films.addAll(filmStorage.getFilmsSearchByDirector("%" + query.toLowerCase() + "%"));
            }
            if (string.equals("title")) {
                films.addAll(filmStorage.getFilmsSearchByTitle("%" + query.toLowerCase() + "%"));
            }
        }

        if (!films.isEmpty()) {
            films.forEach(this::addData);
        }

        return films.stream()
                .distinct()
                .sorted(Comparator.comparing(Film::getLikesCount).reversed())
                .toList();
    }

    public void deleteFilm(long id) {
        log.debug("deleteFilm Удаление фильма с id {}", id);

        if (!filmStorage.containsFilmById(id)) {
            throw new EntityNotFoundException("Не найден фильм для удаления по id ", id);
        }

        likeStorage.deleteAllLikesForFilm(id);
        genreService.deleteGenre(id);
        directorService.deleteDirectorsFromFilm(id);
        reviewStorage.deleteReviewsByFilmId(id);

        boolean wasDeleted = filmStorage.deleteFilm(id);
        if (!wasDeleted) {
            throw new EntityNotFoundException("Не удалось удалить фильм по id ", id);
        }
        log.debug("Фильм с id {} успешно удален: {}", id, wasDeleted);
    }

    public Film addData(Film film) {
        film.setGenres(genreService.getGenresByFilmId(film.getId()));
        film.setDirectors(directorService.getDirectorsByFilmId(film.getId()));
        film.setLikesCount(filmStorage.getLikeListsByFilmId(film.getId()));
        return film;
    }

    private List<Genre> genreValidator(List<Genre> genre) {
        return genre.stream()
                .distinct()
                .toList();
    }
}


