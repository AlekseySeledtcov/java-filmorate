package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilm(long id);

    List<Film> getFilms();

    boolean containsFilmById(long id);

    boolean containsFilmByName(String name);

    List<Film> getPopularFilmList(int count);
}
