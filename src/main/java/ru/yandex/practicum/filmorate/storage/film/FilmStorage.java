package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film deleteFilm(Film film);

    Film updateFilm(Film film);

    Film getFilm(long id);

    List<Film> getFilmsList();

    boolean containsFilm(long id);
}
