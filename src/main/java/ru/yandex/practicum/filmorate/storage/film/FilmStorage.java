package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    Optional<Film> getFilm(long id);

    List<Film> getFilms();

    boolean containsFilmById(long id);

    boolean containsFilmByName(String name);

    List<Film> getPopularFilmList(int count);

    List<Film> getFilmsLikedByUser(long userId);


    List<Film> getFilmsByDirectorSorted(long directorId, String sortedBy);

    long getLikeListsByFilmId(long id);
}
