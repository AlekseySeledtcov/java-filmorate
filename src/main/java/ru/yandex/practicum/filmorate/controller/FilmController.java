package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("films")
@Slf4j
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    FilmValidator fv = new FilmValidator();

    @PostMapping
    public Film create(@RequestBody Film film) {
        try {
            fv.validate(film);
        } catch (ValidationException exception) {
            log.warn("Ошибка валидации ", exception);
            throw new ValidationException("Ошибка валидации");
        }

        film.setId(getNextId());
        log.info("Создание фильма с названием {} id {}", film.getName(), film.getId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        if (film.getId() == null) {
            log.warn("Не указан ID фильма");
            throw new ValidationException("Не указан ID фильма");
        }
        try {
            fv.validate(film);
        } catch (ValidationException exception) {
            log.warn("ошибка валидации ", exception);
            throw new ValidationException("Ошибка валидации");
        }

        Film oldFilm = films.get(film.getId());
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

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
