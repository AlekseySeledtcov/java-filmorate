package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
        log.debug("FilmController. Запрос на добавление фильма {}", film.getName());
        return filmService.addFilm(film);
    }

    @GetMapping
    public List<Film> getFilms() {
        log.debug("FilmController. Запрос на список фильмов");
        return filmService.getFilms();
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.debug("FilmController. Запрос на обновление фильма c id {}", film.getId());
        return filmService.updateFilm(film);
    }

    @PutMapping("/{film_id}/like/{id}")
    public Film putLikeToFilm(@PathVariable("film_id") long filmId, @PathVariable("id") long userId) {
        log.debug("FilmController. Запрос на добавление лайка фильму с id {} от пользователя с id {}", filmId, userId);
        return filmService.putLikeToFilm(filmId, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public Film deleteLiketoFilm(@PathVariable("id") long id, @PathVariable("userId") long userId) {
        log.debug("FilmController. Запрос на удаление лайка");
        return filmService.deleteLikeToFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilmList(@RequestParam(defaultValue = "10") @Positive int count) {
        log.debug("FilmController. getPopularFilmList count={}", count);
        return filmService.getPopularFilmList(count);
    }

    @GetMapping("/{id}")
    public Film getFilmWithGenreById(@PathVariable("id") long id) {
        log.debug("FilmController. getFilmWithGenreById id={}", id);
        return filmService.getFilmWithGenreById(id);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirectorSorted(@PathVariable("directorId") long directorId,
                                               @RequestParam String sortBy) {
        return filmService.getFilmsByDirectorSorted(directorId, sortBy);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable long filmId) {
        log.debug("Контроллер. Удаление фильма с id {}", filmId);
        filmService.deleteFilm(filmId);
    }
}
