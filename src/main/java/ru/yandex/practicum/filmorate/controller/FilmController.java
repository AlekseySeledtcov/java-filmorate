package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("films")
@Slf4j
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        film.setId(getNextId());
        log.info("Создание фильма с названием {} id {}", film.getName(), film.getId());
        films.put(film.getId(), film);
        return film;
    }


    @PutMapping
    public Film update(@RequestBody Film film) {

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
    public List<Film> getFilms() {
        return films.values().stream().toList();
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
