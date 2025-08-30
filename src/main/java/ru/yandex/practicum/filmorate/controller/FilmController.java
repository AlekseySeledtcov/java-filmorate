package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.debug("Запрос на добавление фильма {}", film.getName());
        return filmService.addFilm(film);
    }

    @GetMapping
    public List<Film> getFilms() {
        log.debug("Запрос на список фильмов");
        return filmService.getFilms();
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.debug("Запрос на обновление фильма c id {}", film.getId());
        return filmService.updateFilm(film);
    }

    @PutMapping("/{film_id}/like/{id}")
    public Film putLikeToFilm(@PathVariable("film_id") long filmId, @PathVariable("id") long userId) {
        log.debug("Запрос на добавление лайка фильму с id {} от пользователя с id {}", filmId, userId);
        return filmService.putLikeToFilm(filmId, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public Film deleteLiketoFilm(@PathVariable("id") long id, @PathVariable("userId") long userId) {
        log.debug("Запрос на удаление лайка");
        return filmService.deleteLikeToFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @RequestParam(value = "count", defaultValue = "10") int limit,
            @RequestParam(value = "genreId", required = false) Integer genreId,
            @RequestParam(value = "year", required = false) Integer year) {
        log.debug("getPopularFilms. limit={}, genreId={}, year={}", limit, genreId, year);
        return filmService.getPopularFilms(limit, genreId, year);
    }

    @GetMapping("/{id}")
    public Film getFilmWithGenreById(@PathVariable("id") long id) {
        log.debug("getFilmWithGenreById id={}", id);
        return filmService.getFilmWithGenreById(id);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirectorSorted(@PathVariable("directorId") long directorId,
                                               @RequestParam String sortBy) {
        return filmService.getFilmsByDirectorSorted(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> getFilmsSearch(@RequestParam("query") String query,
                                     @RequestParam("by") String by) {
        log.debug("getFilmsSearch query={}, by={}", query, by);
        return filmService.getFilmsSearch(query, by);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable long filmId) {
        log.debug("Удаление фильма с id {}", filmId);
        filmService.deleteFilm(filmId);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(
            @RequestParam long userId,
            @RequestParam long friendId) {

        log.debug("GET /films/common?userId={}&friendId={} - получение общих фильмов", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }
}
