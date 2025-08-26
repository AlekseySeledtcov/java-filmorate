package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CommonFilmsController {

    private final FilmService filmService;

    @GetMapping("/films/common")
    public List<Film> getCommonFilms(
            @RequestParam long userId,
            @RequestParam long friendId) {

        log.debug("GET /films/common?userId={}&friendId={} - получение общих фильмов", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }
}