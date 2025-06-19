package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping
    public List<Film> getFilmsList() {
        return filmService.getFilmsList();
    }

    @PutMapping("/{id}/like/{userId}")
    public Film putLikeToFilm(@PathVariable("id") long id, @PathVariable("userId") long userId) {
        return filmService.putLikeToFilm(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public Film deleteLiketoFilm(@PathVariable("id") long id, @PathVariable("userId") long userId) {
        return filmService.deleteLiketoFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilmsList(@RequestParam(value = "count", defaultValue = "10") Long count) {
        return filmService.getPopularFilmsList(count);
    }
}
